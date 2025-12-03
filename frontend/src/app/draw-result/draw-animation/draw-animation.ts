import { Component, ElementRef, EventEmitter, HostListener, input, output, ViewChild } from '@angular/core';
import { DrawResultEntry } from '../../user-data/user-data-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-draw-animation',
  imports: [CommonModule],
  templateUrl: './draw-animation.html',
  styleUrl: './draw-animation.scss',
})
export class DrawAnimation {

  @ViewChild('animationCanvas', { static: true })
  canvasRef!: ElementRef<HTMLCanvasElement>;
  private drawing_context!: CanvasRenderingContext2D;
  finished = output<void>();
  resultEntry = input<DrawResultEntry>();
  private width!: number;
  private height!: number;
  transformStyle: string = 'translate(0px, 0px)';
  private alpha = 0;

  ngOnChanges(){
    if(this.drawing_context){
      const entry = this.resultEntry();
      this.startSpiralAnimation();
      console.log("ANIMATION FINISHED")
    }
  }

  ngAfterViewInit() {
    this.drawing_context = this.canvasRef.nativeElement.getContext('2d')!;
    this.resizeCanvas();
    this.startSpiralAnimation();
  }

  @HostListener('window:resize')
  resizeCanvas() {
    const canvas = this.canvasRef.nativeElement;
    this.width = canvas.width = canvas.clientWidth;
  this.height = canvas.height = canvas.clientHeight;
  }

  startSpiralAnimation() {
    const beggining_radius = Math.min(this.width, this.height) / 2;
    const centerX = this.width / 2;
    const centerY = this.height / 2;

    this.alpha = 0;
    this.animate(centerX, centerY, beggining_radius)
  }

  animate(centerX: number, centerY: number, beggining_radius: number){
    const alpha_step = 0.05;
    this.alpha += alpha_step;
    const circle_radius = 15;
    const radial_change = -2; //b in the equation
    /*
      archimedean spiral in polar cords: r(alpha) = a + b*alpha
      a - beggining radius, b - how much the radius grows when alpha += 1 radian

      in cartesian: x = r * cos(alpha), y = r * sin(alpha)
    */
    const radius_from_center = beggining_radius +  radial_change * this.alpha;
    //translating so center coordinates are the origin
    const x = centerX + radius_from_center * Math.cos(this.alpha);
    const y = centerY + radius_from_center * Math.sin(this.alpha);

    this.drawing_context.clearRect(0, 0, this.width, this.height);
    this.drawing_context.beginPath();
    this.drawing_context.arc(x, y, circle_radius, 0, Math.PI * 2);
    this.drawing_context.fillStyle = 'white';
    this.drawing_context.fill();

    if(radius_from_center <= 0) {
      this.finished.emit();
      return;
    } else {
      requestAnimationFrame(() => this.animate(centerX, centerY, beggining_radius));
    }
  }

}
