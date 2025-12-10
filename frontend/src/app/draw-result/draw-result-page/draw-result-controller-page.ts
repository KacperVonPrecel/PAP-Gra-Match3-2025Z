import { Component, inject } from '@angular/core';
import { FireflyBackground } from '../../background/firefly-background/firefly-background';
import { DrawResult } from '../../user-data/user-data-service';
import { Router } from '@angular/router';
import { DrawAnimation } from '../draw-animation/draw-animation';
import { MatButtonModule } from '@angular/material/button';
import { DrawSummary } from './draw-summary/draw-summary';

@Component({
	selector: 'app-draw-result-page',
	imports: [FireflyBackground, DrawAnimation, MatButtonModule, DrawSummary],
	templateUrl: './draw-result-controller-page.html',
	styleUrl: './draw-result-controller-page.scss'
})
export class DrawResultControllerPage {
	private _result!: DrawResult;
	private _currentEntry: number = 0;
	private _showSummary: boolean = false;
	private summaryTimeout = 0;
	private router = inject(Router);
	constructor() {
		//private drawResultService: DrawResultService,
		//this._result = this.drawResultService.getResult();
		const nav = this.router.currentNavigation();
		const state = nav?.extras.state as { result: DrawResult };
		if (!state?.result) {
			this.router.navigate(['/main/home/draw']);
			return;
		}

		this._result = state.result;
	}

	ngAfterViewInit() {
		if (!this._result) {
			this.router.navigate(['/main/home/draw']);
			return;
		}
	}

	get currentEntry(): number {
		return this._currentEntry;
	}

	get showSummary(): boolean {
		return this._showSummary;
	}

	get result() {
		return this._result;
	}

	onAnimationFinished(): void {
		this._currentEntry++;
		if (this.currentEntry < this._result.results.length) {
			console.log('IF');
		} else {
			console.log('ENTERED');
			this._showSummary = true;
			this.summaryTimeout = setTimeout(() => {
				this.router.navigate(['/main/home/draw']);
			}, 3000);
		}
	}

	skip(): void {
		if (!this._showSummary) {
			this._showSummary = true;
			this.summaryTimeout = setTimeout(() => {
				this.router.navigate(['/main/home/draw']);
			}, 3000);
		} else {
			clearTimeout(this.summaryTimeout);
			this.router.navigate(['/main/home/draw']);
		}
	}
}
