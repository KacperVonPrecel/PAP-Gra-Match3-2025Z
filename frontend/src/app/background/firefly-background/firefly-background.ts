import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';

@Component({
  selector: 'app-firefly-background',
  imports: [],
  templateUrl: './firefly-background.html',
  styleUrl: './firefly-background.scss',
})
export class FireflyBackground {
  //reference to the firefly-canvas in html
  @ViewChild('fireflyCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;
  private drawing_context!: CanvasRenderingContext2D;
  private particles: any[] = [];
  private width!: number;
  private height!: number;

  //after view init cause we need the canvas to be created to draw on it
  ngAfterViewInit() {
    const canvas = this.canvasRef.nativeElement;
    this.drawing_context = canvas.getContext('2d')!;
    this.resizeCanvas();
    this.createParticles(40);
    this.animate();
  }

  //done to match the size of the drawing to the size of the window
  @HostListener('window:resize')
  resizeCanvas() {
    const canvas = this.canvasRef.nativeElement;
    this.width = canvas.width = window.innerWidth;
    this.height = canvas.height = window.innerHeight;
  }

   createParticles(count: number) {
    for(let i = 0; i < count; i++)
    {
      this.particles.push({
        x: Math.random() * this.width,
        y: Math.random() * this.height,
        radius: Math.random() * 3 + 2,
        horizontal_speed: (Math.random() - 0.5) * 0.5,
        vertical_speed: (Math.random() - 0.5) * 0.5,
        glow_opacity: Math.random() * 0.7 + 0.3,
      });
    }
  }

   animate() {
    this.drawing_context.clearRect(0, 0, this.width, this.height);
    this.particles.forEach(p => {
      p.x += p.horizontal_speed;
      p.y += p.vertical_speed;

      if (p.x < 0 || p.x > this.width) p.horizontal_speed *= -1;
      if (p.y < 0 || p.y > this.height) p.vertical_speed *= -1;

      const gradient = this.drawing_context.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.radius * 3);
      gradient.addColorStop(0, `rgba(240, 226, 163, ${p.glow_opacity})`);
      gradient.addColorStop(1, 'transparent');

      this.drawing_context.fillStyle = gradient;
      this.drawing_context.beginPath();
      //drawing circle (arc from 0 - 2pi)
      this.drawing_context.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
      this.drawing_context.fill();
    });

    requestAnimationFrame(() => this.animate());
  }
}
