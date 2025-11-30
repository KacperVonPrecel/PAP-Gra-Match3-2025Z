import { Component, inject } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormsModule, Validators } from '@angular/forms';
import { AuthService, LoginRequest, LoginResult } from '../auth-service';
import { ReactiveFormsModule } from '@angular/forms';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner, MatProgressSpinnerModule } from '@angular/material/progress-spinner';


@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatInputModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
  providers: [{provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}}]
})
export class Login {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private _isRequestInProgress: boolean = false;

  readonly loginForm = this.formBuilder.group({
    username: new FormControl('',[Validators.required]),
    password: new FormControl('',[Validators.required])
  });

  get isRequestInProgress(): boolean {
    return this._isRequestInProgress;
  }

  get usernameControl() : AbstractControl
	{
		return this.loginForm.get('username')!;
	}

  get passwordControl() : AbstractControl
	{
		return this.loginForm.get('password')!;
	}

  login() : void
  {
    const loginRequest: LoginRequest = {
      username: this.usernameControl.value!,
      password: this.passwordControl.value!
    }
      this.authService.login(loginRequest).subscribe((result)=>
      {
        switch (result)
                {
                  case LoginResult.SUCCESS:
                    break;
                  case LoginResult.FAILURE:
                    break
      }
      this._isRequestInProgress = false;
    });
    }
  }

