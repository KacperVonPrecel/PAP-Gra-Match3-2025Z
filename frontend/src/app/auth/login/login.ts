import { Component, inject } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormsModule, Validators } from '@angular/forms';
import { AuthService, LoginRequest, LoginResult } from '../auth-service';
import { ReactiveFormsModule } from '@angular/forms';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner, MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router, RouterLink } from '@angular/router';

@Component({
	selector: 'app-login',
	imports: [FormsModule, ReactiveFormsModule, MatButtonModule, MatInputModule, MatProgressSpinnerModule, RouterLink],
	templateUrl: './login.html',
	styleUrl: './login.scss',
	providers: [{ provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: { appearance: 'outline' } }]
})
export class Login {
	readonly ErrorToDisplay = ErrorToDisplay;

	private _isRequestInProgress: boolean = false;
	get isRequestInProgress(): boolean {
		return this._isRequestInProgress;
	}

	private _errorToDisplay?: ErrorToDisplay = undefined;
	get errorToDisplay(): ErrorToDisplay | undefined {
		return this._errorToDisplay;
	}

	readonly loginForm;

	constructor(
		formBuilder: FormBuilder,
		private readonly authService: AuthService,
		private readonly router: Router
	) {
		this.loginForm = formBuilder.group({
			username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]),
			password: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(40)])
		});
	}

	get usernameControl(): AbstractControl {
		return this.loginForm.get('username')!;
	}

	get passwordControl(): AbstractControl {
		return this.loginForm.get('password')!;
	}

	login(): void {
		const loginRequest: LoginRequest = {
			username: this.usernameControl.value!,
			password: this.passwordControl.value!
		};

		this._isRequestInProgress = true;
		this._errorToDisplay = undefined;
		this.authService.login(loginRequest).subscribe((result) => {
			switch (result) {
				case LoginResult.SUCCESS:
					this.router.navigate(['/game']);
					break;
				case LoginResult.INVALID_CREDENTIALS:
					this._errorToDisplay = ErrorToDisplay.INVALID_CREDENTIALS;
					break;
				case LoginResult.SERVER_ERROR:
					this._errorToDisplay = ErrorToDisplay.SERVER_ERROR;
					break;
				case LoginResult.NO_INTERNET:
					this._errorToDisplay = ErrorToDisplay.NO_INTERNET;
					break;
				case LoginResult.UNKNOWN_ERROR:
					this._errorToDisplay = ErrorToDisplay.UNKNOWN_ERROR;
					break;
			}
			this._isRequestInProgress = false;
		});
	}
}

/**
 * It is used to determine which error message to display to the user.
 */
export enum ErrorToDisplay {
	INVALID_CREDENTIALS = 'INVALID_CREDENTIALS',
	SERVER_ERROR = 'SERVER_ERROR',
	NO_INTERNET = 'NO_INTERNET',
	UNKNOWN_ERROR = 'UNKNOWN_ERROR'
}
