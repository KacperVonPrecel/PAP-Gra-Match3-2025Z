import { Component, input } from '@angular/core';
import { getCharacterFileName } from '../../user-data/user-data-service';
import { CharacterData } from '../../user-data/user-data-service';

@Component({
	selector: 'app-select-team-item',
	imports: [],
	templateUrl: './select-team-item.html',
	styleUrl: './select-team-item.scss'
})
export class SelectTeamItem {
	character = input.required<CharacterData>();

	protected filePath(): string {
		return getCharacterFileName(this.character().characterType);
	}
}
