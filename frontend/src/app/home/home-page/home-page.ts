import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon, MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-home-page',
  imports: [
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './home-page.html',
  styleUrl: './home-page.scss',
})
export class HomePage {
  private _money: number = 0;
  private _rank: string = "F"

  get money(): number{
    return  this._money;
  }

  get rank(): string{
    return this._rank;
  }
}
