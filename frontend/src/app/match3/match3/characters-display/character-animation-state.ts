export class CharacterAnimationState {
	takingDamage = new Map<string, string>();
	dead = new Map<string, string>();

	getAnimationClasses(characterId: number) {
		const key = `${characterId}`;
		const classes: string[] = [];
		const dam = this.takingDamage.get(key);
		if (dam) classes.push(dam);
		const dead = this.dead.get(key);
		if (dead) classes.push(dead);
		return classes.join(' ');
	}

	clearDamage(): void {
		this.takingDamage.clear();
	}

	clearDead(): void {
		this.dead.clear();
	}

	deleteDamage(characterId: number): void {
		const key = `${characterId}`;
		this.takingDamage.delete(key);
	}

	addDamage(characterId: number): void {
		const key = `${characterId}`;
		this.takingDamage.set(key, 'damage');
	}

	addDead(characterId: number): void {
		const key = `${characterId}`;
		this.dead.set(key, 'dead');
	}
}
