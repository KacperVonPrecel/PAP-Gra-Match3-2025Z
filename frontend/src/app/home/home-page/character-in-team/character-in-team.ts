import { Component, input } from '@angular/core';
import { CharacterData, getCharacterFileName } from '../../../user-data/user-data-service';

@Component({
	selector: 'app-character-in-team',
	imports: [],
	templateUrl: './character-in-team.html',
	styleUrl: './character-in-team.scss'
})
export class CharacterInTeam {
	character = input.required<CharacterData>();

	protected filePath(): string {
		return getCharacterFileName(this.character().characterType);
	}
}
