import { Component, computed, inject, input } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { CharacterType, UserDataService } from '../../user-data/user-data-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { characterNameMap, characterDescriptionMap } from '../../user-data/user-data-service';

@Component({
	selector: 'app-characters-details',
	imports: [MatAnchor, MatIcon],
	templateUrl: './characters-details.html',
	styleUrl: './characters-details.scss'
})
export class CharactersDetails {
	characterType = input.required<CharacterType>();

	private readonly userDataService = inject(UserDataService);
	private readonly snackBar = inject(MatSnackBar);

	protected readonly data = computed(() => {
		const data = this.userDataService.userDataSignal()!.characters.find((c) => c.characterType === this.characterType());
		return data!;
	});

	protected get characterName() {
		return characterNameMap[this.characterType()];
	}

	protected get characterDescription() {
		return characterDescriptionMap[this.characterType()];
	}

	protected upgrade() {
		if (!this.data().requiredCopiesForNextLevel) {
			this.snackBar.open('This character is at max level.', 'Close', { duration: 3000 });
			return;
		}
		if (this.data().requiredCopiesForNextLevel! > this.data().currentCopiesCount) {
			this.snackBar.open('Not enough character copies to upgrade.', 'Close', { duration: 3000 });
			return;
		}
		this.userDataService.upgrade(this.characterType()).subscribe();
	}
}
