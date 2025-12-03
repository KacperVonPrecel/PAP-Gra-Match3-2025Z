import { Component, EventEmitter, input, output } from '@angular/core';
import { DrawResultEntry } from '../../user-data/user-data-service';

@Component({
  selector: 'app-draw-animation',
  imports: [],
  templateUrl: './draw-animation.html',
  styleUrl: './draw-animation.scss',
})
export class DrawAnimation {

  finished = output<void>();
  resultEntry = input<DrawResultEntry>();

  ngOnChanges(){
    const entry = this.resultEntry();
    setTimeout(()=>this.finished.emit(), 2000)
    console.log("ANIMATION FINISHED")
  }

}
