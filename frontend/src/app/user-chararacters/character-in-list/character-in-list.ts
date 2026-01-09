import { Component, effect, input, OnInit, output, signal } from '@angular/core';
import { getCharacterFileName } from '../../user-data/user-data-service';
import { UserCharacterData } from './user-character-data';
import { timer } from 'rxjs';

@Component({
	selector: 'app-character-in-list',
	imports: [],
	templateUrl: './character-in-list.html',
	styleUrl: './character-in-list.scss'
})
export class CharacterInList {
	character = input.required<UserCharacterData>();
	selected = input.required<boolean>();

	protected upgradeAnim = signal(false);

	clicked = output();

	constructor() {
		let prevLevel = -1;
		effect(() => {
			if (prevLevel === -1) {
				prevLevel = this.character().unlockedData?.level ?? 0;
			}
			if ((this.character().unlockedData?.level ?? 0) > prevLevel && this.upgradeAnim() === false) {
				this.upgradeAnim.set(true);

				timer(3000).subscribe(() => {
					this.upgradeAnim.set(false);
				});
			}

			prevLevel = this.character().unlockedData?.level ?? 0;
		});
	}
	protected filePath(): string {
		return getCharacterFileName(this.character().characterType);
	}

	protected handleClick() {
		if (!this.character().unlockedData) return;
		this.clicked.emit();
	}
}
