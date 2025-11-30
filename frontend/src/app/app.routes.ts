import { Routes } from '@angular/router';
import { Register } from './auth/register/register';
import { Login } from './auth/login/login';
import { HomePage } from './home/home-page/home-page';
import { Home } from './home/home';

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
        path: 'home',
        component: Home,
        title: 'Home'
    }
];
