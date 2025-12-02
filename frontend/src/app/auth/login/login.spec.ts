import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorToDisplay, Login } from './login';
import { HarnessLoader } from '@angular/cdk/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { provideRouter, Router } from '@angular/router';
import { AuthService, LoginRequest, LoginResult } from '../auth-service';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { delay, of } from 'rxjs';

describe('Login', () => {
	let component: Login;
	let fixture: ComponentFixture<Login>;
	let authServiceSpy: jasmine.SpyObj<AuthService>;
	let router: jasmine.SpyObj<Router>;
	let loader: HarnessLoader;

	let usernameFormFieldHarness: MatFormFieldHarness;
	let passwordFormFieldHarness: MatFormFieldHarness;

	let usernameInputHarness: MatInputHarness;
	let passwordInputHarness: MatInputHarness;

	let loginButton: MatButtonHarness;

	beforeEach(async () => {
		authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
		await TestBed.configureTestingModule({
			imports: [Login, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatProgressBarModule],
			providers: [{ provide: AuthService, useValue: authServiceSpy }, provideRouter([])]
		}).compileComponents();

		router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
		fixture = TestBed.createComponent(Login);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);

		usernameFormFieldHarness = await loader.getHarness(MatFormFieldHarness.with({ selector: '#username-field' }));
		passwordFormFieldHarness = await loader.getHarness(MatFormFieldHarness.with({ selector: '#password-field' }));

		usernameInputHarness = (await usernameFormFieldHarness.getControl(MatInputHarness))!;
		passwordInputHarness = (await passwordFormFieldHarness.getControl(MatInputHarness))!;

		loginButton = await loader.getHarness(MatButtonHarness.with({ selector: '#login-button' }));
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
		await passwordInputHarness.setValue('TestPassword123!');

		expect(component.usernameControl.value).toBe('testuser');
		expect(component.passwordControl.value).toBe('TestPassword123!');
	});

	it('check if form have errors when is empty', async () => {
		expect(component.usernameControl.hasError('required')).toBeTrue();
		expect(component.passwordControl.hasError('required')).toBeTrue();
	});

	it('check if form show errors when text is missing', async () => {
		component.usernameControl.markAsTouched();
		component.passwordControl.markAsTouched();

		fixture.detectChanges();

		expect(await usernameFormFieldHarness.getTextErrors()).toHaveSize(1);
		expect(await passwordFormFieldHarness.getTextErrors()).toHaveSize(1);
	});

	it('check if form have errors when text is to short', async () => {
		await usernameInputHarness.setValue('123');
		await passwordInputHarness.setValue('123!');

		expect(component.usernameControl.hasError('minlength')).toBeTrue();
		expect(component.passwordControl.hasError('minlength')).toBeTrue();
	});

	it('check if form show errors when text is too short', async () => {
		await usernameInputHarness.setValue('123');
		await passwordInputHarness.setValue('123');

		component.usernameControl.markAsTouched();
		component.passwordControl.markAsTouched();

		fixture.detectChanges();

		expect(await usernameFormFieldHarness.getTextErrors())
			.withContext('username')
			.toHaveSize(1);
		expect(await passwordFormFieldHarness.getTextErrors())
			.withContext('password')
			.toHaveSize(1);
	});

	it('check if form have errors when text is to long', async () => {
		const longUsername = 'a'.repeat(25);
		const longPassword = 'a'.repeat(45);

		await usernameInputHarness.setValue(longUsername);
		await passwordInputHarness.setValue(longPassword);

		expect(component.usernameControl.hasError('maxlength')).toBeTrue();
		expect(component.passwordControl.hasError('maxlength')).toBeTrue();
	});

	it('check if form show errors when text is too long', async () => {
		const longUsername = 'a'.repeat(25);
		const longPassword = 'a'.repeat(45);

		await usernameInputHarness.setValue(longUsername);
		await passwordInputHarness.setValue(longPassword);

		component.usernameControl.markAsTouched();
		component.passwordControl.markAsTouched();

		fixture.detectChanges();

		expect(await usernameFormFieldHarness.getTextErrors()).toHaveSize(1);
		expect(await passwordFormFieldHarness.getTextErrors()).toHaveSize(1);
	});

	it('check if form have no errors when is valid', async () => {
		await usernameInputHarness.setValue('testuser');
		await passwordInputHarness.setValue('TestPassword123!');

		expect(component.usernameControl.valid).toBeTrue();
		expect(component.passwordControl.valid).toBeTrue();
	});

	it('check if button is disabled when form is invalid', async () => {
		await usernameInputHarness.setValue('');
		await passwordInputHarness.setValue('');

		component['_isRequestInProgress'] = false;

		expect(await loginButton.isDisabled()).toBeTrue();
	});

	it('check if button is disabled when request is in progress', async () => {
		await usernameInputHarness.setValue('testuser');
		await passwordInputHarness.setValue('TestPassword123!');

		component['_isRequestInProgress'] = true;

		expect(await loginButton.isDisabled()).toBeTrue();
	});

	it('check if button is enabled when form is valid and request is not in progress', async () => {
		await usernameInputHarness.setValue('testuser');
		await passwordInputHarness.setValue('TestPassword123!');

		component['_isRequestInProgress'] = false;

		expect(await loginButton.isDisabled()).toBeFalse();
	});

	it('should call authService.login when login button is clicked', async () => {
		await usernameInputHarness.setValue('testuser');
		await passwordInputHarness.setValue('TestPassword123!');

		component['_isRequestInProgress'] = false;

		authServiceSpy.login.and.returnValue(of(LoginResult.SUCCESS));
		await loginButton.click();

		const expectedCallingArguments: LoginRequest = {
			username: 'testuser',
			password: 'TestPassword123!'
		};
		expect(authServiceSpy.login).toHaveBeenCalledOnceWith(expectedCallingArguments);
	});

	it('login should set isRequestInProgress to true and errorToDisplay to undefined when called', async () => {
		component['_errorToDisplay'] = ErrorToDisplay.SERVER_ERROR;
		expect(component.isRequestInProgress).toBeFalse();
		expect(component.errorToDisplay).toBe(ErrorToDisplay.SERVER_ERROR);

		authServiceSpy.login.and.returnValue(of(LoginResult.SUCCESS).pipe(delay(100)));
		component.login();

		expect(component.errorToDisplay).toBeUndefined();
		expect(component.isRequestInProgress).toBeTrue();

		await new Promise((r) => setTimeout(r, 150));
		expect(component.isRequestInProgress).toBeFalse();
	});

	it('login should go to login page after succesfull registration', async () => {
		spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
		authServiceSpy.login.and.returnValue(of(LoginResult.SUCCESS));
		component.login();
		expect(router.navigate).toHaveBeenCalledOnceWith(['/game']);
	});

	it('login should set errorToDisplay error when INVALID_CREDENTIALS is returned', async () => {
		authServiceSpy.login.and.returnValue(of(LoginResult.INVALID_CREDENTIALS));
		component.login();
		expect(component.errorToDisplay).toBe(ErrorToDisplay.INVALID_CREDENTIALS);

		fixture.detectChanges();
		expect(fixture.nativeElement.querySelector('#invalid-credentials')).not.toBeNull();
	});

	it('login should set errorToDisplay error when SERVER_ERROR is returned', async () => {
		authServiceSpy.login.and.returnValue(of(LoginResult.SERVER_ERROR));
		component.login();
		expect(component.errorToDisplay).toBe(ErrorToDisplay.SERVER_ERROR);

		fixture.detectChanges();
		expect(fixture.nativeElement.querySelector('#server-error')).not.toBeNull();
	});

	it('login should set errorToDisplay error when UNKNOWN_ERROR is returned', async () => {
		authServiceSpy.login.and.returnValue(of(LoginResult.UNKNOWN_ERROR));
		component.login();
		expect(component.errorToDisplay).toBe(ErrorToDisplay.UNKNOWN_ERROR);

		fixture.detectChanges();
		expect(fixture.nativeElement.querySelector('#unknown-error')).not.toBeNull();
	});

	it('login should set errorToDisplay error when NO_INTERNET is returned', async () => {
		authServiceSpy.login.and.returnValue(of(LoginResult.NO_INTERNET));
		component.login();
		expect(component.errorToDisplay).toBe(ErrorToDisplay.NO_INTERNET);

		fixture.detectChanges();
		expect(fixture.nativeElement.querySelector('#no-internet-connection')).not.toBeNull();
	});
});
