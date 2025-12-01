import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatAnchor, MatButtonModule } from "@angular/material/button";
import { FireflyBackground } from "../background/firefly-background/firefly-background";

@Component({
  selector: 'app-home',
  imports: [
    RouterOutlet,
    MatAnchor,
    MatButtonModule,
    FireflyBackground
],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {

}
