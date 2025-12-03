import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { BehaviorSubject, delay, map, Observable, of, retry, shareReplay, Subscription } from 'rxjs';
import { UserDataLoadingPage } from './user-data-loading-page/user-data-loading-page';

@Injectable({
	providedIn: 'root'
})
export class UserDataService {
	private readonly _userData = new BehaviorSubject<UserData | undefined>(undefined);
	private _lodingUserDataSubscription: Subscription | undefined;

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

	/**
	 * It should be called to start loading user data when navigating to loading page.
	 * After data is loaded it will be available through {@link userData} observable.
	 * After navigating away from loading page call {@link endLoadingUserData} to stop loading process.
	 * It cannot be multiple loading processes at the same time. If it already is already working loading process this method will throw an error.
	 */
	loadUserData() {
		if (this._lodingUserDataSubscription) throw new Error('It is already loading user data.');
		// It is not required to analyze when stop, because the subscription will be closed after first successful response.
		// When received 401, 403 {@link LogoutInterceptor} will handle logout and clearing user data, and stop loading process.
		this._lodingUserDataSubscription = this.http
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

	/**
	 * It should be called to stop loading user data when navigating away from loading page.
	 * It can be called without checking if loading is in progress or even call {@link loadUserData} before it.
	 */
	endLoadingUserData() {
		this._lodingUserDataSubscription?.unsubscribe();
		this._lodingUserDataSubscription = undefined;
	}

	/**
	 * It should be called after user logout to clear user data.
	 * Call it after navigating out of protected routes by {@link userDataGuard}, because it clears user data.
	 */
	handleLogout() {
		// It more safe to call endLoadingUserData there, than expect that it was called before.
		// Because it doesn't have downside to call it multiple times.
		this.endLoadingUserData();
		this._userData.next(undefined);
	}

	// XXX add method's for updating user drawCharacters, upgradeCharacter and maybe more.
	// Also handling game and should be here. Like updating currency after win/loss.
	// This methods should update the _userData BehaviorSubject accordingly.

	draw(drawRequest: DrawRequest, cost:number): Observable<DrawResult> {
		this._userData.subscribe((res)=>res!.currency-=cost);
		return this.http.post('api/user/draw_characters', drawRequest, {responseType: 'json'}).pipe(
			map((result) => {
					return result as DrawResult;
				})
		)
	}
}

export const RETURN_URL_QUERY_PARAM = 'returnUrl';

export const userDataGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
	const userDataService = inject(UserDataService);
	const router = inject(Router);
	if (userDataService.isUserDataLoaded) return true;
	return router.createUrlTree(['main/loading'], { queryParams: { [RETURN_URL_QUERY_PARAM]: state.url } });
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

export enum DrawType {
	COMMON = 'COMMON',
	UNCOMMON = 'UNCOMMON',
	RARE = 'RARE'
}

export interface DrawRequest {
	drawType: DrawType;
	amount: number;
}

export interface DrawResultEntry{
	characterType: CharacterType;
	amount: number;
}
export interface DrawResult
{
	results: DrawResultEntry[];
}