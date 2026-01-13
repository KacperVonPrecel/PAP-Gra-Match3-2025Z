import { Component, computed, inject, Signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { CharacterType, UserDataService, CharacterData } from '../../user-data/user-data-service';
import { CharacterInList } from '../../user-chararacters/character-in-list/character-in-list';
import { CharacterInTeam } from './character-in-team/character-in-team';
import { Router, RouterLink } from '@angular/router';

@Component({
	selector: 'app-home-page',
	imports: [MatButtonModule, MatIconModule, MatDialogModule, CharacterInTeam, RouterLink],
	templateUrl: './home-page.html',
	styleUrl: './home-page.scss'
})
export class HomePage {
	private readonly userDataService = inject(UserDataService);
	private readonly router = inject(Router);

	protected get activeTeam(): Signal<CharacterData[] | null> {
		return computed(() => {
			const selectedCharacters = this.userDataService.userDataSignal()?.activeTeam;
			if (!selectedCharacters) {
				return null;
			}
			return selectedCharacters.map(
				(charType) => this.userDataService.userDataSignal()!.characters.find((char) => char.characterType === charType)!
			);
		});
	}

	protected changeTeam() {
		this.router.navigate(['/main/home/select-team']);
	}
}
