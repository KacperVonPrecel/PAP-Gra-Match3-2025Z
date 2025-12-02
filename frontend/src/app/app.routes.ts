import { Routes } from '@angular/router';
import { Register } from './auth/register/register';
import { Login } from './auth/login/login';
import { Match3 } from './match3/match3/match3';

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
        path: 'match3',
        component: Match3,
        title: 'Match3'
    }
];
