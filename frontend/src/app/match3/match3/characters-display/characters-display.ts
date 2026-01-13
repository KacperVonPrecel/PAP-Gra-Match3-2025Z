import { Component, input } from '@angular/core';
import { PlayerData, PlayerState } from '../../game-state';

@Component({
	selector: 'app-characters-display',
	imports: [],
	templateUrl: './characters-display.html',
	styleUrl: './characters-display.scss'
})
export class CharactersDisplay {
	myData = input<PlayerData | null>();
	opponentData = input<PlayerData | null>();
	myState = input<PlayerState | null>();
	opponentState = input<PlayerState | null>();
}
