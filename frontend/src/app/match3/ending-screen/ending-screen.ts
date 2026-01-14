import { Component, inject } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { Router } from '@angular/router';

@Component({
	selector: 'app-ending-screen',
	imports: [MatAnchor],
	templateUrl: './ending-screen.html',
	styleUrl: './ending-screen.scss'
})
export class EndingScreen {
	private readonly router = inject(Router);
	protected readonly data: EndingScreenData;

	constructor() {
		const nav = this.router.currentNavigation();
		this.data = nav?.extras.state as EndingScreenData;

		if (this.data?.victory === undefined || this.data?.eloChange === undefined || this.data?.moneyEarned === undefined) {
			this.leave();
			return;
		}
	}

	protected leave() {
		this.router.navigate(['/main/home']);
	}
}

export interface EndingScreenData {
	victory: boolean;
	eloChange: number;
	moneyEarned: number;
}
