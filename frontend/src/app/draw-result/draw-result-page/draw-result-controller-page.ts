import { Component, inject } from '@angular/core';
import { FireflyBackground } from '../../background/firefly-background/firefly-background';
import { DrawResult, DrawType } from '../../user-data/user-data-service';
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
	private _drawType!: DrawType;
	private _currentEntry: number = 0;
	private _showSummary: boolean = false;
	private summaryTimeout = 0;
	private router = inject(Router);
	constructor() {
		const nav = this.router.currentNavigation();
		const state = nav?.extras.state as {
			result: DrawResult;
			drawType: DrawType;
		};
		if (!state?.result) {
			this.router.navigate(['/main/home/draw']);
			return;
		}

		this._result = state.result;
		if (!state?.drawType) {
			this._drawType = DrawType.COMMON;
		} else {
			this._drawType = state.drawType;
		}
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

	get drawType() {
		return this._drawType;
	}

	get color() {
		return drawTypeColorMap[this._drawType];
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

export const drawTypeColorMap: { [key in DrawType]: string } = {
	[DrawType.COMMON]: 'rgba(193, 211, 127, 1)',
	[DrawType.UNCOMMON]: 'rgba(250, 187, 205, 1)',
	[DrawType.RARE]: 'rgba(144, 126, 211, 1)'
};
