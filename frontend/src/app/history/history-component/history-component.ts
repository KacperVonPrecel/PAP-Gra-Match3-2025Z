import { Component, OnInit } from '@angular/core';
import { HistoryMatchData, HistoryService, UserStats } from '../history-service';
import { HistoryElement } from '../history-element/history-element';
import { ScrollableDirective } from '../scrollable.directive';
import { ActivatedRoute } from '@angular/router';

@Component({
	selector: 'app-history-component',
	imports: [HistoryElement, ScrollableDirective],
	templateUrl: './history-component.html',
	styleUrl: './history-component.scss'
})
export class HistoryComponent implements OnInit {
	private _loadingMatches: boolean = false;
	private _hasMoreMatches: boolean = false;

	private _userStats?: UserStats;
	private _historyElements: HistoryMatchData[] = [];

	private id?: number;

	protected get historyElements() {
		return this._historyElements;
	}

	protected get userStats() {
		return this._userStats;
	}

	private _now = Date.now();
	protected get now() {
		return this._now;
	}

	constructor(
		private historyService: HistoryService,
		private route: ActivatedRoute
	) {}

	ngOnInit(): void {
		this.id = Number(this.route.snapshot.paramMap.get('id'));

		this._loadingMatches = true;
		this.historyService.userStats(this.id).subscribe((res) => {
			this._userStats = res;
		});

		this.historyService.loadHistory(this.id).subscribe((res) => {
			this._historyElements.push(...res.matches);
			this._hasMoreMatches = res.moreToLoad;
			this._loadingMatches = false;
		});
	}

	onScrollState(state: boolean) {
		if (state && this._hasMoreMatches && !this._loadingMatches) {
			this._loadingMatches = true;
			this.historyService.loadHistory(this.id, this.historyElements[this.historyElements.length - 1].matchId).subscribe((res) => {
				this._historyElements.push(...res.matches);
				this._hasMoreMatches = res.moreToLoad;
				this._loadingMatches = false;
			});
		}
	}
}
