import { Injectable } from '@angular/core';
import { DrawResult } from '../user-data/user-data-service';

@Injectable({
  providedIn: 'root',
})
export class DrawResultService {
  private result?: DrawResult;

    setResult(result: DrawResult) {
    this.result = result;
  }

  getResult(): DrawResult | undefined {
    const r = this.result;
    this.result = undefined;
    return r;
  }
}
