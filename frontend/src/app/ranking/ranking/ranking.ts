import { Component, inject, OnInit } from '@angular/core';
import { RankingEntry, RankingService } from '../ranking-service';
import { ScrollableDirective } from '../../history/scrollable.directive';
import { RankingElement } from '../ranking-element/ranking-element';
import { Router } from '@angular/router';

@Component({
	selector: 'app-ranking',
	imports: [ScrollableDirective, RankingElement],
	templateUrl: './ranking.html',
	styleUrl: './ranking.scss'
})
export class Ranking implements OnInit {
	private readonly rankingService = inject(RankingService);
	private readonly router = inject(Router);

	private _rankingElements: RankingInfo[] = [];

	private pageNumber = 0;
	private _loadingRanking: boolean = true;
	private _hasMoreRankingToLoad: boolean = false;

	protected get rankingElements() {
		return this._rankingElements;
	}

	ngOnInit(): void {
		this.rankingService.loadRankingEntries(this.pageNumber).subscribe((res) => {
			this.handleLoadRankingEntries(null, res.loadedRankingEntries);
			this._hasMoreRankingToLoad = res.isMoreToLoad;
			this._loadingRanking = false;
			this.pageNumber += 1;
		});
	}

	protected onScrollState(state: boolean) {
		if (state && this._hasMoreRankingToLoad && !this._loadingRanking) {
			this._loadingRanking = true;

			this.rankingService.loadRankingEntries(this.pageNumber).subscribe((res) => {
				let prev = this._rankingElements[this._rankingElements.length - 1];
				this.handleLoadRankingEntries(prev, res.loadedRankingEntries);
				this._hasMoreRankingToLoad = res.isMoreToLoad;
				this._loadingRanking = false;
				this.pageNumber += 1;
			});
		}
	}

	private handleLoadRankingEntries(prev: RankingInfo | null, rankingEntries: RankingEntry[]) {
		rankingEntries.forEach((entry, index) => {
			let place: number = -1;
			if (prev && prev.rankingEntry.eloPoints === entry.eloPoints) {
				place = prev.place;
			} else {
				place = this.pageNumber * 20 + index + 1;
			}
			prev = { place: place, rankingEntry: entry };
			this._rankingElements.push(prev);
		});
	}

	handleClick(userId: number) {
		this.router.navigate(['main/home/user-history/' + userId]);
	}
}
interface RankingInfo {
	place: number;
	rankingEntry: RankingEntry;
}
