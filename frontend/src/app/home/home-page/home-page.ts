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

  constructor(private dialog: MatDialog){}

  openSettings(){
    this.dialog.open(SettingsDialog)
  }
}
