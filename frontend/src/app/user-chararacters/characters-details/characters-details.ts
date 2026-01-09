import { Component, computed, inject, input } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { CharacterType, UserDataService } from '../../user-data/user-data-service';

@Component({
	selector: 'app-characters-details',
	imports: [MatAnchor, MatIcon],
	templateUrl: './characters-details.html',
	styleUrl: './characters-details.scss'
})
export class CharactersDetails {
	characterType = input.required<CharacterType>();

	private readonly userDataService = inject(UserDataService);

	protected readonly data = computed(() => {
		const data = this.userDataService.userDataSignal()!.characters.find((c) => c.characterType === this.characterType());
		return data!;
	});

	protected upgrade() {
		this.userDataService.upgrade(this.characterType()).subscribe();
	}
}
