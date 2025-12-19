import { Component, input } from '@angular/core';
import { HistoryCharacterData } from '../../history-service';
import { getCharacterFileName } from '../../../user-data/user-data-service';

@Component({
	selector: 'app-history-character-component',
	imports: [],
	templateUrl: './history-character-component.html',
	styleUrl: './history-character-component.scss'
})
export class HistoryCharacterComponent {
	character = input.required<HistoryCharacterData>();

	protected filePath(): string {
		return getCharacterFileName(this.character().characterType);
	}
}
