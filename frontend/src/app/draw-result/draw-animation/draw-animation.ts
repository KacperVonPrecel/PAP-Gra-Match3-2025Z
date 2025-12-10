import { Component, ElementRef, EventEmitter, HostListener, input, output, ViewChild } from '@angular/core';
import { DrawResultEntry } from '../../user-data/user-data-service';
import { CommonModule } from '@angular/common';

@Component({
	selector: 'app-draw-animation',
	imports: [CommonModule],
	templateUrl: './draw-animation.html',
	styleUrl: './draw-animation.scss'
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
	private circles = [
		{ alpha: (Math.PI * 2) / 3, startingAlpha: (Math.PI * 2) / 3 },
		{ alpha: (Math.PI * 4) / 3, startingAlpha: (Math.PI * 4) / 3 },
		{ alpha: 0, startingAlpha: 0 }
	];

	ngOnChanges() {
		if (this.drawing_context) {
			const entry = this.resultEntry();
			this.startSpiralAnimation();
			console.log('ANIMATION FINISHED');
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

		for (const circle of this.circles) {
			circle.alpha = circle.startingAlpha;
		}
		this.animate(centerX, centerY, beggining_radius);
	}

	animate(centerX: number, centerY: number, begginingRadius: number) {
		const alpha_step = 0.05;
		const circle_radius = 30;
		const radial_change = -35 * (begginingRadius / 1080); //b in the equation
		this.drawing_context.clearRect(0, 0, this.width, this.height);
		for (const circle of this.circles) {
			circle.alpha += alpha_step;
			/*
      archimedean spiral in polar cords: r(alpha) = a + b*alpha
      a - beggining radius, b - how much the radius grows when alpha += 1 radian

      in cartesian: x = r * cos(alpha), y = r * sin(alpha)
    */
			const radius_from_center = begginingRadius + radial_change * circle.alpha;
			//translating so center coordinates are the origin
			const x = centerX + radius_from_center * Math.cos(circle.alpha);
			const y = centerY + radius_from_center * Math.sin(circle.alpha);

			this.drawing_context.beginPath();
			this.drawing_context.arc(x, y, circle_radius, 0, Math.PI * 2);
			const gradient = this.drawing_context.createRadialGradient(x, y, 0, x, y, circle_radius * 2);

			gradient.addColorStop(0, `rgba(193, 211, 127, 1)`);
			gradient.addColorStop(1, 'transparent');
			this.drawing_context.fillStyle = gradient;
			this.drawing_context.arc(x, y, circle_radius * 3, 0, Math.PI * 2);
			this.drawing_context.fill();

			if (radius_from_center <= 0) {
				this.finished.emit();
				return;
			}
		}
		requestAnimationFrame(() => this.animate(centerX, centerY, begginingRadius));
	}
}
