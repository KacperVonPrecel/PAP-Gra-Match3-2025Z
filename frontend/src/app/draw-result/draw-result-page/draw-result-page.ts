import { Component } from '@angular/core';
import { FireflyBackground } from "../../background/firefly-background/firefly-background";
import { DrawResult } from '../../user-data/user-data-service';
import { DrawResultService } from '../draw-result-service';
import { Router } from '@angular/router';
import { DrawAnimation } from "../draw-animation/draw-animation";

@Component({
  selector: 'app-draw-result-page',
  imports: [
    FireflyBackground,
    DrawAnimation],
  templateUrl: './draw-result-page.html',
  styleUrl: './draw-result-page.scss',
})
export class DrawResultPage {
  result!: DrawResult;
  currentEntry:number = 0;
  constructor(
    private drawResultService: DrawResultService,
    private router: Router
  ){
    this.result = this.drawResultService.getResult();
  }

  ngAfterViewInit() {
    if (!this.result) {
      this.router.navigate(['/main/home/draw']);
      return;
    }
  }

  onAnimationFinished() {
    this.router.navigate(['/main/home/draw']);
  }
}
