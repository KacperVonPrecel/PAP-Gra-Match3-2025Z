import { T } from '@angular/cdk/keycodes';
import { GameBoard } from './game-board';

export class AnimationState {
	swap = new Map<string, string>();
	destroyed = new Map<string, string>();
	falling = new Map<string, { class: string; distance: number }>();
	new = new Map<string, string>();

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
		if (sp) classes.push(sp);
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
		this.falling.set(key, { class: 'falling', distance });
	}
	addDestroyed(row: number, column: number, animation_class: string): void {
		const key = `${row},${column}`;
		this.destroyed.set(key, animation_class);
	}
	addNew(row: number, column: number, animation_class: string): void {
		const key = `${row},${column}`;
		this.new.set(key, animation_class);
	}

	getFallDistanceAndDuration(row: number, col: number): Record<string, string> {
		const key = `${row},${col}`;
		const fall = this.falling.get(key);
		if (!fall) return {};
		const pxDistance = `calc(var(--cell-size) * ${fall.distance})`;
		const duration = `${GameBoard.FALLING_ONE_BLOCK_DURATION * fall.distance}ms`;
		return { '--fall-distance': pxDistance, '--fall-duration': duration };
	}
}
