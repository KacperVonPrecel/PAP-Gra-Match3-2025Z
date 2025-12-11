import { Component, OnInit } from '@angular/core';
import { HistoryMatchData, HistoryService } from '../history-service';
import { HistoryElement } from '../history-element/history-element';
import { ScrollableDirective } from '../scrollable.directive';

@Component({
	selector: 'app-history-component',
	imports: [HistoryElement, ScrollableDirective],
	templateUrl: './history-component.html',
	styleUrl: './history-component.scss'
})
export class HistoryComponent implements OnInit {
	private _loadingMatches: boolean = false;
	private _hasMoreMatches: boolean = false;
	private _historyElements: HistoryMatchData[] = [];
	get historyElements() {
		return this._historyElements;
	}
	private _now = Date.now();
	get now() {
		return this._now;
	}

	constructor(private historyService: HistoryService) {}

	ngOnInit(): void {
		this._loadingMatches = true;
		this.historyService.loadHistory().subscribe((res) => {
			this._historyElements.push(...res.matches);
			this._hasMoreMatches = res.moreToLoad;
			this._loadingMatches = false;
		});
	}

	onScrollState(state: boolean) {
		if (state && this._hasMoreMatches && !this._loadingMatches) {
			this._loadingMatches = true;
			this.historyService.loadHistory(this.historyElements[this.historyElements.length - 1].matchId).subscribe((res) => {
				this._historyElements.push(...res.matches);
				this._hasMoreMatches = res.moreToLoad;
				this._loadingMatches = false;
			});
		}
	}
}
