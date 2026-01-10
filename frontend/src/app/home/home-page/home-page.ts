import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { CharacterType, UserDataService } from '../../user-data/user-data-service';

@Component({
	selector: 'app-home-page',
	imports: [MatButtonModule, MatIconModule, MatDialogModule],
	templateUrl: './home-page.html',
	styleUrl: './home-page.scss'
})
export class HomePage {
	private readonly userDataService = inject(UserDataService);

	protected xxx() {
		this.userDataService.setActiveTeam([CharacterType.TRASH_MAN, CharacterType.RUBY_HORNED_DAME, CharacterType.SACRED_CAT]).subscribe();
	}
}
