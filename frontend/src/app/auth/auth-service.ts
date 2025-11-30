import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class AuthService {
	constructor(private readonly http: HttpClient) {}

	register(registerRequest: RegisterRequest): Observable<RegisterResult> {
		return this.http.post('api/auth/register', registerRequest, { responseType: 'json' }).pipe(
			map(() => {
				return RegisterResult.SUCCESS;
			}),
			catchError((error: HttpErrorResponse) => {
				if (error.status === 0) return of(RegisterResult.NO_INTERNET);
				else if (error.status === 409) {
					const errorResponse = error.error as RegisterConflictErrorResponse;
					if (errorResponse.error === RegisterConflictError.USERNAME_TAKEN) {
						return of(RegisterResult.USERNAME_TAKEN);
					} else if (errorResponse.error === RegisterConflictError.EMAIL_TAKEN) {
						return of(RegisterResult.EMAIL_TAKEN);
					}
				} else if (error.status === 500) return of(RegisterResult.SERVER_ERROR);
				return of(RegisterResult.UNKNOWN_ERROR);
			})
		);
	}

	login(loginRequest: LoginRequest): Observable<LoginResult> {
		return this.http.post('api/auth/login', loginRequest, { responseType: 'json' }).pipe(
			map(() => {
				return LoginResult.SUCCESS;
			}),
			catchError((error: HttpErrorResponse) => {
				if (error.status === 0) return of(LoginResult.NO_INTERNET);
				else if (error.status === 401) return of(LoginResult.INVALID_CREDENTIALS);
				else if (error.status === 500) return of(LoginResult.SERVER_ERROR);
				return of(LoginResult.UNKNOWN_ERROR);
			})
		);
	}
}

export interface LoginRequest {
	username: string;
	password: string;
}

export interface RegisterRequest {
	username: string;
	email: string;
	password: string;
}

export interface RegisterConflictErrorResponse {
	error: RegisterConflictError;
}

export enum RegisterConflictError {
	USERNAME_TAKEN = 'USERNAME_TAKEN',
	EMAIL_TAKEN = 'EMAIL_TAKEN'
}

export enum RegisterResult {
	SUCCESS = 'SUCCESS',
	USERNAME_TAKEN = 'USERNAME_TAKEN',
	EMAIL_TAKEN = 'EMAIL_TAKEN',
	SERVER_ERROR = 'SERVER_ERROR',
	NO_INTERNET = 'NO_INTERNET',
	UNKNOWN_ERROR = 'UNKNOWN_ERROR'
}

export enum LoginResult {
	SUCCESS = 'SUCCESS',
	INVALID_CREDENTIALS = 'INVALID_CREDENTIALS',
	SERVER_ERROR = 'SERVER_ERROR',
	NO_INTERNET = 'NO_INTERNET',
	UNKNOWN_ERROR = 'UNKNOWN_ERROR'
}
