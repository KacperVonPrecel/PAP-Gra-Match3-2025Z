import { TestBed } from '@angular/core/testing';

import { AuthService, LoginRequest, LoginResult, RegisterConflictError, RegisterRequest, RegisterResult } from './auth-service';
import { HttpClientTestingModule, HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('AuthService', () => {
	let service: AuthService;
	let httpMock: HttpTestingController;

	const registerRequest: RegisterRequest = {
		username: 'john',
		email: 'john@example.com',
		password: '123456'
	};

	const loginRequest: LoginRequest = {
		username: 'john',
		password: '123456'
	};

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [provideHttpClient(), provideHttpClientTesting()]
		});
		service = TestBed.inject(AuthService);
		httpMock = TestBed.inject(HttpTestingController);
	});

	afterEach(() => {
		httpMock.verify();
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('register should return SUCCESS on successful registration', (done) => {
		service.register(registerRequest).subscribe((result) => {
			expect(result).toBe(RegisterResult.SUCCESS);
			done();
		});

		const req = httpMock.expectOne('api/auth/register');
		expect(req.request.method).toBe('POST');
		req.flush({});
	});

	it('register should return USERNAME_TAKEN if server responds with 409 and username taken', (done) => {
		service.register(registerRequest).subscribe((result) => {
			expect(result).toBe(RegisterResult.USERNAME_TAKEN);
			done();
		});

		const req = httpMock.expectOne('api/auth/register');
		expect(req.request.method).toBe('POST');

		req.flush({ error: RegisterConflictError.USERNAME_TAKEN }, { status: 409, statusText: 'Conflict' });
	});

	it('register should return EMAIL_TAKEN if server responds with 409 and email taken', (done) => {
		service.register(registerRequest).subscribe((result) => {
			expect(result).toBe(RegisterResult.EMAIL_TAKEN);
			done();
		});

		const req = httpMock.expectOne('api/auth/register');
		expect(req.request.method).toBe('POST');

		req.flush({ error: RegisterConflictError.EMAIL_TAKEN }, { status: 409, statusText: 'Conflict' });
	});

	it('register should return SERVER_ERROR if server responds with 500 ', (done) => {
		service.register(registerRequest).subscribe((result) => {
			expect(result).toBe(RegisterResult.SERVER_ERROR);
			done();
		});

		const req = httpMock.expectOne('api/auth/register');
		expect(req.request.method).toBe('POST');

		req.flush({}, { status: 500, statusText: 'Server Error' });
	});

	it('register should return NO_INTERNET if status equals 0', (done) => {
		service.register(registerRequest).subscribe((result) => {
			expect(result).toBe(RegisterResult.NO_INTERNET);
			done();
		});

		const req = httpMock.expectOne('api/auth/register');
		expect(req.request.method).toBe('POST');

		req.flush({}, { status: 0, statusText: 'No internet' });
	});

	it('register should return UNKNOWN_ERROR if error unknown error was returned', (done) => {
		service.register(registerRequest).subscribe((result) => {
			expect(result).toBe(RegisterResult.UNKNOWN_ERROR);
			done();
		});

		const req = httpMock.expectOne('api/auth/register');
		expect(req.request.method).toBe('POST');

		req.flush({}, { status: 700, statusText: 'Unknown error' });
	});

	it('login should return SUCCESS on successful login', (done) => {
		service.login(loginRequest).subscribe((result) => {
			expect(result).toBe(LoginResult.SUCCESS);
			done();
		});

		const req = httpMock.expectOne('api/auth/login');
		expect(req.request.method).toBe('POST');
		req.flush({});
	});

	it('login should return WRONG_LOGIN_DATA if server responds with 401', (done) => {
		service.login(loginRequest).subscribe((result) => {
			expect(result).toBe(LoginResult.INVALID_CREDENTIALS);
			done();
		});

		const req = httpMock.expectOne('api/auth/login');
		expect(req.request.method).toBe('POST');
		req.flush({}, { status: 401, statusText: 'Unauthorized' });
	});

	it('login should return SERVER_ERROR if server responds with 500', (done) => {
		service.login(loginRequest).subscribe((result) => {
			expect(result).toBe(LoginResult.SERVER_ERROR);
			done();
		});

		const req = httpMock.expectOne('api/auth/login');
		expect(req.request.method).toBe('POST');
		req.flush({}, { status: 500, statusText: 'Server Error' });
	});

	it('login should return NO_INTERNET if status equals 0', (done) => {
		service.login(loginRequest).subscribe((result) => {
			expect(result).toBe(LoginResult.NO_INTERNET);
			done();
		});

		const req = httpMock.expectOne('api/auth/login');
		expect(req.request.method).toBe('POST');
		req.flush({}, { status: 0, statusText: 'No internet' });
	});

	it('login should return UNKNOWN_ERROR if error unknown error was returned', (done) => {
		service.login(loginRequest).subscribe((result) => {
			expect(result).toBe(LoginResult.UNKNOWN_ERROR);
			done();
		});

		const req = httpMock.expectOne('api/auth/login');
		expect(req.request.method).toBe('POST');

		req.flush({}, { status: 700, statusText: 'Unknown error' });
	});
});
