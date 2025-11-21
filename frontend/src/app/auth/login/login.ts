import { Component, inject } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormsModule, Validators } from '@angular/forms';
import { AuthService, LoginRequest, LoginResult } from '../auth-service';
import { ReactiveFormsModule } from '@angular/forms';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';


@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatInputModule,
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
  providers: [{provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}}]
})
export class Login {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);

  readonly loginForm = this.formBuilder.group({
    username: ['',[Validators.required]],
    password: ['',[Validators.required]]
  });


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
    });
    }
  }

