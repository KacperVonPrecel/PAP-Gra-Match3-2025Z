import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { BehaviorSubject, catchError, delay, EMPTY, map, Observable, of, retry, shareReplay, Subscription, take, tap } from 'rxjs';
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
	 * @returns observable that emits the user data once it's loaded.
	 * @throws an error if accessed before data is loaded.
	 */
	get userDataObservable(): Observable<UserData> {
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
	 * @returns Current user data value.
	 * @throws an error if accessed before data is loaded.
	 */
	get userData(): UserData {
		const userData = this._userData.value;
		if (userData === undefined) throw new Error('User data not loaded yet.');
		return userData;
	}

	/**
	 * It should be called to start loading user data when navigating to loading page.
	 * After data is loaded it will be available through {@link userDataObservable} observable.
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

	/**
	 * @param cost cannot be negative. It need to be lower than {@link userData} {@link UserData#currency}, otherwise error will be thrown.
	 * @returns observable which need be subscribed to perform request. It shouldn't be unsubscribed because server possibly can perform change in user state
	 * and page won't be updated about that.
	 */
	draw(drawRequest: DrawRequest, cost: number): Observable<DrawResult> {
		if (cost < 0) throw Error('Cost cannot be negative');
		if (this.userData.currency < cost) throw Error('Too much high cost');
		// XXX handle maybe some errors, but what's possible options only 404 bad_request if not enough money. In this case move back to login screen.
		return this.http.post('api/user/draw_characters', drawRequest, { responseType: 'json' }).pipe(
			map((result) => {
				return result as DrawResult;
			}),
			catchError((error: HttpErrorResponse) => {
				// XXXW handle error 0 - NO_INTERNET. Show user error
				return EMPTY;
			}),
			tap(() => {
				const oldUserData = this.userData;
				const currentCurrency = oldUserData.currency - cost;
				// It shouldn't happen because before calling request currency value was checked if it enough.
				// But possibly something can change currency in memory during this request, and this means some error in code,
				// because it shouldn't be possible during request.

				if (currentCurrency < 0) throw Error('Currency cannot be negative');
				// XXXW change also characters data append characters count or create new character if in prev data it was absent.
				this._userData.next({
					characters: oldUserData.characters,
					currency: currentCurrency
				});
			}),
			catchError((error: Error) => {
				//XXXW move back user to loading page.
				return EMPTY;
			})
		);
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
	readonly characters: CharacterData[];
	readonly currency: number;
}

export interface CharacterData {
	readonly characterType: CharacterType;
	readonly damage: number;
	readonly health: number;
	readonly level: number;
	/** If it is null it means that character has reached max level. */
	readonly requiredCopiesForNextLevel: number | null;
	readonly currentCopiesCount: number;
}

export enum CharacterType {
	AMETHYST_ENCHANTRESS = 'AMETHYST_ENCHANTRESS',
	TRASH_MAN = 'TRASH_MAN',
	SACRED_CAT = 'SACRED_CAT',
	EMERALD_CORE_KNIGHT = 'EMERALD_CORE_KNIGHT',
	RUBY_HORNED_DAME = 'RUBY_HORNED_DAME'
}

export function getCharacterFileName(characterType: CharacterType): string {
	return 'assets/characters/' + characterFileMap[characterType];
}

const characterFileMap: { [key in CharacterType]: string } = {
	[CharacterType.AMETHYST_ENCHANTRESS]: 'amethyst_enchantress.svg',
	[CharacterType.TRASH_MAN]: 'trash_man.svg',
	[CharacterType.SACRED_CAT]: 'sacred_cat.svg',
	[CharacterType.EMERALD_CORE_KNIGHT]: 'emerald_core_knight.svg',
	[CharacterType.RUBY_HORNED_DAME]: 'ruby_horned_dame.svg'
};

export enum DrawType {
	COMMON = 'COMMON',
	UNCOMMON = 'UNCOMMON',
	RARE = 'RARE'
}

export interface DrawRequest {
	readonly drawType: DrawType;
	readonly amount: number;
}

export interface DrawResultEntry {
	readonly characterType: CharacterType;
	readonly amount: number;
}
export interface DrawResult {
	readonly results: DrawResultEntry[];
}
