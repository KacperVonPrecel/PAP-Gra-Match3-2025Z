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
	private static readonly SWAP_DURATION = 150;
	swapAttempt = output<MoveRequest>();
	moveValid = input<boolean | null>(); //null because there can be no move happening
	private lastMove: MoveRequest | null = null;

	constructor() {
		effect(() => {
			const _moveValid = this.moveValid();
			if (_moveValid === null) return;
			if (this.lastMove === null) return;
			if (_moveValid === false) {
				//if sth went wrong, swap back
				this.animateSwapBack(this.lastMove);
				this.lastMove = null;
				this.allowSwapping = true;
			}
			if (_moveValid === true) {
				//we will need to store the old board and run animation loop
			}
		});
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
	getCrystalImage(crystal: Crystal) {
		return this.crystalImages.get(crystal.crystalType);
	}

	get assetsLoaded(): boolean {
		return this.imagesLoadedCount == Object.values(CrystalType).length;
	}

	onPointerDown(event: PointerEvent, row_idx: number, column_idx: number) {
		this.dragStart = { row: row_idx, column: column_idx };
		this.startX = event.clientX;
		this.startY = event.clientY;
	}

	onPointerMove(event: PointerEvent) {
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

	onPointerUp() {
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

	animateSwap(move: MoveRequest) {
		const sourceSwapDirection = this.getSwapDirection(move);
		const targetSwapDirection = this.getSwapDirection(this.getReverseMove(move));
		const sourceKey = `${move.source.row},${move.source.column}`;
		const targetKey = `${move.target.row},${move.target.column}`;
		this.swapAnimations.set(sourceKey, `${sourceSwapDirection}`);
		this.swapAnimations.set(targetKey, `${targetSwapDirection}`);
	}

	animateSwapBack(move: MoveRequest) {
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
			return false;
		}, GameBoard.SWAP_DURATION);
	}

	handleSwapAttempt(target: { row: number; column: number }): void {
		const moveRequest: MoveRequest = {
			source: { row: this.dragStart!.row, column: this.dragStart!.column },
			target: { row: target.row, column: target.column }
		};

		if (this.allowSwapping) {
			this.lastMove = moveRequest;
			this.animateSwap(moveRequest);
			this.allowSwapping = false;
			if (this.isMoveValid(moveRequest)) {
				this.emitSwapAttempt(moveRequest);
			} else {
				this.animateSwapBack(moveRequest);
				this.allowSwapping = true;
			}
		}
	}

	emitSwapAttempt(move: MoveRequest) {
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
}
