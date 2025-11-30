import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatAnchor, MatButtonModule } from "@angular/material/button";

@Component({
  selector: 'app-home',
  imports: [
    RouterOutlet,
    MatAnchor,
    MatButtonModule
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {

}
