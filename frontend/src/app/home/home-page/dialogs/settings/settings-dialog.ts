import { Component } from '@angular/core';
import { MatDialogContent, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { UserData, UserDataService } from '../../../../user-data/user-data-service';
import { Observable } from 'rxjs';
import { AsyncPipe } from '@angular/common';

@Component({
	selector: 'app-settings-dialog',
	imports: [MatDialogContent, MatDialogModule, MatButtonModule, AsyncPipe],
	templateUrl: './settings-dialog.html',
	styleUrl: './settings-dialog.scss'
})
export class SettingsDialog {
	constructor(private userDataService: UserDataService) {}
	get userData(): Observable<UserData> {
		return this.userDataService.userData;
	}
}
