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
import { Router } from '@angular/router';

@Component({
	selector: 'app-register',
	imports: [MatInputModule, MatButtonModule, FormsModule, ReactiveFormsModule, MatProgressSpinnerModule],
	templateUrl: './register.html',
	styleUrl: './register.scss',
	providers: [{ provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: { appearance: 'outline' } }]
})
export class Register {
	private readonly formBuilder = inject(FormBuilder);
	private _isRequestInProgress: boolean = false;

	get isRequestInProgress(): boolean {
		return this._isRequestInProgress;
	}

	private readonly passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
		const password = this.passwordControl.value;
		const confirmPassword = this.confirmPasswordControl.value;

		if (password !== confirmPassword) {
			return { passwordMismatch: true };
		}
		return null;
	};

	readonly registerForm = this.formBuilder.group({
		username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]),
		email: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(100), Validators.email]],
		password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40)]],
		confirmPassword: ['', [Validators.required]]
	});

	constructor(
		private readonly authService: AuthService,
		private readonly router: Router
	) {
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
					//XXX show generic error message
					break;
			}
			this._isRequestInProgress = false;
		});
	}
}
