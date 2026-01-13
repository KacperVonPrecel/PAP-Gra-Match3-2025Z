import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';

@Component({
	selector: 'app-finding-match',
	imports: [MatProgressSpinner, MatButtonModule, RouterLink],
	templateUrl: './finding-match.html',
	styleUrl: './finding-match.scss'
})
export class FindingMatch {}
