import { Component, inject } from '@angular/core';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import {
	AbstractControl,
	FormBuilder,
	FormControl,
	FormsModule,
	ReactiveFormsModule,
	ValidationErrors,
	ValidatorFn,
	Validators
} from '@angular/forms';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { AuthService, RegisterResult } from '../auth-service';
import { RegisterRequest } from '../auth-service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router, RouterLink } from '@angular/router';

@Component({
	selector: 'app-register',
	imports: [MatInputModule, MatButtonModule, FormsModule, ReactiveFormsModule, MatProgressSpinnerModule, RouterLink],
	templateUrl: './register.html',
	styleUrl: './register.scss',
	providers: [{ provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: { appearance: 'outline' } }]
})
export class Register {
	readonly ErrorToDisplay = ErrorToDisplay;

	private _isRequestInProgress: boolean = false;
	get isRequestInProgress(): boolean {
		return this._isRequestInProgress;
	}

	private _errorToDisplay?: ErrorToDisplay = undefined;
	get errorToDisplay(): ErrorToDisplay | undefined {
		return this._errorToDisplay;
	}

	private readonly passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
		const password = this.passwordControl.value;
		const confirmPassword = this.confirmPasswordControl.value;

		if (password !== confirmPassword) {
			this.confirmPasswordControl.setErrors({ passwordMismatch: true });
		} else {
			this.confirmPasswordControl.setErrors(null);
		}
		return null;
	};

	readonly registerForm;

	constructor(
		formBuilder: FormBuilder,
		private readonly authService: AuthService,
		private readonly router: Router
	) {
		this.registerForm = formBuilder.group({
			username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]),
			email: new FormControl('', [
				Validators.required,
				Validators.minLength(5),
				Validators.maxLength(100),
				Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$')
			]),
			password: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(40)]),
			/** Without any validatorm, because passwordMatchValidator will check if it matches with password.
			 * And if it matches it match valid password it means that it is valid. */
			confirmPassword: new FormControl('', [])
		});
		this.registerForm.setValidators(this.passwordMatchValidator);
	}

	get usernameControl(): AbstractControl {
		return this.registerForm.get('username')!;
	}

	get emailControl(): AbstractControl {
		return this.registerForm.get('email')!;
	}

	get passwordControl(): AbstractControl {
		return this.registerForm.get('password')!;
	}

	get confirmPasswordControl(): AbstractControl {
		return this.registerForm.get('confirmPassword')!;
	}

	register(): void {
		const registerRequest: RegisterRequest = {
			username: this.usernameControl.value!,
			email: this.emailControl.value!,
			password: this.passwordControl.value!
		};

		this._isRequestInProgress = true;
		this._errorToDisplay = undefined;
		this.authService.register(registerRequest).subscribe((result) => {
			switch (result) {
				case RegisterResult.SUCCESS:
					this.router.navigate(['/auth/login']);
					break;
				case RegisterResult.USERNAME_TAKEN:
					this.usernameControl.setErrors({ usernameTaken: true });
					break;
				case RegisterResult.EMAIL_TAKEN:
					this.emailControl.setErrors({ emailTaken: true });
					break;
				case RegisterResult.SERVER_ERROR:
					this._errorToDisplay = ErrorToDisplay.SERVER_ERROR;
					break;
				case RegisterResult.UNKNOWN_ERROR:
					this._errorToDisplay = ErrorToDisplay.UNKNOWN_ERROR;
					break;
				case RegisterResult.NO_INTERNET:
					this._errorToDisplay = ErrorToDisplay.NO_INTERNET;
					break;
			}
			this._isRequestInProgress = false;
		});
	}
}

/**
 * It is used to determine which error message to display to the user.
 * It doesn't include all errors from RegisterResult, only those that need to be displayed in a general error er area.
 */
export enum ErrorToDisplay {
	SERVER_ERROR = 'SERVER_ERROR',
	NO_INTERNET = 'NO_INTERNET',
	UNKNOWN_ERROR = 'UNKNOWN_ERROR'
}
