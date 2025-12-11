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
	@Input() result!: DrawResultEntry[];
	constructor(private router: Router) {}

	ngAfterViewInit() {
		if (!this.result) {
			this.router.navigate(['/main/home/draw']);
		}
	}
}
