import { Component } from '@angular/core';
import { FireflyBackground } from "../background/firefly-background/firefly-background";
import { DrawResult } from '../user-data/user-data-service';
import { DrawResultService } from '../draw-result/draw-result-service';

@Component({
  selector: 'app-draw-result-page',
  imports: [FireflyBackground],
  templateUrl: './draw-result-page.html',
  styleUrl: './draw-result-page.scss',
})
export class DrawResultPage {
  result?: DrawResult;
  constructor(private drawResultService: DrawResultService){
    this.result = this.drawResultService.getResult();
  }
}
