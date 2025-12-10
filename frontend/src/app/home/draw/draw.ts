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
	constructor(
		private userDataService: UserDataService,
		private router: Router
	) {}

	private _selected = 0;
	private _amount = 0;
	private _drawData: DrawData[] = [
		{ name: DrawType.COMMON, price: 25, description: 'standard probabilities' },
		{ name: DrawType.UNCOMMON, price: 50, description: 'higher propability for rare characters' },
		{ name: DrawType.RARE, price: 100, description: 'very high propability for rare characters' }
	];

	next() {
		if (this._drawData.length > this._selected + 1) {
			this._selected += 1;
		}
	}

	previous() {
		if (this._drawData.length > 0) {
			this._selected -= 1;
		}
	}

	get amount() {
		return this._amount;
	}

	get drawType() {
		return this._drawData[this._selected];
	}

	get userData(): Observable<UserData> {
		return this.userDataService.userData;
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
			drawType: this._drawData[this._selected].name,
			amount: this._amount
		};
		this.userDataService.draw(drawRequest, this.amount * this._drawData[this._selected].price).subscribe((result) => {
			if (!result) return;
			this.router.navigate(['/main/draw-result'], {
				state: { result }
			});
			//this.drawResultService.setResult(result);
			//this.router.navigate(['/main/draw-result']);
		});
	}
}

interface DrawData {
	name: DrawType;
	price: number;
	description: string;
}
