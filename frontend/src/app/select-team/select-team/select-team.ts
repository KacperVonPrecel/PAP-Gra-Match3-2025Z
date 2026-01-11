import { Component, inject, Signal } from '@angular/core';

import { CdkDrag, CdkDragDrop, CdkDragPlaceholder, CdkDropList, CdkDropListGroup, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { UserDataService, CharacterData } from '../../user-data/user-data-service';
import { SelectTeamItem } from '../select-team-item/select-team-item';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatAnchor } from '@angular/material/button';
import { Router } from '@angular/router';

@Component({
	selector: 'app-select-team',
	imports: [CdkDropList, CdkDrag, SelectTeamItem, CdkDragPlaceholder, MatAnchor],
	templateUrl: './select-team.html',
	styleUrl: './select-team.scss'
})
export class SelectTeam {
	private readonly userDataService = inject(UserDataService);
	private readonly matSnackBar = inject(MatSnackBar);
	private readonly router = inject(Router);

	protected readonly selectedCharacters =
		this.userDataService.userData!.activeTeam?.map(
			(charType) => this.userDataService.userDataSignal()!.characters.find((char) => char.characterType === charType)!
		) ?? [];
	protected readonly availableCharacters = this.userDataService.userData!.characters.filter(
		(char) => !this.userDataService.userData!.activeTeam?.includes(char.characterType)
	);

	protected drop(event: CdkDragDrop<CharacterData[]>) {
		if (event.previousContainer === event.container) {
			moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
			return;
		}

		if (event.container.data === this.selectedCharacters && this.selectedCharacters.length >= 3) {
			this.matSnackBar.open('You can only select 3 characters for your team.', 'Close', { duration: 3000 });
			return;
		}

		transferArrayItem(event.previousContainer.data, event.container.data, event.previousIndex, event.currentIndex);
	}

	protected updateTeam() {
		if (this.selectedCharacters.length != 3) {
			this.matSnackBar.open('You must select exactly 3 characters for your team.', 'Close', { duration: 3000 });
			return;
		}
		this.userDataService.setActiveTeam(this.selectedCharacters.map((char) => char.characterType)).subscribe(() => {
			this.router.navigate(['/main/home/home-page']);
		});
	}
}
