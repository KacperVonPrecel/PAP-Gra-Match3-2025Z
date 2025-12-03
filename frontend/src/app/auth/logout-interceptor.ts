import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UserDataService } from '../user-data/user-data-service';

@Injectable()
export class LogoutInterceptor implements HttpInterceptor {
	constructor(
		private userDataService: UserDataService,
		private router: Router
	) {}

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		return next.handle(req).pipe(
			catchError((error: HttpErrorResponse) => {
				if (error.status === 401 || error.status === 403) {
					this.router.navigate(['auth/login']);
					this.userDataService.handleLogout();
				}

				return throwError(() => error);
			})
		);
	}
}
