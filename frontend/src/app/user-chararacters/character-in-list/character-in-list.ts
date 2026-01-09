import { Component, input, output } from '@angular/core';
import { getCharacterFileName } from '../../user-data/user-data-service';
import { UserCharacterData } from './user-character-data';

@Component({
	selector: 'app-character-in-list',
	imports: [],
	templateUrl: './character-in-list.html',
	styleUrl: './character-in-list.scss'
})
export class CharacterInList {
	character = input.required<UserCharacterData>();
	selected = input.required<boolean>();

	clicked = output();

	protected filePath(): string {
		return getCharacterFileName(this.character().characterType);
	}

	protected handleClick() {
		if (!this.character().unlockedData) return;
		this.clicked.emit();
	}
}
