import { Component, effect, input } from '@angular/core';

@Component({
	selector: 'app-health-bar',
	imports: [],
	templateUrl: './health-bar.html',
	styleUrl: './health-bar.scss'
})
export class HealthBar {
	max_health = input<number>();
	current_health = input<number>();

	constructor() {
		effect(() => {
			const current_health = this.current_health();
			//fire health fall/increase animation
		});
	}
}
