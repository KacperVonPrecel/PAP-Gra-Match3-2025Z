import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HttpClient, provideHttpClient } from '@angular/common/http';
import { BehaviorSubject, Subscription } from 'rxjs';
import { UserDataService, UserData, CharacterType, CharacterData } from './user-data-service';

describe('UserDataService', () => {
	let service: UserDataService;
	let httpMock: HttpTestingController;

	const userData: UserData = {
		id: 1,
		characters: [
			{
				characterType: CharacterType.AMETHYST_ENCHANTRESS,
				damage: 100,
				health: 1000,
				level: 1,
				requiredCopiesForNextLevel: 10,
				currentCopiesCount: 5
			},
			{
				characterType: CharacterType.TRASH_MAN,
				damage: 150,
				health: 800,
				level: 2,
				requiredCopiesForNextLevel: 20,
				currentCopiesCount: 15
			}
		],
		currency: 5000
	};

	const maxLevelCharacterData: CharacterData = {
		characterType: CharacterType.AMETHYST_ENCHANTRESS,
		damage: 300,
		health: 2000,
		level: 10,
		requiredCopiesForNextLevel: null,
		currentCopiesCount: 50
	};

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [provideHttpClient(), provideHttpClientTesting()]
		});

		service = TestBed.inject(UserDataService);
		httpMock = TestBed.inject(HttpTestingController);
	});

	afterEach(() => {
		httpMock.verify();
		(service as any)._userData = new BehaviorSubject<UserData | undefined>(undefined);
	});

	describe('Initial State', () => {
		it('should be created', () => {
			expect(service).toBeTruthy();
		});

		it('should have undefined user data initially', () => {
			expect(service.isUserDataLoaded).toBe(false);
		});

		it('should emit false for observableUserDataLoaded initially', (done) => {
			service.observableUserDataLoaded.subscribe((isLoaded: boolean) => {
				expect(isLoaded).toBe(false);
				done();
			});
		});
	});

	describe('observableUserDataLoaded', () => {
		it('should emit true after user data is loaded', (done) => {
			const values: boolean[] = [];
			service.observableUserDataLoaded.subscribe((isLoaded: boolean) => {
				values.push(isLoaded);
				if (values.length === 2) {
					expect(values).toEqual([false, true]);
					done();
				}
			});

			service.loadUserData();
			const req = httpMock.expectOne('api/user/data');
			req.flush(userData);
		});

		it('should emit immediate true if data already loaded', (done) => {
			service.loadUserData();
			const req = httpMock.expectOne('api/user/data');
			req.flush(userData);

			service.observableUserDataLoaded.subscribe((isLoaded: boolean) => {
				expect(isLoaded).toBe(true);
				done();
			});
		});
	});

	it('isUserDataLoaded should return true after data is loaded', () => {
		service.loadUserData();
		const req = httpMock.expectOne('api/user/data');
		req.flush(userData);

		expect(service.isUserDataLoaded).toBe(true);
	});

	describe('userData observable', () => {
		it('should throw error if accessed before data is loaded', (done) => {
			service.userDataObservable.subscribe({
				next: () => fail('Should not emit'),
				error: (error: Error) => {
					expect(error.message).toBe('User data not loaded yet.');
					done();
				}
			});
		});

		it('should emit user data after it is loaded', (done) => {
			service.loadUserData();
			const req = httpMock.expectOne('api/user/data');
			req.flush(userData);

			service.userDataObservable.subscribe((data: UserData) => {
				expect(data).toEqual(userData);
				done();
			});
		});

		it('should emit user data to multiple subscribers', (done) => {
			let subscriberCount = 0;
			const checkDone = () => {
				subscriberCount++;
				if (subscriberCount === 3) done();
			};

			service.loadUserData();
			const req = httpMock.expectOne('api/user/data');
			req.flush(userData);

			setTimeout(() => {
				service.userDataObservable.subscribe((data: UserData) => {
					expect(data).toEqual(userData);
					checkDone();
				});

				service.userDataObservable.subscribe((data: UserData) => {
					expect(data).toEqual(userData);
					checkDone();
				});

				service.userDataObservable.subscribe((data: UserData) => {
					expect(data).toEqual(userData);
					checkDone();
				});
			}, 0);
		});

		it('should handle max level character (null requiredCopiesForNextLevel)', (done) => {
			const mockDataWithMaxLevel: UserData = {
				id: 1,
				characters: [maxLevelCharacterData],
				currency: 10000
			};

			service.loadUserData();
			const req = httpMock.expectOne('api/user/data');
			req.flush(mockDataWithMaxLevel);

			setTimeout(() => {
				service.userDataObservable.subscribe((data: UserData) => {
					expect(data.characters[0].requiredCopiesForNextLevel).toBeNull();
					expect(data.characters[0].level).toBe(10);
					done();
				});
			}, 0);
		});
	});

	describe('loadUserData', () => {
		it('should make HTTP GET request to correct endpoint', () => {
			service.loadUserData();
			const req = httpMock.expectOne('api/user/data');
			expect(req.request.method).toBe('GET');
			expect(req.request.responseType).toBe('json');
		});

		it('should update _userData BehaviorSubject with response', () => {
			service.loadUserData();
			const req = httpMock.expectOne('api/user/data');
			req.flush(userData);

			expect(service.isUserDataLoaded).toBe(true);
		});

		it('should handle HTTP error and retry (with delay)', fakeAsync(() => {
			service.loadUserData();

			// First request - error
			const req1 = httpMock.expectOne('api/user/data');
			req1.error(new ProgressEvent('Network error'));

			// Should retry after delay (1000ms)
			tick(1000);
			expect(service.isUserDataLoaded).toBe(false);

			const req2 = httpMock.expectOne('api/user/data');
			req2.flush(userData);

			expect(service.isUserDataLoaded).toBe(true);
		}));

		it('should retry multiple times on consecutive failures', fakeAsync(() => {
			const subscription = service.loadUserData();

			const req1 = httpMock.expectOne('api/user/data');
			req1.error(new ProgressEvent('Network error'));

			tick(1000);
			expect(service.isUserDataLoaded).toBe(false);

			const req2 = httpMock.expectOne('api/user/data');
			req2.error(new ProgressEvent('Network error'));

			tick(1000);
			expect(service.isUserDataLoaded).toBe(false);

			const req3 = httpMock.expectOne('api/user/data');
			req3.flush(userData);

			expect(service.isUserDataLoaded).toBe(true);
		}));

		it('should update existing subscribers when new data loads', (done) => {
			const values: UserData[] = [];
			let subscription: Subscription;

			service.loadUserData();
			const req1 = httpMock.expectOne('api/user/data');
			req1.flush(userData);

			setTimeout(() => {
				subscription = service.userDataObservable.subscribe((data: UserData) => {
					values.push(data);

					if (values.length === 2) {
						expect(values[0]).toEqual(userData);
						expect(values[1]).toEqual(updatedUserData);
						subscription.unsubscribe();
						done();
					}
				});

				const updatedUserData: UserData = {
					id: 1,
					characters: userData.characters,
					currency: 7500 // Updated currency
				};

				service.loadUserData();
				const req2 = httpMock.expectOne('api/user/data');
				req2.flush(updatedUserData);
			}, 0);
		});
	});

	// Test for the XXX validation comment
	describe('Data Validation (Future Implementation)', () => {
		it('should not accept invalid data', () => {
			//XXX
		});
	});

	describe('draw', () => {
		//XXXW
		// XXXW Test for error inside tap()
	});
});
