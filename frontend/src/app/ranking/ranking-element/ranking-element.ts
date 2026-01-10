import { Component, input, output } from '@angular/core';

@Component({
	selector: 'app-ranking-element',
	imports: [],
	templateUrl: './ranking-element.html',
	styleUrl: './ranking-element.scss'
})
export class RankingElement {
	place = input.required<number>();
	username = input.required<string>();
	eloPoints = input.required<number>();

	clicked = output<void>();

	handleClick() {
		this.clicked.emit();
	}
}
