import { Component, inject, OnInit, output } from '@angular/core';
import { CharacterType, UserDataService } from '../../user-data/user-data-service';
import { CharacterInList } from '../character-in-list/character-in-list';
import { UserCharacterData } from '../character-in-list/user-character-data';

@Component({
	selector: 'app-characters-list',
	imports: [CharacterInList],
	templateUrl: './characters-list.html',
	styleUrl: './characters-list.scss'
})
export class CharactersList {
	selectedCharacter = output<CharacterType | null>();

	protected userDataService = inject(UserDataService);

	protected characters: UserCharacterData[] = [];

	protected selectedCharacterType: CharacterType | null = null;

	constructor() {
		this.userDataService.userDataObservable.subscribe((data) => {
			const unlockedCharacterData: UserCharacterData[] = data.characters.map((character) => {
				const c: UserCharacterData = { characterType: character.characterType };
				return c;
			});
			const unclockedCharacterTypes = unlockedCharacterData.map((c) => c.characterType);
			const lockedCharacterData: UserCharacterData[] = Object.values(CharacterType)
				.filter((type) => !(type in unclockedCharacterTypes))
				.map((type) => {
					const c: UserCharacterData = { characterType: type };
					return c;
				});

			this.characters = [...unlockedCharacterData, ...lockedCharacterData];
		});
	}

	protected handleSelectingCharacter(character: UserCharacterData) {
		if (this.selectedCharacterType === character.characterType) this.selectedCharacterType = null;
		else this.selectedCharacterType = character.characterType;
		console.log('Selected character:', this.selectedCharacterType);
		this.selectedCharacter.emit(this.selectedCharacterType);
	}
}
