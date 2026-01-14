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
import { Match3 } from './match3/match3/match3';
import { CharactersList } from './user-chararacters/characters-list/characters-list';
import { Characters } from './user-chararacters/characters/characters';
import { Ranking } from './ranking/ranking/ranking';
import { SelectTeam } from './select-team/select-team/select-team';
import { FindingMatch } from './match3/finding-match/finding-match';

export const routes: Routes = [
	{
		path: '',
		pathMatch: 'full',
		redirectTo: 'auth/login'
	},
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
						path: 'user-history/:id',
						component: HistoryComponent,
						title: 'History'
					},
					{
						path: 'history',
						component: HistoryComponent,
						title: 'History'
					},
					{
						path: 'characters',
						component: Characters,
						title: 'Characters'
					},
					{
						path: 'ranking',
						component: Ranking,
						title: 'Ranking'
					},
					{
						path: 'select-team',
						component: SelectTeam,
						title: 'Select Team'
					}
				]
			},
			{
				canActivate: [userDataGuard],
				path: 'draw-result',
				component: DrawResultControllerPage,
				title: 'DrawResultPage'
			},
			{
				canActivate: [userDataGuard],
				path: 'match3',
				component: Match3,
				title: 'Match3'
			},
			{
				canActivate: [userDataGuard],
				path: 'finding-match',
				component: FindingMatch,
				title: 'FindingMatch'
			}
		]
	}
];
