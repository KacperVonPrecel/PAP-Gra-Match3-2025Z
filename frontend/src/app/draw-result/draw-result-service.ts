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
      return this.result ?? null;
    }
    return this.result;

  }
}
