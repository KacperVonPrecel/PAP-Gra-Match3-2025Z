import { Component } from '@angular/core';
import { FireflyBackground } from "../../background/firefly-background/firefly-background";
import { DrawResult } from '../../user-data/user-data-service';
import { DrawResultService } from '../draw-result-service';
import { Router } from '@angular/router';
import { DrawAnimation } from "../draw-animation/draw-animation";
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-draw-result-page',
  imports: [
    FireflyBackground,
    DrawAnimation,
    MatButtonModule
  ],
  templateUrl: './draw-result-controller-page.html',
  styleUrl: './draw-result-controller-page.scss',
})
export class DrawResultControllerPage {
  private _result!: DrawResult;
  private _currentEntry:number = 0;
  constructor(
    private drawResultService: DrawResultService,
    private router: Router
  ){
    this._result = this.drawResultService.getResult();
  }

  ngAfterViewInit() {
    if (!this._result) {
      this.router.navigate(['/main/home/draw']);
      return;
    }
  }

  get currentEntry(): number{
    return this._currentEntry
  }

  get result(){
    return this._result;
  }

  onAnimationFinished(): void {
    this._currentEntry++;
      if (this.currentEntry < this._result.results.length) {
        console.log("IF");
      }
      else{
        console.log("ENTERED");
        this.router.navigate(['/main/home/draw']);
      }
  }

  skip():void {
    this.router.navigate(['/main/home/draw']);
  }
}
