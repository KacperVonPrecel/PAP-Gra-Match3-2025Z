import { Component, DestroyRef, inject, resource } from '@angular/core';
import { FireflyBackground } from '../../background/firefly-background/firefly-background';
import { DrawResult, DrawType } from '../../user-data/user-data-service';
import { Router } from '@angular/router';
import { DrawAnimation } from '../draw-animation/draw-animation';
import { MatButtonModule } from '@angular/material/button';
import { DrawSummary } from './draw-summary/draw-summary';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { timer } from 'rxjs';

@Component({
	selector: 'app-draw-result-page',
	imports: [DrawAnimation, MatButtonModule, DrawSummary],
	templateUrl: './draw-result-controller-page.html',
	styleUrl: './draw-result-controller-page.scss'
})
export class DrawResultControllerPage {
	readonly result!: DrawResult;
	readonly drawType!: DrawType;
	private _currentEntry: number = 0;
	private _showSummary: boolean = false;
	private _navigatedOut: boolean = false;

	constructor(
		private readonly router: Router,
		private readonly destroyRef: DestroyRef
	) {
		const nav = this.router.currentNavigation();
		const state = nav?.extras.state as {
			result: DrawResult;
			drawType: DrawType;
		};

		if (!state?.result || !state?.drawType) {
			this.navigateOut();
			return;
		}
		this.result = state?.result;
		this.drawType = state?.drawType;
	}

	get currentEntry(): number {
		return this._currentEntry;
	}

	get showSummary(): boolean {
		return this._showSummary;
	}

	get color() {
		return drawTypeColorMap[this.drawType];
	}

	onAnimationFinished(): void {
		if (this.currentEntry + 1 >= this.result.results.length) {
			this.showSummaryToUser();
		} else {
			this._currentEntry++;
		}
	}

	skip(): void {
		if (!this._showSummary) {
			this.showSummaryToUser();
		} else {
			this.navigateOut();
		}
	}

	private navigateOut(): void {
		if (this._navigatedOut) return;
		this._navigatedOut = true;
		this.router.navigate(['/main/home/draw'], { replaceUrl: true });
	}

	private showSummaryToUser(): void {
		this._showSummary = true;
		timer(3000)
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe(() => {
				this.navigateOut();
			});
	}
}

export const drawTypeColorMap: { [key in DrawType]: string } = {
	[DrawType.COMMON]: 'rgba(193, 211, 127, 1)',
	[DrawType.UNCOMMON]: 'rgba(250, 187, 205, 1)',
	[DrawType.RARE]: 'rgba(144, 126, 211, 1)'
};
