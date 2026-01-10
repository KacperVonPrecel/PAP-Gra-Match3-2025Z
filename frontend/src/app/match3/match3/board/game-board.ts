import { Component, effect, HostBinding, input, output } from '@angular/core';
import { BoardState, Crystal, NewBlock, Position } from '../../game-state';
import { CrystalType, getCrystalFileName } from '../../match3-service';
import { MoveRequest } from '../../match3-service';
import { AnimationState } from './animation-state';
import { NgStyle } from '@angular/common';

@Component({
	selector: 'app-board',
	imports: [NgStyle],
	templateUrl: './game-board.html',
	styleUrl: './game-board.scss'
})
export class GameBoard {
	CrystalType = CrystalType; // to expose the enum to the template
	/*current board state - includes information about animation played from the previous state, to get to the current one*/
	state = input.required<BoardState | null>();
	/*did the server give a valid response to the player's attempted move*/
	moveValid = input<boolean | null>(); //null because there can be no move happening
	/*output to parent with the player's move request*/
	swapAttempt = output<MoveRequest>();
	/*player's last move request - only stored before server gives the response, stored for the purpose of animating the swap back*/
	private lastMove: MoveRequest | null = null;
	/*boolean determining if its the player's turn */
	myTurn = input<boolean>();
	/*value determining if the player can swap blocks based on what's happening on the board (eg. after swap, animations) - independant of whose turn it is*/
	private allowSwapping: boolean = true;
	private dragStart: Position | null = null;
	private startX = 0;
	private startY = 0;
	/*threshold of how many pixels need to be moved before the block is considered as dragged*/
	private dragThreshold = 20;
	/*object managing maps of animation classes for animations that are currently happening*/
	animationState: AnimationState = new AnimationState();
	/*board before a move was executes - stored for the purpose of animating*/
	private _oldBoard: Crystal[][] | null = null;
	private _animationsPlaying: boolean = false;
	/*value determining whether the swap for the move in animation step was already animated*/
	private swapWasAnimated: boolean = false;
	private crystalImages = new Map<CrystalType, string>();
	/*value to ensure all images of crystals are loaded before displaying the board - should be equal to the number of types in CrystalType*/
	private imagesLoadedCount: number = 0;

	/*constants declared here not in animation state, because they are mostly used for waiting for animation to finish in this class*/
	private static readonly SWAP_DURATION = 150;
	private static readonly DESTROY_DURATION = 250;
	private static readonly NEW_DURATION = 150;
	public static readonly FALLING_ONE_BLOCK_DURATION = 250; //needs to be accessible in animation state
	private static readonly BOARD_RESET_DURATION = 150;

	/*assigning values to variables used in css, that are dependant on the constants in the component*/
	@HostBinding('style.--swap-duration') swapDuration = `${GameBoard.SWAP_DURATION}ms`;
	@HostBinding('style.--destroy-duration') destroyDuration = `${GameBoard.DESTROY_DURATION}ms`;

	constructor() {
		effect(async () => {
			const state = this.state();
			if (!state) return;
			if (!this._oldBoard) {
				//if there is no old board - that means its the first call after creation or refresh-> there will be no animations
				this._oldBoard = this.state()!.board.map((row) => row.map((cell) => ({ ...cell })));
			}
			this._animationsPlaying = true;
			await this.animationSequence();
			this.animationState.clearAllClasses();
			//snapshotting the board (as old board for the next animation) before the move request is sent
			this._oldBoard = this.state()!.board.map((row) => row.map((cell) => ({ ...cell }))); //... is object spread operator -> for copying objects
			this._animationsPlaying = false;
		});
		() => {
			const _moveValid = this.moveValid();

			if (_moveValid === null) return;
			if (this.lastMove === null) return;
			if (_moveValid === false) {
				//if sth went wrong, swap back
				this.animateSwapBack(this.lastMove);
				this.allowSwapping = true;
			}
			if (_moveValid === true) {
				this.swapWasAnimated = true;
				this.allowSwapping = true;
			}
			this.lastMove = null;
			return;
		};
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
		if (this.myTurn()) {
			this.dragStart = { row: row_idx, column: column_idx };
			this.startX = event.clientX;
			this.startY = event.clientY;
		}
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
		this.animationState.addSwap(move.source.row, move.source.column, `${sourceSwapDirection}`);
		this.animationState.addSwap(move.target.row, move.target.column, `${targetSwapDirection}`);
	}

	animateSwapBack(move: MoveRequest): void {
		//we're assuming the first swap happened and ended sure
		this.animationState.addSwap(move.source.row, move.source.column, `go-back`);
		this.animationState.addSwap(move.target.row, move.target.column, `go-back`);
		setTimeout(() => {
			//make sure second swap happened
			this.animationState.deleteSwap(move.source.row, move.source.column);
			this.animationState.deleteSwap(move.target.row, move.target.column);
			this.allowSwapping = true;
		}, GameBoard.SWAP_DURATION);
	}

	handleSwapAttempt(target: { row: number; column: number }): void {
		/*plays swap animation, sends move request if the move is valid and plays swap back animation if needed*/
		const moveRequest: MoveRequest = {
			source: { row: this.dragStart!.row, column: this.dragStart!.column },
			target: { row: target.row, column: target.column }
		};
		if (!this.myTurn) {
			return;
		}
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
			setTimeout(() => {
				if (this.isMoveValid(moveRequest)) {
					this.emitSwapAttempt(moveRequest);
				} else {
					this.animateSwapBack(moveRequest);
					this.allowSwapping = true;
				}
			}, GameBoard.SWAP_DURATION);
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

	private wait(ms: number): Promise<void> {
		/*waiting for ms seconds, for use in async functions*/
		return new Promise((resolve) => setTimeout(resolve, ms));
	}

	async animationSequence(): Promise<void> {
		/*goes through all animations in every animation state, then clears the animations when a new board is to be displayed
		async to avoid cascading timeouts - we need to wait for one type of animation to finish before starting another*/
		if (this.state()) {
			const animationSteps = this.state()!.animationSteps;
			//play swap if im not the player who swapped
			for (const step of animationSteps) {
				if (step.swapped && !this.swapWasAnimated) {
					this.animateSwap(step.swapped);
					await this.wait(GameBoard.SWAP_DURATION);
				}
				if (step.swapped) {
					const sourceCrystal = this._oldBoard![step.swapped.source.row][step.swapped.source.column];
					this._oldBoard![step.swapped.source.row][step.swapped.source.column] =
						this._oldBoard![step.swapped.target.row][step.swapped.target.column];
					this._oldBoard![step.swapped.target.row][step.swapped.target.column] = sourceCrystal;
				}
				this.animationState.clearSwap();
				this.swapWasAnimated = false; //for next time - saying the swap wasnt animated yet

				for (const destroyedBlock of step.destroyed) {
					this.animationState.addDestroyed(destroyedBlock.row, destroyedBlock.column, `destroying`);
				}
				if (step.destroyed.length > 0) {
					await this.wait(GameBoard.DESTROY_DURATION);
				}
				//after animation finished remove animation class and modify the display board
				this.animationState.clearDestroy();
				for (const destroyedBlock of step.destroyed) {
					this._oldBoard![destroyedBlock.row][destroyedBlock.column] = { crystalType: CrystalType.EMPTY };
				}

				let biggestDistance = 0;
				for (const falling of step.falling) {
					let distance = falling.target.row - falling.source.row;
					if (distance > biggestDistance) {
						biggestDistance = distance;
					}
					this.animationState.addFalling(falling.source.row, falling.source.column, falling.target.row);
				}
				await this.wait(biggestDistance * GameBoard.FALLING_ONE_BLOCK_DURATION);
				//after animation finished remove animation class and modify the display board
				this.animationState.clearFallingAndNew();
				void document.body.offsetHeight;
				this.rebuildCollumnsAfterFall(step.falling);

				//grouping new blocks by column
				const newByColumn = new Map<number, Array<NewBlock>>(); //map->column, new block
				for (const newBlock of step.newBlocks) {
					if (!newByColumn.has(newBlock.position.column)) {
						newByColumn.set(newBlock.position.column, []);
					}
					newByColumn.get(newBlock.position.column)!.push(newBlock);
				}
				//going through new blocks for each column
				biggestDistance = 0;
				for (const [col, blocks] of newByColumn.entries()) {
					const maxRow = Math.max(...blocks.map((block) => block.position.row));
					for (const block of blocks) {
						const offset = -(maxRow + 1);
						const distance = maxRow + 1;
						console.log(block.position.row, distance, offset);
						if (distance > biggestDistance) {
							biggestDistance = distance;
						}
						this._oldBoard![block.position.row][block.position.column] = block.crystal;
						this.animationState.addNew(block.position.row, block.position.column, offset);
					}
				}

				await this.wait(biggestDistance * GameBoard.FALLING_ONE_BLOCK_DURATION);
				//after animation finished remove animation class and modify the display board
				this.animationState.clearFallingAndNew();
				for (const newBlock of step.newBlocks) {
					this._oldBoard![newBlock.position.row][newBlock.position.column] = newBlock.crystal;
				}
			}
		}

		//play swap if im not the player who swapped
		//destroyed animation
		//falling animation
		//new blocks animation
		//new board animation if needed
		//commit all changes (including swap) to display board and clear classes
		return;
	}

	rebuildCollumnsAfterFall(falling: MoveRequest[]) {
		const fallsByColumn = new Map<number, Array<{ source: number; target: number }>>();
		//group all falls by column
		for (const f of falling) {
			if (!fallsByColumn.has(f.source.column)) {
				fallsByColumn.set(f.source.column, []);
			}
			fallsByColumn.get(f.source.column)!.push({ source: f.source.row, target: f.target.row });
		}
		//creating an empty column

		const columnHeight = this.oldBoard.length;
		for (const [col, falls] of fallsByColumn.entries()) {
			const newCol: Crystal[] = new Array(columnHeight);
			//map of target row-> source row for this column
			const fallMap = new Map<number, number>();
			//set of source rows for this column
			const sources = new Set<number>();
			/*we will be ckecking for each row if it has some crystal falling into it, if its the source of a fall or if its unaffected */
			for (const fall of falls) {
				fallMap.set(fall.target, fall.source);
				sources.add(fall.source);
			}
			for (let idx = 0; idx < columnHeight; idx++) {
				//idx will be the row index
				if (fallMap.has(idx)) {
					newCol[idx] = this.oldBoard[fallMap.get(idx)!][col]; //replacing with source crystal
				} else if (sources.has(idx)) {
					newCol[idx] = { crystalType: CrystalType.EMPTY };
				} else {
					newCol[idx] = this.oldBoard[idx][col];
				}
			}
			//writing the new column to the displayed board
			for (let row = 0; row < columnHeight; row++) {
				this.oldBoard[row][col] = newCol[row];
			}
		}
	}
}
