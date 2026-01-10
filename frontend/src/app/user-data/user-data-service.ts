import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable, Signal } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { BehaviorSubject, catchError, delay, EMPTY, map, Observable, of, retry, shareReplay, Subscription, take, tap } from 'rxjs';
import { UserDataLoadingPage } from './user-data-loading-page/user-data-loading-page';
import { toSignal } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root'
})
export class UserDataService {
	private readonly _userData = new BehaviorSubject<UserData | undefined>(undefined);
	private _lodingUserDataSubscription: Subscription | undefined;
	private readonly _userDataSignal = toSignal(this._userData);

	constructor(
		private readonly http: HttpClient,
		private readonly router: Router
	) {}

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

	get userDataSignal(): Signal<UserData | undefined> {
		return this._userDataSignal;
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

	logout() {
		return this.http.post('api/logout', {}, { responseType: 'json' }).pipe(
			tap({
				next: () => this.handleLogout(),
				error: () => this.handleLogout() //XXX it should check error code and depending on error handle logout or not
			})
		);
	}

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
			tap((result) => {
				this.handleDrawResult(result, cost);
			}),
			catchError((error: Error) => {
				//Do we need the URL tree here?? like in user data guard
				this.router.navigate(['main/loading']);
				//XXXW move back user to loading page.
				return EMPTY;
			})
		);
	}

	private handleDrawResult(drawResult: DrawResult, cost: number) {
		const oldUserData = this.userData;
		const currentCurrency = oldUserData.currency - cost;
		// It shouldn't happen because before calling request currency value was checked if it enough.
		// But possibly something can change currency in memory during this request, and this means some error in code,
		// because it shouldn't be possible during request.

		if (currentCurrency < 0) throw Error('Currency cannot be negative');

		let characters: CharacterData[] = oldUserData.characters.map((c) => {
			const resultEntry = drawResult.results.find((r) => r.characterType === c.characterType);
			if (resultEntry) {
				return {
					characterType: c.characterType,
					damage: c.damage,
					health: c.health,
					level: c.level,
					requiredCopiesForNextLevel: c.requiredCopiesForNextLevel,
					currentCopiesCount: c.currentCopiesCount + resultEntry.amount
				};
			}
			return c;
		});
		let lockedCharactersData: CharacterData[] = oldUserData.lockedCharacterData;

		const unlockedCharacters: CharacterData[] = [];
		lockedCharactersData.forEach((c) => {
			const resultEntry = drawResult.results.find((r) => r.characterType === c.characterType);
			if (resultEntry) {
				characters.push({
					characterType: c.characterType,
					damage: c.damage,
					health: c.health,
					level: c.level,
					requiredCopiesForNextLevel: c.requiredCopiesForNextLevel,
					currentCopiesCount: c.currentCopiesCount + resultEntry.amount
				});
				unlockedCharacters.push(c);
			}
		});

		lockedCharactersData = lockedCharactersData.filter((c) => !unlockedCharacters.includes(c));

		this._userData.next({
			id: oldUserData.id,
			characters: characters,
			currency: currentCurrency,
			rankingPosition: oldUserData.rankingPosition,
			lockedCharacterData: lockedCharactersData,
			activeTeam: oldUserData.activeTeam
		});
	}

	upgrade(characterType: CharacterType): Observable<void> {
		const request: UpgradeRequest = { characterType: characterType };

		return this.http.post('api/user/upgrade_character', request, { responseType: 'json' }).pipe(
			map((result) => {
				return result as UpgradeResult;
			}),
			catchError((error: HttpErrorResponse) => {
				// XXXW handle error 0 - NO_INTERNET. Show user error
				return EMPTY;
			}),
			tap((result) => {
				const userData = this.userData;
				const newCharactersList = userData.characters.filter((c) => c.characterType !== characterType);
				newCharactersList.push(result.characterData);

				this._userData.next({
					id: userData.id,
					currency: userData.currency,
					characters: newCharactersList,
					rankingPosition: userData.rankingPosition,
					lockedCharacterData: userData.lockedCharacterData,
					activeTeam: userData.activeTeam
				});
			}),
			map(() => {})
		);
	}

	setActiveTeam(characterTypes: CharacterType[]): Observable<void> {
		const request: SetActiveTeamRequest = { newTeam: characterTypes };
		return this.http.post<void>('api/user/set_team', request, { responseType: 'json' }).pipe(
			catchError((error: HttpErrorResponse) => {
				return EMPTY;
			}),
			tap(() => {
				const userData = this.userData;
				this._userData.next({
					id: userData.id,
					currency: userData.currency,
					characters: userData.characters,
					rankingPosition: userData.rankingPosition,
					lockedCharacterData: userData.lockedCharacterData,
					activeTeam: characterTypes
				});
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
	readonly id: number; //XXX it's should be bigint
	readonly characters: CharacterData[];
	readonly currency: number;
	readonly rankingPosition: number;
	readonly lockedCharacterData: CharacterData[];
	readonly activeTeam: CharacterType[] | null;
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

export interface UpgradeRequest {
	readonly characterType: CharacterType;
}

export interface UpgradeResult {
	readonly characterData: CharacterData;
}

export interface SetActiveTeamRequest {
	readonly newTeam: CharacterType[];
}
