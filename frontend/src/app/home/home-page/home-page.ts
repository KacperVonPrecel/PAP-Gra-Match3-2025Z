import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { SettingsDialog } from './dialogs/settings/settings-dialog';
import { RankingDialog } from './dialogs/ranking/ranking-dialog';

@Component({
  selector: 'app-home-page',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
  ],
  templateUrl: './home-page.html',
  styleUrl: './home-page.scss',
})
export class HomePage {
  private _money: number = 0;
  private _rank: string = "F"

  constructor(private dialog: MatDialog){}

  get money(): number{
    return  this._money;
  }

  get rank(): string{
    return this._rank;
  }

  openSettings(){
    this.dialog.open(SettingsDialog)
  }

  openRanking(){
    this.dialog.open(RankingDialog)
  }
}
