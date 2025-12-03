import { Routes } from '@angular/router';
import { Register } from './auth/register/register';
import { Login } from './auth/login/login';
import { MainPage } from './main-page/main-page';
import { UserDataLoadingPage } from './user-data/user-data-loading-page/user-data-loading-page';
import { Home } from './home/home';
import { HomePage } from './home/home-page/home-page';
import { userDataGuard } from './user-data/user-data-service';
import { Draw } from './home/draw/draw';
import { DrawResultControllerPage } from './draw-result/draw-result-page/draw-result-controller-page';
import { HistoryComponent } from './history/history-component/history-component';

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
		children: [
			{ path: 'loading', component: UserDataLoadingPage },
			{
				canActivate: [userDataGuard],
				path: 'home',
				component: Home,
				title: 'Home',
				children: [
					{
						path: 'home-page',
						component: HomePage,
						title: 'Home Page'
					},
					{
						path: 'draw',
						component: Draw,
						title: 'Draw'
					},
					{
						path: 'history',
						component: HistoryComponent,
						title: 'History'
					}
				]
			},
			{
				canActivate: [userDataGuard],
				path: 'draw-result',
				component: DrawResultControllerPage,
				title: 'DrawResultPage'
			}
		]
	}
];
