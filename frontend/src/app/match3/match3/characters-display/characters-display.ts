import { Component, effect, input } from '@angular/core';
import { PlayerData, PlayerState } from '../../game-state';
import { HealthBar } from './health-bar/health-bar';
import { CharacterType, getCharacterFileName } from '../../../user-data/user-data-service';
import { CharacterAnimationState } from './character-animation-state';

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

	private myOldState: PlayerState | null = null;
	private opponentOldState: PlayerState | null = null;

	private characterImages = new Map<CharacterType, string>();
	imagesLoadedCount: number = 0;
	animationState = new CharacterAnimationState();

	constructor() {
		effect(() => {
			const opponentData = this.opponentData();
			const myData = this.myData();
			this.loadCharacterAssets();
		});
		effect(() => {
			const opponentState = this.opponentState();
			const myState = this.myState();

			if (this.opponentOldState && opponentState) {
				for (const key of opponentState.charactersHealth.keys()) {
					const oldHealth = this.opponentOldState.charactersHealth.get(key);
					const newHealth = opponentState.charactersHealth.get(key);
					console.log(oldHealth, newHealth);
					if (oldHealth != newHealth) {
						console.log(key, 'ENTERED');
						if (newHealth! <= 0) {
							console.log('Added dead');
							this.animationState.addDead(key);
						} else {
							this.animationState.addDamage(key);
						}
					}
				}
			}

			if (this.myOldState && myState) {
				for (const key of myState.charactersHealth.keys()) {
					const oldHealth = this.myOldState.charactersHealth.get(key);
					const newHealth = myState.charactersHealth.get(key);
					if (oldHealth != newHealth) {
						if (newHealth && newHealth <= 0) {
							this.animationState.addDead(key);
						} else {
							this.animationState.addDamage(key);
						}
					}
				}
			}

			if (opponentState) {
				this.opponentOldState = opponentState;
			}
			if (myState) {
				this.myOldState = myState;
			}
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
