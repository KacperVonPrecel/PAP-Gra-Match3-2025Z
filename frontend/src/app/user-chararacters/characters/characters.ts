import { Component } from '@angular/core';
import { CharactersList } from '../characters-list/characters-list';
import { CharactersDetails } from '../characters-details/characters-details';
import { CharacterType } from '../../user-data/user-data-service';

@Component({
	selector: 'app-characters',
	imports: [CharactersList, CharactersDetails],
	templateUrl: './characters.html',
	styleUrl: './characters.scss'
})
export class Characters {
	protected selectedCharacter: CharacterType | null = null;

	protected handleCharacterSelected(character: CharacterType | null) {
		this.selectedCharacter = character;
	}
}
