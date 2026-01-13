import { Component, Input, input } from '@angular/core';
import { Router } from '@angular/router';
import { DrawResult, DrawResultEntry } from '../../../user-data/user-data-service';
import { MatList, MatListItem } from '@angular/material/list';

@Component({
	selector: 'app-draw-summary',
	imports: [MatList, MatListItem],
	templateUrl: './draw-summary.html',
	styleUrl: './draw-summary.scss'
})
export class DrawSummary {
	result = input.required<DrawLineInfo[], DrawResultEntry[]>({
		transform: (v: DrawResultEntry[]) =>
			v.map((d) => {
				return {
					characterName: d.characterType,
					amount: d.amount
				};
			})
	});
	constructor(private router: Router) {}

	ngAfterViewInit() {
		if (!this.result) {
			this.router.navigate(['/main/home/draw']);
		}
	}
}

interface DrawLineInfo {
	characterName: string;
	amount: number;
}
