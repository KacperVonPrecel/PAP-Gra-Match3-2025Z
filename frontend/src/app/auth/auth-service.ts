import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService 
{
	constructor(private http: HttpClient) {}


	register(registerRequest: RegisterRequest) : Observable<RegisterResult>
	{
		return this.http.post('api/auth/register', registerRequest, { responseType: 'json' })
			.pipe(
				map((_: any) => {
					return RegisterResult.SUCCESS
				}),
				catchError((error: HttpErrorResponse) => {
					if (error.status === 409)
					{
						const errorResponse = error.error as RegisterErrorResponse;
						if (errorResponse.result === RegisterResult.USERNAME_TAKEN)
						{
							return of(RegisterResult.USERNAME_TAKEN);
						}
						else if (errorResponse.result === RegisterResult.EMAIL_TAKEN)
						{
							return of(RegisterResult.EMAIL_TAKEN);
						}
					}
					return of(RegisterResult.FAILURE);
				})
			);
	}
}

export interface RegisterRequest
{
	username: string;
	email: string;
	password: string;
}

export interface RegisterErrorResponse
{
	result: RegisterResult;
}

export enum RegisterResult
{
	SUCCESS = "SUCCESS",
	USERNAME_TAKEN = "USERNAME_TAKEN",
	EMAIL_TAKEN = "EMAIL_TAKEN",
	FAILURE  = "FAILURE"
}