import { GameBoard } from './game-board';

enum FallType {
	NEW,
	FALLING
}

export class AnimationState {
	swap = new Map<string, string>();
	destroyed = new Map<string, string>();
	falling = new Map<string, { class: string; distance: number; startOffset: number; fallType: FallType }>();

	getAnimationClasses(row: number, col: number): string {
		const key = `${row},${col}`;
		const classes: string[] = [];
		const s = this.swap.get(key);
		if (s) classes.push(s);
		const d = this.destroyed.get(key);
		if (d) classes.push(d);
		const f = this.falling.get(key);
		if (f) classes.push(f.class);
		return classes.join(' '); //join all classes into one big string so they can be assigned easily
	}
	//methods for cleaning the entire map
	clearSwap(): void {
		this.swap.clear();
	}
	clearDestroy(): void {
		this.destroyed.clear();
	}
	clearFallingAndNew(): void {
		this.falling.clear();
	}
	clearAllClasses(): void {
		this.clearSwap();
		this.clearDestroy();
		this.clearFallingAndNew();
	}

	//deletting single entry
	deleteSwap(row: number, column: number): void {
		const key = `${row},${column}`;
		this.swap.delete(key);
	}
	deleteFallingAndNew(row: number, column: number): void {
		const key = `${row},${column}`;
		this.falling.delete(key);
	}
	deleteDestroyed(row: number, column: number): void {
		const key = `${row},${column}`;
		this.destroyed.delete(key);
	}

	//adding entries
	addSwap(row: number, column: number, animation_class: string): void {
		const key = `${row},${column}`;
		this.swap.set(key, animation_class);
	}

	addFalling(row: number, column: number, target_row: number): void {
		if (target_row <= row) {
			return;
		}
		const distance = target_row - row;
		const key = `${row},${column}`;
		this.falling.set(key, { class: 'falling', distance: distance, startOffset: 0, fallType: FallType.FALLING });
	}
	addDestroyed(row: number, column: number, animation_class: string): void {
		const key = `${row},${column}`;
		this.destroyed.set(key, animation_class);
	}
	addNew(row: number, column: number, offset: number): void {
		const key = `${row},${column}`;
		this.falling.set(key, { class: 'new', distance: 0, startOffset: offset, fallType: FallType.NEW });
	}

	getFallParameters(row: number, col: number): Record<string, string> {
		const key = `${row},${col}`;
		const fall = this.falling.get(key);
		if (!fall) return {};
		let pxDistance = `calc(var(--cell-size) * ${fall.distance})`; //0 * cell size in new, actual distance in falling
		let duration = ``;
		let startOffset = `calc(var(--cell-size) * ${fall.startOffset})`; //0 * cell size in falling, actual offset in new
		if (fall.fallType == FallType.FALLING) {
			duration = `${GameBoard.FALLING_ONE_BLOCK_DURATION * fall.distance}ms`; //duration based on distance
		} else {
			duration = `${GameBoard.FALLING_ONE_BLOCK_DURATION * -1 * fall.startOffset}ms`; //duration based on offset
		}
		return { '--fall-distance': pxDistance, '--fall-duration': duration, '--fall-start-offset': startOffset };
	}
}
