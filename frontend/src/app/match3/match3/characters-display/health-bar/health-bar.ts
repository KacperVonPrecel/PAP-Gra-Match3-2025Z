import { Component, effect, input } from '@angular/core';

@Component({
	selector: 'app-health-bar',
	imports: [],
	templateUrl: './health-bar.html',
	styleUrl: './health-bar.scss'
})
export class HealthBar {
	maxHealth = input<number>();
	currentHealth = input<number>();

	get healthPercent(): number {
		if (!this.currentHealth() || !this.maxHealth()) {
			return 0;
		}
		if (this.maxHealth() === 0) {
			return 0;
		}
		const health_percent = (this.currentHealth()! / this.maxHealth()!) * 100;
		//not allowing the bar to overflow
		if (health_percent > 100) {
			return 100;
		}
		//guarding against unexpected values
		if (health_percent < 0) {
			return 0;
		}
		return health_percent;
	}

	getMaxHealth(): number {
		if (!this.maxHealth) {
			return 0;
		}
		return this.maxHealth()!;
	}

	getCurrentHealth(): number {
		if (!this.currentHealth) {
			return 0;
		}
		return this.currentHealth()!;
	}

	get barColor(): string {
		const healthPercent = this.healthPercent;
		if (healthPercent > 60) {
			return 'rgba(154, 205, 50, 1)';
		}
		if (healthPercent > 30) {
			return 'rgba(255,215,0, 1)';
		}
		return 'rgba(199, 90, 17)';
	}
}
