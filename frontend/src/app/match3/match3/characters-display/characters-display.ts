import { Component, effect, input } from '@angular/core';
import { PlayerData, PlayerState } from '../../game-state';
import { HealthBar } from './health-bar/health-bar';
import { CharacterType, getCharacterFileName } from '../../../user-data/user-data-service';

@Component({
	selector: 'app-characters-display',
	imports: [HealthBar],
	templateUrl: './characters-display.html',
	styleUrl: './characters-display.scss'
})
export class CharactersDisplay {
	myData = input<PlayerData | null>();
	opponentData = input<PlayerData | null>();
	myState = input<PlayerState | null>();
	opponentState = input<PlayerState | null>();

	private characterImages = new Map<CharacterType, string>();
	imagesLoadedCount: number = 0;

	constructor() {
		effect(() => {
			const opponentData = this.opponentData();
			const myData = this.myData();
			this.loadCharacterAssets();
		});
	}

	loadCharacterAssets(): void {
		this.imagesLoadedCount = 0;
		if (this.myData()) {
			for (const character of this.myData()!.characters) {
				if (!this.characterImages.has(character.characterType)) {
					const url = getCharacterFileName(character.characterType);
					const img = new Image();
					img.src = url;
					this.characterImages.set(character.characterType, url);
					img.onload = () => {
						this.imagesLoadedCount++;
					};
				} else {
					this.imagesLoadedCount++;
				}
			}
		}

		if (this.opponentData()) {
			for (const character of this.opponentData()!.characters) {
				if (!this.characterImages.has(character.characterType)) {
					const url = getCharacterFileName(character.characterType);
					const img = new Image();
					img.src = url;
					this.characterImages.set(character.characterType, url);
					img.onload = () => {
						this.imagesLoadedCount++;
					};
				} else {
					this.imagesLoadedCount++;
				}
			}
		}
	}

	get assetsLoaded(): boolean {
		if (this.imagesLoadedCount == this.characterImages.size) {
			return true;
		}
		return false;
	}

	getCharacterImage(characterType: CharacterType) {
		return this.characterImages.get(characterType);
	}
}
