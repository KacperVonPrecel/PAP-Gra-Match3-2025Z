import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Register } from './register';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormField, MatInput, MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AuthService, RegisterRequest, RegisterResult } from '../auth-service';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { delay, of } from 'rxjs';
import { Router } from '@angular/router';

describe('Register', () => {
	let component: Register;
	let fixture: ComponentFixture<Register>;
	let authServiceSpy: jasmine.SpyObj<AuthService>;
	let routerSpy: jasmine.SpyObj<Router>;
	let loader: HarnessLoader;

	let usernameFormFieldHarness: MatFormFieldHarness;
	let emailFormFieldHarness: MatFormFieldHarness;
	let passwordFormFieldHarness: MatFormFieldHarness;
	let confirmPasswordFormFieldHarness: MatFormFieldHarness;

	let usernameInputHarness: MatInputHarness;
	let emailInputHarness: MatInputHarness;
	let passwordInputHarness: MatInputHarness;
	let confirmPasswordInputHarness: MatInputHarness;

	let registerButton: MatButtonHarness;

	beforeEach(async () => {
		authServiceSpy = jasmine.createSpyObj('AuthService', ['register']);
		routerSpy = jasmine.createSpyObj('Router', ['navigate']);
		await TestBed.configureTestingModule({
			imports: [Register, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatProgressBarModule],
			providers: [
				{ provide: AuthService, useValue: authServiceSpy },
				{ provide: Router, useValue: routerSpy }
			]
		}).compileComponents();

		fixture = TestBed.createComponent(Register);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);

		usernameFormFieldHarness = await loader.getHarness(MatFormFieldHarness.with({ selector: '#username-field' }));
		emailFormFieldHarness = await loader.getHarness(MatFormFieldHarness.with({ selector: '#email-field' }));
		passwordFormFieldHarness = await loader.getHarness(MatFormFieldHarness.with({ selector: '#password-field' }));
		confirmPasswordFormFieldHarness = await loader.getHarness(MatFormFieldHarness.with({ selector: '#confirm-password-field' }));

		usernameInputHarness = (await usernameFormFieldHarness.getControl(MatInputHarness))!;
		emailInputHarness = (await emailFormFieldHarness.getControl(MatInputHarness))!;
		passwordInputHarness = (await passwordFormFieldHarness.getControl(MatInputHarness))!;
		confirmPasswordInputHarness = (await confirmPasswordFormFieldHarness.getControl(MatInputHarness))!;

		registerButton = await loader.getHarness(MatButtonHarness.with({ selector: '#register-button' }));
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('check if loading indicator is shown when request is in progress', async () => {
		component['_isRequestInProgress'] = true;
		fixture.detectChanges();
		const progressBar = fixture.nativeElement.querySelector('#progress');
		expect(progressBar).toBeTruthy();
	});

	it('check if loading indicator is hidden when request is not in progress', async () => {
		component['_isRequestInProgress'] = false;
		fixture.detectChanges();
		const progressBar = fixture.nativeElement.querySelector('#progress');
		expect(progressBar).toBeFalsy();
	});

	it('check if input is connected to form controls', async () => {
		await usernameInputHarness.setValue('testuser');
		await emailInputHarness.setValue('test.user@email.com');
		await passwordInputHarness.setValue('TestPassword123!');
		await confirmPasswordInputHarness.setValue('InvalidPasswordConf123!');

		expect(component.usernameControl.value).toBe('testuser');
		expect(component.emailControl.value).toBe('test.user@email.com');
		expect(component.passwordControl.value).toBe('TestPassword123!');
		expect(component.confirmPasswordControl.value).toBe('InvalidPasswordConf123!');
	});

	it('check if form have errors when is empty', async () => {
		expect(component.usernameControl.hasError('required')).toBeTrue();
		expect(component.emailControl.hasError('required')).toBeTrue();
		expect(component.passwordControl.hasError('required')).toBeTrue();
		expect(component.confirmPasswordControl.hasError('required')).toBeTrue();
		expect(component.registerForm.valid).toBeFalse();
	});

	it('check if form have errors when text is missing', async () => {
		component.usernameControl.markAsTouched();
		component.emailControl.markAsTouched();
		component.passwordControl.markAsTouched();
		component.confirmPasswordControl.markAsTouched();

		fixture.detectChanges();

		expect(await usernameFormFieldHarness.getTextErrors()).toHaveSize(1);
		expect(await emailFormFieldHarness.getTextErrors()).toHaveSize(1);
		expect(await passwordFormFieldHarness.getTextErrors()).toHaveSize(1);
		expect(await confirmPasswordFormFieldHarness.getTextErrors()).toHaveSize(1);
	});

	it('check if form have errors when text is to short', async () => {
		await usernameInputHarness.setValue('123');
		await emailInputHarness.setValue('123');
		await passwordInputHarness.setValue('123!');

		expect(component.usernameControl.hasError('minlength')).toBeTrue();
		expect(component.emailControl.hasError('minlength')).toBeTrue();
		expect(component.passwordControl.hasError('minlength')).toBeTrue();
	});

	it('check if form have errors when text is too short', async () => {
		await usernameInputHarness.setValue('123');
		await emailInputHarness.setValue('123');
		await passwordInputHarness.setValue('123');

		fixture.detectChanges();

		expect(await usernameFormFieldHarness.getTextErrors()).toHaveSize(1);
		expect(await emailFormFieldHarness.getTextErrors()).toHaveSize(1);
		expect(await passwordFormFieldHarness.getTextErrors()).toHaveSize(1);
	});

	it('check if form have errors when text is to long', async () => {
		const longUsername = 'a'.repeat(25);
		const longEmail = 'a'.repeat(95) + '@email.com';
		const longPassword = 'a'.repeat(45);

		await usernameInputHarness.setValue(longUsername);
		await emailInputHarness.setValue(longEmail);
		await passwordInputHarness.setValue(longPassword);

		expect(component.usernameControl.hasError('maxlength')).toBeTrue();
		expect(component.emailControl.hasError('maxlength')).toBeTrue();
		expect(component.passwordControl.hasError('maxlength')).toBeTrue();
	});

	it('check if form have errors when text is too long', async () => {
		const longUsername = 'a'.repeat(25);
		const longEmail = 'a'.repeat(95) + '@email.com';
		const longPassword = 'a'.repeat(45);

		await usernameInputHarness.setValue(longUsername);
		await emailInputHarness.setValue(longEmail);
		await passwordInputHarness.setValue(longPassword);

		fixture.detectChanges();

		expect(await usernameFormFieldHarness.getTextErrors()).toHaveSize(1);
		expect(await emailFormFieldHarness.getTextErrors()).toHaveSize(1);
		expect(await passwordFormFieldHarness.getTextErrors()).toHaveSize(1);
	});

	it('check if form have errors when email is invalid', async () => {
		await emailInputHarness.setValue('invalid-email');

		expect(component.emailControl.hasError('email')).toBeTrue();
	});

	it('check if form have errors when email is invalid', async () => {
		await emailInputHarness.setValue('invalid-email');

		fixture.detectChanges();
		expect(await emailFormFieldHarness.getTextErrors()).toHaveSize(1);
	});

	it('check if form have no errors when is valid', async () => {
		await usernameInputHarness.setValue('testuser');
		await emailInputHarness.setValue('test.user@email.com');
		await passwordInputHarness.setValue('TestPassword123!');
		await confirmPasswordInputHarness.setValue('TestPassword123!');

		expect(component.usernameControl.valid).toBeTrue();
		expect(component.emailControl.valid).toBeTrue();
		expect(component.passwordControl.valid).toBeTrue();
		expect(component.confirmPasswordControl.valid).toBeTrue();
		expect(component.registerForm.valid).toBeTrue();
	});

	it('check if button is disabled when form is invalid', async () => {
		await usernameInputHarness.setValue('');
		await emailInputHarness.setValue('');
		await passwordInputHarness.setValue('');
		await confirmPasswordInputHarness.setValue('');

		component['_isRequestInProgress'] = false;

		expect(await registerButton.isDisabled()).toBeTrue();
	});

	it('check if button is disabled when request is in progress', async () => {
		await usernameInputHarness.setValue('testuser');
		await emailInputHarness.setValue('test.user@email.com');
		await passwordInputHarness.setValue('TestPassword123!');
		await confirmPasswordInputHarness.setValue('TestPassword123!');

		component['_isRequestInProgress'] = true;

		expect(await registerButton.isDisabled()).toBeTrue();
	});

	it('check if button is enabled when form is valid and request is not in progress', async () => {
		await usernameInputHarness.setValue('testuser');
		await emailInputHarness.setValue('test.user@email.com');
		await passwordInputHarness.setValue('TestPassword123!');
		await confirmPasswordInputHarness.setValue('TestPassword123!');

		component['_isRequestInProgress'] = false;

		expect(await registerButton.isDisabled()).toBeFalse();
	});

	it('should call authService.register when register button is clicked', async () => {
		await usernameInputHarness.setValue('testuser');
		await emailInputHarness.setValue('test.user@email.com');
		await passwordInputHarness.setValue('TestPassword123!');
		await confirmPasswordInputHarness.setValue('TestPassword123!');

		component['_isRequestInProgress'] = false;

		authServiceSpy.register.and.returnValue(of(RegisterResult.SUCCESS));
		await registerButton.click();

		const expectedCallingArguments: RegisterRequest = {
			username: 'testuser',
			email: 'test.user@email.com',
			password: 'TestPassword123!'
		};
		expect(authServiceSpy.register).toHaveBeenCalledOnceWith(expectedCallingArguments);
	});

	it('register should set isRequestInProgress to true when called', async () => {
		expect(component['_isRequestInProgress']).toBeFalse();
		authServiceSpy.register.and.returnValue(of(RegisterResult.SUCCESS).pipe(delay(100)));
		component.register();
		expect(component['_isRequestInProgress']).toBeTrue();

		await new Promise((r) => setTimeout(r, 150));
		expect(component['_isRequestInProgress']).toBeFalse();
	});

	it('register should go to login page after succesfull registration', async () => {
		authServiceSpy.register.and.returnValue(of(RegisterResult.SUCCESS));
		routerSpy.navigate.and.returnValue(Promise.resolve(true));
		component.register();
		expect(routerSpy.navigate).toHaveBeenCalledOnceWith(['/auth/login']);
	});

	it('register should set usernameTaken error when USERNAME_TAKEN is returned', async () => {
		authServiceSpy.register.and.returnValue(of(RegisterResult.USERNAME_TAKEN));
		component.register();
		expect(component.usernameControl.hasError('usernameTaken')).toBeTrue();

		fixture.detectChanges();
		expect(await usernameFormFieldHarness.getTextErrors()).toHaveSize(1);
	});

	it('register should set emailTaken error when EMAIL_TAKEN is returned', async () => {
		authServiceSpy.register.and.returnValue(of(RegisterResult.EMAIL_TAKEN));
		component.register();
		expect(component.emailControl.hasError('emailTaken')).toBeTrue();

		fixture.detectChanges();
		expect(await emailFormFieldHarness.getTextErrors()).toHaveSize(1);
	});
});
