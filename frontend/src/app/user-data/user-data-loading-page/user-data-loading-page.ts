import { Component, OnInit } from '@angular/core';
import { RETURN_URL_QUERY_PARAM, UserDataService } from '../user-data-service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
	selector: 'app-user-data-loading-page',
	imports: [],
	templateUrl: './user-data-loading-page.html',
	styleUrl: './user-data-loading-page.scss'
})
export class UserDataLoadingPage implements OnInit {
	constructor(
		private readonly userDataService: UserDataService,
		private readonly router: Router,
		private readonly route: ActivatedRoute
	) {}

	ngOnInit(): void {
		this.userDataService.loadUserData();
		this.userDataService.observableUserDataLoaded.subscribe((isLoaded) => {
			if (isLoaded) {
				const returnUrl = this.route.snapshot.queryParamMap.get(RETURN_URL_QUERY_PARAM);
				if (returnUrl) this.router.navigate([returnUrl]);
				else this.router.navigate(['/main/home']);
			}
		});
	}
}
