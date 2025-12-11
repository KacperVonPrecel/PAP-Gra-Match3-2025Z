import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { UserData, UserDataService, DrawRequest, DrawType } from '../../user-data/user-data-service';
import { Observable } from 'rxjs';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { AsyncPipe } from '@angular/common';
import { Router } from '@angular/router';

@Component({
	selector: 'app-draw',
	imports: [MatButtonModule, MatIconModule, AsyncPipe],
	templateUrl: './draw.html',
	styleUrl: './draw.scss'
})
export class Draw {
	private _drawData: DrawData[] = [
		{ type: DrawType.COMMON, price: 25, description: 'standard probabilities' },
		{ type: DrawType.UNCOMMON, price: 50, description: 'higher propability for rare characters' },
		{ type: DrawType.RARE, price: 100, description: 'very high propability for rare characters' }
	];

	private _selected = 0;
	get drawType() {
		return this._drawData[this._selected];
	}

	private _amount = 0;
	get amount() {
		return this._amount;
	}

	get userData(): Observable<UserData> {
		return this.userDataService.userDataObservable;
	}

	constructor(
		private userDataService: UserDataService,
		private router: Router
	) {}

	next() {
		if (this._drawData.length > this._selected + 1) {
			this._selected += 1;
		}
	}

	previous() {
		if (this._selected > 0) {
			this._selected -= 1;
		}
	}

	increase_amount() {
		this._amount += 1;
	}

	decrease_amount() {
		if (this._amount > 0) {
			this._amount -= 1;
		}
	}

	draw() {
		const drawRequest: DrawRequest = {
			drawType: this._drawData[this._selected].type,
			amount: this._amount
		};

		// XXXW add boolean isRequestInProgress like in login or register component and block at least button for draw.
		// But I think it should block all buttons and show progress bar

		this.userDataService.draw(drawRequest, this.amount * this._drawData[this._selected].price).subscribe((result) => {
			if (!result) return;
			this.router.navigate(['/main/draw-result'], {
				state: {
					result: result,
					drawType: this.drawType.type
				}
			});
		});
	}
}

interface DrawData {
	type: DrawType;
	price: number;
	description: string;
}
