import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RankingService {
  private _mockRanking: RankingEntry[] = [
    {place: 1,
    rank: 'A',
    name: 'bacio'
    },
    {place: 2,
    rank: 'D',
    name: 'match3_destroyer69'
    }
  ]
  getRanking(): Observable<RankingEntry[]>{
    return of(this._mockRanking);
  }
}

export interface RankingEntry {
  place: number;
  rank: string;
  name: string;
}