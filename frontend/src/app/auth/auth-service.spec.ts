import { TestBed } from '@angular/core/testing';

import { AuthService, RegisterRequest, RegisterResult } from './auth-service';
import { HttpClientTestingModule, HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('AuthService', () => {
	let service: AuthService;
	let httpMock: HttpTestingController;

	const mockRequest: RegisterRequest = {
		username: 'john',
		email: 'john@example.com',
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

	it('should return SUCCESS on successful registration', (done) => {
		service.register(mockRequest).subscribe((result) => {
			expect(result).toBe(RegisterResult.SUCCESS);
			done();
		});

		const req = httpMock.expectOne('api/auth/register');
		expect(req.request.method).toBe('POST');
		req.flush({});
	});

	it('should return USERNAME_TAKEN if server responds with 409 and username taken', () => {
		service.register(mockRequest).subscribe((result) => {
			expect(result).toBe(RegisterResult.USERNAME_TAKEN);
		});

		const req = httpMock.expectOne('api/auth/register');
		expect(req.request.method).toBe('POST');

		req.flush({ result: RegisterResult.USERNAME_TAKEN }, { status: 409, statusText: 'Conflict' });
	});

	it('should return EMAIL_TAKEN if server responds with 409 and email taken', () => {
		service.register(mockRequest).subscribe((result) => {
			expect(result).toBe(RegisterResult.EMAIL_TAKEN);
		});

		const req = httpMock.expectOne('api/auth/register');
		expect(req.request.method).toBe('POST');

		req.flush({ result: RegisterResult.EMAIL_TAKEN }, { status: 409, statusText: 'Conflict' });
	});

	it('should return FAILURE for other errors', () => {
		service.register(mockRequest).subscribe((result) => {
			expect(result).toBe(RegisterResult.SERVER_ERROR);
		});

		const req = httpMock.expectOne('api/auth/register');
		expect(req.request.method).toBe('POST');

		req.flush({}, { status: 500, statusText: 'Server Error' });
	});
});
