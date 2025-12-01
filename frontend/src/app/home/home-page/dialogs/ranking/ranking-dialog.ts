import { Component } from '@angular/core';
import { MatDialogContent, MatDialogModule } from "@angular/material/dialog";
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { RankingEntry, RankingService } from './ranking-service';

@Component({
  selector: 'app-ranking-dialog',
  imports: [
    MatDialogContent,
    MatDialogModule,
    MatButtonModule,
    MatListModule
  ],
  templateUrl: './ranking-dialog.html',
  styleUrl: './ranking-dialog.scss',
})
export class RankingDialog {
  private _rank: string = "F";
  private _ranking: RankingEntry[] = []

  constructor(private rankingService: RankingService){}

  ngOnInit(): void
  {
    this.rankingService.getRanking().subscribe( data => {
      this._ranking = data;
    })
  }

  get ranking(): RankingEntry[]{
    return this._ranking;
  }

  get rank(): string{
    return this._rank;
  }


}
