import { Component, input } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
	selector: 'app-characters-details',
	imports: [MatAnchor, MatIcon],
	templateUrl: './characters-details.html',
	styleUrl: './characters-details.scss'
})
export class CharactersDetails {
	characterType = input.required<string>();
}
