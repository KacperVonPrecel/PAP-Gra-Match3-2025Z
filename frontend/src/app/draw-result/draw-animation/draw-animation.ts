import { Component, ElementRef, EventEmitter, HostListener, input, output, ViewChild } from '@angular/core';
import { characterFileMap, DrawResultEntry } from '../../user-data/user-data-service';
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
	private drawingContext!: CanvasRenderingContext2D;
	finished = output<void>();
	resultEntry = input.required<DrawResultEntry>();
	private width!: number;
	private height!: number;
	private svgImage: HTMLImageElement = new Image();
	private circles = [
		{ alpha: (Math.PI * 2) / 3, startingAlpha: (Math.PI * 2) / 3 },
		{ alpha: (Math.PI * 4) / 3, startingAlpha: (Math.PI * 4) / 3 },
		{ alpha: 0, startingAlpha: 0 }
	];

	ngOnChanges() {
		if (this.drawingContext) {
			this.startSpiralAnimation();
			console.log('ANIMATION FINISHED');
		}
	}

	ngAfterViewInit() {
		this.drawingContext = this.canvasRef.nativeElement.getContext('2d')!;
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
		const begginingRadius = Math.min(this.width, this.height) / 2;
		const centerX = this.width / 2;
		const centerY = this.height / 2;

		for (const circle of this.circles) {
			circle.alpha = circle.startingAlpha;
		}
		this.animateSpiral(centerX, centerY, begginingRadius);
	}

	animateSpiral(centerX: number, centerY: number, begginingRadius: number) {
		const alphaStep = 0.05;
		const circleRadius = 30;
		const radialChange = -35 * (begginingRadius / 1080); //b in the equation
		this.drawingContext.clearRect(0, 0, this.width, this.height);
		for (const circle of this.circles) {
			circle.alpha += alphaStep;
			/*
      archimedean spiral in polar cords: r(alpha) = a + b*alpha
      a - beggining radius, b - how much the radius grows when alpha += 1 radian

      in cartesian: x = r * cos(alpha), y = r * sin(alpha)
    */
			const radiusFromCenter = begginingRadius + radialChange * circle.alpha;
			//translating so center coordinates are the origin
			const x = centerX + radiusFromCenter * Math.cos(circle.alpha);
			const y = centerY + radiusFromCenter * Math.sin(circle.alpha);

			this.drawingContext.beginPath();
			this.drawingContext.arc(x, y, circleRadius, 0, Math.PI * 2);
			const gradient = this.drawingContext.createRadialGradient(x, y, 0, x, y, circleRadius * 2);

			gradient.addColorStop(0, `rgba(193, 211, 127, 1)`);
			gradient.addColorStop(1, 'transparent');
			this.drawingContext.fillStyle = gradient;
			this.drawingContext.arc(x, y, circleRadius * 3, 0, Math.PI * 2);
			this.drawingContext.fill();

			if (radiusFromCenter <= 0) {
				this.svgImage.onload = () => {
					this.animateCharacter(2);
				};
				this.svgImage.src = 'assets/characters/' + characterFileMap[this.resultEntry().characterType];

				return;
			}
		}
		//requestAnimationFrame -> call before next repaint, next frame
		requestAnimationFrame(() => this.animateSpiral(centerX, centerY, begginingRadius));
	}

	animateCharacter(circleRadius: number) {
		const scale = Math.min(this.height, this.width) / 1080;
		const maxRadius = Math.min(this.height, this.width) * 0.8;
		const circleRadiusStep = scale;
		this.drawingContext.clearRect(0, 0, this.width, this.height);
		const x = this.width / 2;
		const y = this.height / 2;
		this.drawingContext.beginPath();
		const gradient = this.drawingContext.createRadialGradient(x, y, 0, x, y, circleRadius * 2);
		gradient.addColorStop(0, `rgba(193, 211, 127, 1)`);
		gradient.addColorStop(1, 'transparent');
		this.drawingContext.fillStyle = gradient;
		this.drawingContext.arc(x, y, circleRadius * 3, 0, Math.PI * 2);
		this.drawingContext.fill();

		if (this.svgImage.complete) {
			const imgWidth = this.svgImage.naturalWidth;
			const imgHeight = this.svgImage.naturalHeight;
			const imgScaledHeight = Math.min(1.5 * circleRadius, 0.8 * this.height);
			const imgScaledWidth = (imgScaledHeight * imgWidth) / imgHeight;
			const imgX = x - imgScaledWidth / 2;
			const imgY = y - imgScaledHeight / 2;
			//saving before changing alpha, so the alpha changes just for the image
			this.drawingContext.save();
			const alpha = Math.min(circleRadius / maxRadius, 1);
			this.drawingContext.globalAlpha = alpha;

			this.drawingContext.drawImage(this.svgImage, imgX, imgY, imgScaledWidth, imgScaledHeight);
		}

		if (circleRadius > maxRadius) {
			setTimeout(() => {
				this.finished.emit();
				console.log('FINISHED');
			}, 5000);
			return;
		}
		requestAnimationFrame(() => this.animateCharacter(circleRadius + circleRadiusStep));
	}
}
