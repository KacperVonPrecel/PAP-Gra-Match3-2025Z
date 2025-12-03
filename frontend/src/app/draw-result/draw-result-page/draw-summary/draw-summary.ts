import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { DrawResult } from '../../../user-data/user-data-service';
import { DrawResultService } from '../../draw-result-service';
import { MatList, MatListItem } from "@angular/material/list";

@Component({
  selector: 'app-draw-summary',
  imports: [MatList, MatListItem],
  templateUrl: './draw-summary.html',
  styleUrl: './draw-summary.scss',
})
export class DrawSummary {
  private _result!: DrawResult;
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

  get result(){
    return this._result;
  }

}
