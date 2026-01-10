import { Component, computed, DestroyRef, OnInit, Signal } from '@angular/core';
import { RouterOutlet, RouterLinkWithHref, RouterLinkActive, Router } from '@angular/router';
import { MatAnchor, MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { BreakpointObserver } from '@angular/cdk/layout';
import { MatDialog } from '@angular/material/dialog';
import { UserData, UserDataService } from '../user-data/user-data-service';
import { Observable } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
	selector: 'app-home',
	imports: [RouterOutlet, MatButtonModule, MatIcon, RouterLinkWithHref, RouterLinkActive, AsyncPipe],
	templateUrl: './home.html',
	styleUrl: './home.scss'
})
export class Home implements OnInit {
	private _smallScreen: boolean = false;
	get smallScreen(): boolean {
		return this._smallScreen;
	}
	constructor(
		private readonly breakpointObserver: BreakpointObserver,
		private readonly dialog: MatDialog,
		private readonly userDataService: UserDataService,
		private readonly destroyRef: DestroyRef,
		private readonly router: Router
	) {}

	ngOnInit(): void {
		this.breakpointObserver
			.observe(['(max-width: 600px)'])
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe((result) => {
				this._smallScreen = result.matches;
			});
	}

	get userData(): Observable<UserData> {
		return this.userDataService.userDataObservable;
	}

	get rank(): Signal<number> {
		return computed(() => this.userDataService.userDataSignal()?.rankingPosition ?? 0);
	}

	protected logout() {
		this.userDataService.logout().subscribe({
			next: () => this.router.navigate(['/']),
			error: () => this.router.navigate(['/']) //XXX it should check error code and depending on error navigate or not
		});
	}

	protected openRanking() {
		this.router.navigate(['/main/home/ranking']);
	}
}
