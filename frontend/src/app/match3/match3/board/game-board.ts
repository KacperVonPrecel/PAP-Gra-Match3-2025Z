import { Component, effect, input, output } from '@angular/core';
import { BoardState, Crystal, Position } from '../../game-state';
import { CrystalType, getCrystalFileName } from '../../match3-service';
import { MoveRequest } from '../../match3-service';

@Component({
	selector: 'app-board',
	imports: [],
	templateUrl: './game-board.html',
	styleUrl: './game-board.scss'
})
export class GameBoard {
	CrystalType = CrystalType; // to expose the enum to the template
	state = input.required<BoardState | null>();
	private crystalImages = new Map<CrystalType, string>();
	private imagesLoadedCount: number = 0; //value to ensure all images of crystals are loaded before displaying the board
	private dragStart: Position | null = null;
	private startX = 0;
	private startY = 0;
	private dragThreshold = 20; //how many pixels need to be moved before its dragged
	//map to track which animation classes should be assigned in css
	private swapAnimations = new Map<string, string>(); //key - "row, column", value -> what animation (eg. "swap-up")
	private allowSwapping: boolean = true;
	swapAttempt = output<MoveRequest>();
	moveValid = input<boolean | null>(); //null because there can be no move happening
	private lastMove: MoveRequest | null = null;
	private _oldBoard: Crystal[][] | null = null; //old board saved for animations
	private _animationsPlaying: boolean = false;

	private static readonly SWAP_DURATION = 150;

	constructor() {
		effect(() => {
			const _moveValid = this.moveValid();
			if (_moveValid === null) return;
			if (this.lastMove === null) return;
			if (_moveValid === false) {
				//if sth went wrong, swap back
				this.animateSwapBack(this.lastMove);
				this.allowSwapping = true;
			}
			if (_moveValid === true) {
				this._animationsPlaying = true;
				this.animationSequence();
				this._animationsPlaying = false;
				this.allowSwapping = true;
			}
			this.lastMove = null;
			return;
		});
	}

	get animationsPlaying(): boolean {
		return this._animationsPlaying;
	}

	get oldBoard(): Crystal[][] {
		if (this._oldBoard) {
			return this._oldBoard;
		}
		//XXXW probably handle that better even tho it wont likely happen
		return [];
	}

	loadCrystalAssets(): void {
		//loop over all types in crystal type
		for (const type of Object.values(CrystalType)) {
			const url = getCrystalFileName(type);
			const img = new Image();
			img.src = url;
			img.onload = () => {
				this.imagesLoadedCount++;
			};
			this.crystalImages.set(type, url);
		}
	}

	ngOnInit() {
		this.loadCrystalAssets();
	}
	getCrystalImage(crystal: Crystal): string | undefined {
		return this.crystalImages.get(crystal.crystalType);
	}

	get assetsLoaded(): boolean {
		return this.imagesLoadedCount == Object.values(CrystalType).length;
	}

	onPointerDown(event: PointerEvent, row_idx: number, column_idx: number): void {
		this.dragStart = { row: row_idx, column: column_idx };
		this.startX = event.clientX;
		this.startY = event.clientY;
	}

	onPointerMove(event: PointerEvent): void {
		if (!this.dragStart) return;

		const dx = event.clientX - this.startX;
		const dy = event.clientY - this.startY;
		//Didnt surpass threshold in any direction
		if (Math.abs(dx) < this.dragThreshold && Math.abs(dy) < this.dragThreshold) {
			return;
		}

		let target: { row: number; column: number } | null = null;

		// Horizontal drag
		if (Math.abs(dx) > Math.abs(dy)) {
			target =
				dx > 0
					? { row: this.dragStart.row, column: this.dragStart.column + 1 } ///position of drag was to the right
					: { row: this.dragStart.row, column: this.dragStart.column - 1 }; //position of drag was to the left
		}
		// Vertical drag
		else {
			target =
				dy > 0
					? { row: this.dragStart.row + 1, column: this.dragStart.column } // down
					: { row: this.dragStart.row - 1, column: this.dragStart.column }; // up
		}

		if (!target) return;

		this.handleSwapAttempt(target);
		//If any drag direction was detected, we cant start dragging again until we click on a crystal again
		this.dragStart = null;
	}

	onPointerUp(): void {
		this.dragStart = null;
	}

	private positionsEqual(a: Position, b: Position): boolean {
		return a.row === b.row && a.column === b.column;
	}

	private movesEqal(a: MoveRequest, b: MoveRequest): boolean {
		return this.positionsEqual(a.source, b.source) && this.positionsEqual(a.target, b.target);
	}

	private getReverseMove(a: MoveRequest): MoveRequest {
		return { source: a.target, target: a.source };
	}

	isMoveValid(move: MoveRequest): boolean {
		const allowed = this.state()?.allowedMoves;
		if (!allowed) return false;
		const reversedMove = this.getReverseMove(move);
		return allowed.some((m) => this.movesEqal(m, move)) || allowed.some((m) => this.movesEqal(m, reversedMove));
	}

	animateSwap(move: MoveRequest): void {
		const sourceSwapDirection = this.getSwapDirection(move);
		const targetSwapDirection = this.getSwapDirection(this.getReverseMove(move));
		const sourceKey = `${move.source.row},${move.source.column}`;
		const targetKey = `${move.target.row},${move.target.column}`;
		this.swapAnimations.set(sourceKey, `${sourceSwapDirection}`);
		this.swapAnimations.set(targetKey, `${targetSwapDirection}`);
	}

	animateSwapBack(move: MoveRequest): void {
		const sourceKey = `${move.source.row},${move.source.column}`;
		const targetKey = `${move.target.row},${move.target.column}`;
		setTimeout(() => {
			//make sure first swap happened
			this.swapAnimations.set(sourceKey, `go-back`);
			this.swapAnimations.set(targetKey, `go-back`);
			setTimeout(() => {
				//make sure second swap happened
				this.swapAnimations.delete(sourceKey);
				this.swapAnimations.delete(targetKey);
			}, GameBoard.SWAP_DURATION);

			this.allowSwapping = true;
		}, GameBoard.SWAP_DURATION);
	}

	handleSwapAttempt(target: { row: number; column: number }): void {
		const moveRequest: MoveRequest = {
			source: { row: this.dragStart!.row, column: this.dragStart!.column },
			target: { row: target.row, column: target.column }
		};
		const targetCrystal = this.state()!.board[target.row][target.column];
		const targetType = targetCrystal.crystalType;

		const sourceCrystal = this.state()!.board[this.dragStart!.row][this.dragStart!.column];
		const sourceType = sourceCrystal.crystalType;

		if (
			targetType == CrystalType.EMPTY ||
			targetType == CrystalType.DISABLED ||
			sourceType == CrystalType.EMPTY ||
			sourceType == CrystalType.DISABLED
		) {
			return;
		}

		const numOfRows = this.state()!.board.length;
		const numOfColumns = this.state()!.board[0].length;
		//ensuring we dont play animations for swapping outside the board
		if (this.dragStart!.row < 0 || target.row < 0 || this.dragStart!.row >= numOfRows || target.row >= numOfRows) {
			return;
		}
		if (this.dragStart!.column < 0 || target.column < 0 || this.dragStart!.column >= numOfColumns || target.column >= numOfColumns) {
			return;
		}

		if (this.allowSwapping) {
			this.lastMove = moveRequest;
			this.animateSwap(moveRequest);
			this.allowSwapping = false;
			if (this.isMoveValid(moveRequest)) {
				//snapshotting the board before the move request is sent
				//deep copy of board -> otherwise its a reference and will change when the new game state is assigned
				this._oldBoard = this.state()!.board.map((row) => row.map((cell) => ({ ...cell }))); //... is object spread operator -> for copying objects
				this.emitSwapAttempt(moveRequest);
			} else {
				this.animateSwapBack(moveRequest);
				this.allowSwapping = true;
			}
		}
	}

	emitSwapAttempt(move: MoveRequest): void {
		this.swapAttempt.emit(move);
	}

	getSwapDirection(move: MoveRequest): string {
		if (move.source.row > move.target.row) {
			return 'swap-up';
		}
		if (move.target.row > move.source.row) {
			return 'swap-down';
		}
		if (move.source.column > move.target.column) {
			return 'swap-left';
		}
		if (move.target.column > move.source.column) {
			return 'swap-right';
		}
		return '';
	}

	getSwapClass(row_idx: number, column_idx: number): string {
		return this.swapAnimations.get(`${row_idx},${column_idx}`) ?? '';
	}

	animationSequence(): void {
		if (this.state()) {
			const animationSteps = this.state()!.animationSteps;
			for (const step of animationSteps) {
				//play swap if im not the player who swapped
				for (const destroyedBlock of step.destroyed) {
					//assign destroyed class
				}
			}
		}
		//play swap if im not the player who swapped
		//destroyed animation
		//falling animation
		//new blocks animation
		//new board animation if needed
		return;
	}
}
