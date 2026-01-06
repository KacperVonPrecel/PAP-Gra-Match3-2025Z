import { Component, input } from '@angular/core';
import { BoardState, Crystal } from '../../game-state';
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
	private imagesLoadedCount: number = 0;
	private dragStart: { row: number; column: number } | null = null;
	private startX = 0;
	private startY = 0;
	private dragThreshold = 20; //how many pixels need to be moved before its dragged

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

		//If any drag direction was detected, it wont be changed
		this.dragStart = null;
	}

	onPointerUp() {
		this.dragStart = null;
	}

	isMoveValid(move: MoveRequest): boolean {
		const allowed = this.state()?.allowedMoves;
		if (!allowed) return false;
		return allowed.some(
			(m) =>
				m.sourceRow === move.sourceRow &&
				m.sourceColumn === move.sourceColumn &&
				m.targetRow === move.targetRow &&
				m.targetColumn === move.targetColumn
		);
	}

	handleSwapAttempt(target: { row: number; column: number }): boolean {
		const moveRequest: MoveRequest = {
			sourceRow: this.dragStart!.row,
			sourceColumn: this.dragStart!.column,
			targetRow: target.row,
			targetColumn: target.column
		};
		return this.isMoveValid(moveRequest);
		//swap animation
		//if valid keep position
		//if not swap back
	}
}
