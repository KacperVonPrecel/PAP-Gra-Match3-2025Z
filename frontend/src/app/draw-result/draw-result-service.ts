import { Injectable } from '@angular/core';
import { DrawResult } from '../user-data/user-data-service';

@Injectable({
  providedIn: 'root',
})
export class DrawResultService {
  private result!: DrawResult;

    setResult(result: DrawResult) {
    this.result = result;
  }

  getResult(): DrawResult{
    if (!this.result) {
      throw new Error("DrawResultService: No draw result has been set.");
    }
    return this.result;

  }
}
