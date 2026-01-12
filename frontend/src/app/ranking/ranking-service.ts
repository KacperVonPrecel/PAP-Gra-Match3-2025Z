import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class RankingService {
	private readonly http = inject(HttpClient);

	loadRankingEntries(pageNumber?: number): Observable<LoadRankingResponse> {
		let params = new HttpParams().set('size', 20);
		if (pageNumber) params = params.set('pageNumber', pageNumber);

		return this.http.get<LoadRankingResponse>('/api/ranking/global', { params: params, withCredentials: true });
	}
}

export interface LoadRankingResponse {
	loadedRankingEntries: RankingEntry[];
	isMoreToLoad: boolean;
}

export interface RankingEntry {
	userId: number; //XXX it should be bigint
	username: string;
	eloPoints: number;
}
