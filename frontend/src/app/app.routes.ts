import { Routes } from '@angular/router';
import { Register } from './auth/register/register';
import { Login } from './auth/login/login';
import { MainPage } from './main-page/main-page';
import { UserDataLoadingPage } from './user-data/user-data-loading-page/user-data-loading-page';

export const routes: Routes = [
	{
		path: 'auth/register',
		component: Register,
		title: 'Register'
	},
	{
		path: 'auth/login',
		component: Login,
		title: 'Login'
	},
	{
		path: 'main',
		component: MainPage,
		title: 'Main Page',
		children: [{ path: '', redirectTo: 'loading' }, { path: 'loading', component: UserDataLoadingPage }, { path: 'home', canActivate: [] }]
	}
];
