import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FireflyBackground } from "./background/firefly-background/firefly-background";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FireflyBackground],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {}
