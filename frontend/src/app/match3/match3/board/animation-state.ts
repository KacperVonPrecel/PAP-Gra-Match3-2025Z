export class AnimationState {
	swap = new Map<string, string>();
	destroyed = new Map<string, string>();
	falling = new Map<string, string>();
	new = new Map<string, string>();

	getAnimationClasses(row: number, col: number): string {
		const key = `${row},${col}`;
		const classes: string[] = [];
		const s = this.swap.get(key);
		if (s) classes.push(s);
		const d = this.destroyed.get(key);
		if (d) classes.push(d);
		const f = this.falling.get(key);
		if (f) classes.push(f);
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
	clearSpawn(): void {
		this.new.clear();
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
	addFalling(row: number, column: number, animation_class: string): void {
		const key = `${row},${column}`;
		this.falling.set(key, animation_class);
	}
	addDestroyed(row: number, column: number, animation_class: string): void {
		const key = `${row},${column}`;
		this.destroyed.set(key, animation_class);
	}
	addNew(row: number, column: number, animation_class: string): void {
		const key = `${row},${column}`;
		this.new.set(key, animation_class);
	}
}
