import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { BehaviorSubject, delay, map, Observable, retry, shareReplay } from 'rxjs';
import { UserDataLoadingPage } from './user-data-loading-page/user-data-loading-page';

@Injectable({
	providedIn: 'root'
})
export class UserDataService {
	private _userData = new BehaviorSubject<UserData | undefined>(undefined);

	constructor(private readonly http: HttpClient) {}

	get observableUserDataLoaded(): Observable<boolean> {
		return this._userData.asObservable().pipe(map((data) => data !== undefined));
	}

	get isUserDataLoaded(): boolean {
		return this._userData.value !== undefined;
	}

	/**
	 * Observable that emits the user data once it's loaded.
	 * Throws an error if accessed before data is loaded.
	 */
	get userData(): Observable<UserData> {
		return this._userData.asObservable().pipe(
			map((userData) => {
				if (!userData) {
					throw new Error('User data not loaded yet.');
				}
				return userData;
			})
		);
	}

	loadUserData() {
		this.http
			.get('api/user/data', { responseType: 'json' })
			.pipe(
				map((result) => {
					//XXX validate result structure
					return result as UserData;
				}),
				retry({ delay: 1000 })
			)
			.subscribe((result: UserData) => {
				this._userData.next(result);
			});
	}

	// XXX add method's for updating user drawCharacters, upgradeCharacter and maybe more.
	// Also handling game and should be here. Like updating currency after win/loss.
	// This methods should update the _userData BehaviorSubject accordingly.
}

export const RETURN_URL_QUERY_PARAM = 'returnUrl';

export const userDataGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
	const userDataService = inject(UserDataService);
	const router = inject(Router);
	if (userDataService.isUserDataLoaded) return true;
	return router.createUrlTree(['main/loading'], { queryParams: { RETURN_URL_QUERY_PARAM: state.url } });
};

export interface UserData {
	characters: CharacterData[];
	currency: number;
}

export interface CharacterData {
	characterType: CharacterType;
	damage: number;
	health: number;
	level: number;
	/** If it is null it means that character has reached max level. */
	requiredCopiesForNextLevel: number | null;
	currentCopiesCount: number;
}

export enum CharacterType {
	FIRST_CHARACTER = 'FIRST_CHARACTER',
	SECOND_CHARACTER = 'SECOND_CHARACTER'
}
