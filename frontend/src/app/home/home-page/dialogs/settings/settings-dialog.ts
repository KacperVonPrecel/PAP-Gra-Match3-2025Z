import { Component } from '@angular/core';
import { MatDialogContent, MatDialogModule } from "@angular/material/dialog";
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-settings-dialog',
  imports: [
    MatDialogContent,
    MatDialogModule,
    MatButtonModule
  ],
  templateUrl: './settings-dialog.html',
  styleUrl: './settings-dialog.scss',
})
export class SettingsDialog {

}
