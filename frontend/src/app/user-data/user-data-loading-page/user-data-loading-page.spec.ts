import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { UserDataService } from '../user-data-service';
import { Router, ActivatedRoute } from '@angular/router';
import { BehaviorSubject, of, Subject } from 'rxjs';
import { UserDataLoadingPage } from './user-data-loading-page';

describe('UserDataLoadingPage', () => {
	let component: UserDataLoadingPage;
	let fixture: ComponentFixture<UserDataLoadingPage>;
	let mockUserDataService: jasmine.SpyObj<UserDataService>;
	let mockRouter: jasmine.SpyObj<Router>;
	let mockActivatedRoute: any;
	let userDataLoadedSubject: Subject<boolean>;

	beforeEach(async () => {
		userDataLoadedSubject = new BehaviorSubject<boolean>(false);

		mockUserDataService = jasmine.createSpyObj('UserDataService', ['loadUserData', 'endLoadingUserData'], {
			observableUserDataLoaded: userDataLoadedSubject.asObservable() // property to include
		});

		mockRouter = jasmine.createSpyObj('Router', ['navigate']);

		const queryParamMap = jasmine.createSpyObj('ParamMap', ['get']);
		mockActivatedRoute = {
			snapshot: {
				queryParamMap: queryParamMap
			}
		};

		await TestBed.configureTestingModule({
			providers: [
				{ provide: UserDataService, useValue: mockUserDataService },
				{ provide: Router, useValue: mockRouter },
				{ provide: ActivatedRoute, useValue: mockActivatedRoute }
			]
		}).compileComponents();

		fixture = TestBed.createComponent(UserDataLoadingPage);
		component = fixture.componentInstance;
	});

	afterEach(() => {
		userDataLoadedSubject.complete();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should call loadUserData on initialization', () => {
		mockActivatedRoute.snapshot.queryParamMap.get.and.returnValue(null);
		component.ngOnInit();
		expect(mockUserDataService.loadUserData).toHaveBeenCalled();
	});

	it('should navigate to returnUrl when user data is loaded and returnUrl exists', fakeAsync(() => {
		const returnUrl = '/dashboard';
		mockActivatedRoute.snapshot.queryParamMap.get.and.returnValue(returnUrl);

		component.ngOnInit();
		userDataLoadedSubject.next(true);
		tick();

		expect(mockRouter.navigate).toHaveBeenCalledWith([returnUrl]);
		expect(mockActivatedRoute.snapshot.queryParamMap.get).toHaveBeenCalledWith('returnUrl');
	}));

	it('should navigate to default route when user data is loaded and no returnUrl exists', fakeAsync(() => {
		mockActivatedRoute.snapshot.queryParamMap.get.and.returnValue(null);

		component.ngOnInit();
		userDataLoadedSubject.next(true);
		tick();

		expect(mockRouter.navigate).toHaveBeenCalledWith(['/main/home']);
	}));

	it('should handle multiple emissions from observableUserDataLoaded', fakeAsync(() => {
		mockActivatedRoute.snapshot.queryParamMap.get.and.returnValue(null);

		component.ngOnInit();
		userDataLoadedSubject.next(false);
		userDataLoadedSubject.next(true);
		tick();

		expect(mockRouter.navigate).toHaveBeenCalledTimes(1);
		expect(mockRouter.navigate).toHaveBeenCalledWith(['/main/home']);
	}));

	it('should not navigate if user data is not loaded', fakeAsync(() => {
		mockActivatedRoute.snapshot.queryParamMap.get.and.returnValue('/dashboard');

		component.ngOnInit();
		userDataLoadedSubject.next(false);
		tick();

		expect(mockRouter.navigate).not.toHaveBeenCalled();
	}));

	it('should handle empty string returnUrl by navigating to default', fakeAsync(() => {
		mockActivatedRoute.snapshot.queryParamMap.get.and.returnValue('');

		component.ngOnInit();
		userDataLoadedSubject.next(true);
		tick();

		expect(mockRouter.navigate).toHaveBeenCalledWith(['/main/home']);
	}));

	it('should handle loaded user data before navigation', fakeAsync(() => {
		mockActivatedRoute.snapshot.queryParamMap.get.and.returnValue(null);
		userDataLoadedSubject.next(true);

		component.ngOnInit();
		tick();

		expect(mockRouter.navigate).toHaveBeenCalledWith(['/main/home']);
	}));
});
