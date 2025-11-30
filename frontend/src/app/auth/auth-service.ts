import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class AuthService {
	constructor(private http: HttpClient) {}

	register(registerRequest: RegisterRequest): Observable<RegisterResult> {
		return this.http.post('api/auth/register', registerRequest, { responseType: 'json' }).pipe(
			map((_: any) => {
				return RegisterResult.SUCCESS;
			}),
			catchError((error: HttpErrorResponse) => {
				if (error.status === 409) {
					const errorResponse = error.error as RegisterConflictErrorResponse;
					if (errorResponse.error === RegisterConflictError.USERNAME_TAKEN) {
						return of(RegisterResult.USERNAME_TAKEN);
					} else if (errorResponse.error === RegisterConflictError.EMAIL_TAKEN) {
						return of(RegisterResult.EMAIL_TAKEN);
					}
				}
				return of(RegisterResult.SERVER_ERROR);
			})
		);
	}

	login(loginRequest: LoginRequest) : Observable<LoginResult>{
		return this.http.post('api/auth/login', loginRequest, { responseType: 'json' })
		.pipe(
			map(() => {
					return LoginResult.SUCCESS;
				}),
			catchError((error: HttpErrorResponse) => {
				if (error.status === 409) {
					const errorResponse = error.error as LoginErrorResponse;
					if (errorResponse.result == LoginResult.WRONG_USERNAME){
						return of(LoginResult.WRONG_USERNAME)
					} else if (errorResponse.result == LoginResult.WRONG_PASSWORD){
						return of(LoginResult.WRONG_PASSWORD)
					}
				}
				return of (LoginResult.SERVER_ERROR);
			}
		)
	);
	}

}

export interface LoginRequest
{
	username: string,
	password: string
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

export interface LoginErrorResponse
{
	result: LoginResult;
}

export enum RegisterResult {
	SUCCESS = 'SUCCESS',
	USERNAME_TAKEN = 'USERNAME_TAKEN',
	EMAIL_TAKEN = 'EMAIL_TAKEN',
	SERVER_ERROR = 'SERVER_ERROR'
}

export enum LoginResult
{
	SUCCESS = 'SUCCESS',
	WRONG_USERNAME = 'WRONG_USERNAME',
	WRONG_PASSWORD = 'WRONG_PASSWORD',
	SERVER_ERROR = 'SERVER_ERROR'
}
