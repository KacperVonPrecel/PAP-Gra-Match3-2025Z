import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { CharacterType } from '../user-data/user-data-service';

@Injectable({
	providedIn: 'root'
})
export class HistoryService {
	constructor(private http: HttpClient) {}

	loadHistory(latestRecordId?: bigint): Observable<HistoryMatchesData> {
		let params = new HttpParams().set('size', 20);
		console.log(latestRecordId?.toString());
		if (latestRecordId) params = params.set('latestRecordId', latestRecordId.toString());

		return this.http.get('api/match_history/load', { params: params, withCredentials: true }).pipe(
			map((result) => {
				//XXX validate result structure
				return result as HistoryMatchesData;
			})
		);
	}
}

export interface HistoryMatchesData {
	matches: HistoryMatchData[];
	moreToLoad: boolean;
}

export interface HistoryMatchData {
	matchId: bigint;
	playerId: bigint;
	playerUsername: string;
	opponentId: bigint;
	opponentsUsername: string;
	finishTime: bigint;
	playerEloChange: number;
	opponentsEloChange: number;
	playerCharacters: HistoryCharacterData[];
	opponentCharacters: HistoryCharacterData[];
	isPlayerWinner: boolean;
}

export interface HistoryCharacterData {
	characterType: CharacterType;
	level: number;
}
