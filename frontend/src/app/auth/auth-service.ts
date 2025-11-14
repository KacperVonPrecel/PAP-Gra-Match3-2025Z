import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';
import { Register } from './register/register';

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
					if (errorResponse.result === RegisterConflictError.USERNAME_TAKEN) {
						return of(RegisterResult.USERNAME_TAKEN);
					} else if (errorResponse.result === RegisterConflictError.EMAIL_TAKEN) {
						return of(RegisterResult.EMAIL_TAKEN);
					}
				}
				return of(RegisterResult.SERVER_ERROR);
			})
		);
	}
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
	SERVER_ERROR = 'SERVER_ERROR'
}
