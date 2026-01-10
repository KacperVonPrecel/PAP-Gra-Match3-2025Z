import { GameBoard } from './game-board';
import { Crystal } from '../../game-state';

export class AnimationState {
	swap = new Map<string, string>();
	destroyed = new Map<string, string>();
	falling = new Map<string, { class: string; distance: number; startOffset: number }>();
	new = new Map<string, { class: string; crystal: Crystal; startOffset: number }>();

	getAnimationClasses(row: number, col: number): string {
		const key = `${row},${col}`;
		const classes: string[] = [];
		const s = this.swap.get(key);
		if (s) classes.push(s);
		const d = this.destroyed.get(key);
		if (d) classes.push(d);
		const f = this.falling.get(key);
		if (f) classes.push(f.class);
		const sp = this.new.get(key);
		if (sp) classes.push(sp.class);
		return classes.join(' '); //join all classes into one big string so they can be assigned easily
	}
	//methods for cleaning the entire map
	clearSwap(): void {
		this.swap.clear();
	}
	clearDestroy(): void {
		this.destroyed.clear();
	}
	clearFall(): void {
		this.falling.clear();
	}
	clearNew(): void {
		this.new.clear();
	}
	clearAllClasses(): void {
		this.clearSwap();
		this.clearDestroy();
		this.clearFall();
		this.clearNew();
	}

	//deletting single entry
	deleteSwap(row: number, column: number): void {
		const key = `${row},${column}`;
		this.swap.delete(key);
	}
	deleteFalling(row: number, column: number): void {
		const key = `${row},${column}`;
		this.falling.delete(key);
	}
	deleteNew(row: number, column: number): void {
		const key = `${row},${column}`;
		this.new.delete(key);
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
		this.falling.set(key, { class: 'falling', distance: distance, startOffset: 0 });
	}
	addDestroyed(row: number, column: number, animation_class: string): void {
		const key = `${row},${column}`;
		this.destroyed.set(key, animation_class);
	}
	addNew(row: number, column: number, crystal: Crystal): void {
		const key = `${row},${column}`;
		const startOffset = -(row + 1); //row + 1-> how many rows we need to fall, -1 because offset needs to be negative for falling down
		this.new.set(key, { class: 'falling', crystal: crystal, startOffset: startOffset });
	}

	getFallParameters(row: number, col: number): Record<string, string> {
		const key = `${row},${col}`;
		const fall = this.falling.get(key);
		if (!fall) return {};
		const pxDistance = `calc(var(--cell-size) * ${fall.distance})`;
		const duration = `${GameBoard.FALLING_ONE_BLOCK_DURATION * fall.distance}ms`;
		const startOffset = `calc(var(--cell-size) * ${fall.startOffset})`;
		return { '--fall-distance': pxDistance, '--fall-duration': duration, '--fall-start-offset': startOffset };
	}
}
