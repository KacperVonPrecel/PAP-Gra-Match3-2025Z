import {
	AfterViewInit,
	Component,
	ElementRef,
	EventEmitter,
	HostListener,
	Input,
	input,
	OnChanges,
	OnDestroy,
	Output,
	output,
	ViewChild
} from '@angular/core';
import { DrawResultEntry, getCharacterFileName } from '../../user-data/user-data-service';
import { CommonModule } from '@angular/common';

@Component({
	selector: 'app-draw-animation',
	imports: [CommonModule],
	templateUrl: './draw-animation.html',
	styleUrl: './draw-animation.scss'
})
export class DrawAnimation implements OnChanges, AfterViewInit, OnDestroy {
	private static readonly ALPHA_STEP_PER_SECOND = 6;
	/** In miliseconds. */
	private static readonly CIRCLE_ANIMATION_DURATION = 3000;
	// XXXW circle radius depending on canvas size?
	private static readonly CIRCLE_RADIUS = 30;

	/** In miliseconds. */
	private static readonly CHARACTER_ANIMATION_DURATION = 3000;
	/** In miliseconds, should be less or equal {@link CHARACTER_ANIMATION_DURATION}. */
	private static readonly CHARACTER_RESIZE_DURATION = DrawAnimation.CHARACTER_ANIMATION_DURATION - 500;

	/** It percentage of canvas height. Value from [0, 1]. */
	private static readonly CHARACTER_STARTING_SIZE = 0.1;
	/** It percentage of canvas height. Value from [0, 1]. */
	private static readonly CHARACTER_END_SIZE = 0.8;

	/** Value from [0, 1]. */
	private static readonly CHARACTER_STARTING_ALPHA = 0.2;
	/** Value from [0, 1]. */
	private static readonly CHARACTER_END_ALPHA = 1;

	@ViewChild('animationCanvas', { static: true }) canvasRef!: ElementRef<HTMLCanvasElement>;
	private drawingContext!: CanvasRenderingContext2D;
	/**
	 * After changing value animation will start from beggining.
	 */
	resultEntry = input.required<DrawResultEntry>();
	/**
	 * After changing value animation will start from beggining.
	 */
	// XXXW should receive DrawType from parent, and resolved color depending on type.
	circleColor = input.required<string>();
	@Output() finished = new EventEmitter<void>();

	private canvasWidth!: number;
	private canvasHeight!: number;

	private svgImage: HTMLImageElement = new Image();

	private animationSpiralStartTimestamp?: number;
	private animationCharacterStartTimestamp?: number;

	private destroyed: boolean = false;

	private circles = [
		{ alpha: (Math.PI * 2) / 3, startingAlpha: (Math.PI * 2) / 3 },
		{ alpha: (Math.PI * 4) / 3, startingAlpha: (Math.PI * 4) / 3 },
		{ alpha: 0, startingAlpha: 0 }
	];

	ngAfterViewInit(): void {
		this.drawingContext = this.canvasRef.nativeElement.getContext('2d')!;
		this.resizeCanvas();
		this.startAnimation();
	}

	ngOnChanges(): void {
		if (this.drawingContext) {
			this.startAnimation();
		}
	}

	ngOnDestroy(): void {
		this.destroyed = true;
	}

	@HostListener('window:resize')
	resizeCanvas() {
		const canvas = this.canvasRef.nativeElement;
		this.canvasHeight = canvas.height = window.innerHeight;
		this.canvasWidth = canvas.width = window.innerWidth;
	}

	startAnimation() {
		// Preload character asset, to not unecesarly wait after ending of spriral animation.
		this.svgImage.src = getCharacterFileName(this.resultEntry().characterType);

		// Reset last animation start timestamp
		this.animationSpiralStartTimestamp = undefined;
		requestAnimationFrame((timemstamp) => this.animateSpiral(timemstamp));
	}

	private animateSpiral(timemstamp: number) {
		if (this.destroyed) return;
		this.drawingContext.clearRect(0, 0, this.canvasWidth, this.canvasHeight);

		const timeFromAnimationStart = this.animationSpiralStartTimestamp != undefined ? timemstamp - this.animationSpiralStartTimestamp! : 0;
		if (!this.animationSpiralStartTimestamp) this.animationSpiralStartTimestamp = timemstamp;

		if (timeFromAnimationStart >= DrawAnimation.CIRCLE_ANIMATION_DURATION) {
			if (this.svgImage.complete) {
				this.startCharacterAnimation();
			} else {
				// Wait until image loading is completed.
				this.svgImage.onload = () => {
					this.startCharacterAnimation();
				};
			}
			return;
		}

		for (const circle of this.circles) {
			const [x, y] = this.calculateCirclePosition(timeFromAnimationStart, circle.startingAlpha);

			this.drawingContext.beginPath();
			this.drawingContext.arc(x, y, DrawAnimation.CIRCLE_RADIUS, 0, Math.PI * 2);
			const gradient = this.drawingContext.createRadialGradient(x, y, 0, x, y, DrawAnimation.CIRCLE_RADIUS * 2);

			gradient.addColorStop(0, this.circleColor());
			gradient.addColorStop(1, 'transparent');
			this.drawingContext.fillStyle = gradient;
			this.drawingContext.arc(x, y, DrawAnimation.CIRCLE_RADIUS * 3, 0, Math.PI * 2);
			this.drawingContext.fill();
		}

		requestAnimationFrame((t) => this.animateSpiral(t));
	}

	/**
	 * Calculatates new position of circle, using archimedean spiral formula in polar cords.
	 *
	 * Used math formula:
	 * Archimedean spiral in polar cords : r = a + b*alpha
	 * a - beggining radius, b - how much the radius grows when alpha += 1 radian
	 * In cartesian: x = r * cos(alpha), y = r * sin(alpha)
	 */
	private calculateCirclePosition(timeFromAnimationStart: number, startingAlpha: number): [x: number, y: number] {
		// XXXW maybe change function description to be more clear, if it is possible

		// XXXW I think it still a little size dependen to which position will go in time on animation,
		// it's too good if it easy it should be changed. If not leave a comment, and don't do it.
		const begginingRadius = Math.min(this.canvasWidth, this.canvasHeight) / 2;
		const canvasCenterX = this.canvasWidth / 2;
		const canvasCenterY = this.canvasHeight / 2;

		const alphaStep = (timeFromAnimationStart / 1000) * DrawAnimation.ALPHA_STEP_PER_SECOND; // XXXW magic 1000
		const radialChange = -45 * (begginingRadius / 1080); //b in the equation // XXXW magic 1080
		const alpha = startingAlpha + alphaStep;

		const radiusFromCenter = begginingRadius + radialChange * alpha;

		const x = canvasCenterX + radiusFromCenter * Math.cos(alpha);
		const y = canvasCenterY + radiusFromCenter * Math.sin(alpha);
		return [x, y];
	}

	private startCharacterAnimation() {
		this.animationCharacterStartTimestamp = undefined;
		requestAnimationFrame((timemstamp) => this.animateCharacter(timemstamp));
	}

	private animateCharacter(timemstamp: number) {
		if (this.destroyed) return;
		this.drawingContext.clearRect(0, 0, this.canvasWidth, this.canvasHeight);

		const timeFromAnimationStart = this.animationCharacterStartTimestamp != undefined ? timemstamp - this.animationCharacterStartTimestamp! : 0;
		if (!this.animationCharacterStartTimestamp) this.animationCharacterStartTimestamp = timemstamp;

		if (timeFromAnimationStart >= DrawAnimation.CHARACTER_ANIMATION_DURATION) {
			this.finished.emit();
			return;
		}

		const progress = Math.min(1, timeFromAnimationStart / DrawAnimation.CHARACTER_RESIZE_DURATION);

		const canvasCenterX = this.canvasWidth / 2;
		const canvasCenterY = this.canvasHeight / 2;

		//XXXW remove or change this gradient
		// this.drawingContext.beginPath();
		// const gradient = this.drawingContext.createRadialGradient(canvasCenterX, canvasCenterY, 0, canvasCenterX, canvasCenterY, circleRadius * 2);
		// gradient.addColorStop(0, this.circleColor);
		// gradient.addColorStop(1, 'transparent');
		// this.drawingContext.fillStyle = gradient;
		// this.drawingContext.arc(canvasCenterX, canvasCenterY, circleRadius * 3, 0, Math.PI * 2);
		// this.drawingContext.fill();

		this.drawingContext.globalAlpha =
			DrawAnimation.CHARACTER_STARTING_ALPHA + (DrawAnimation.CHARACTER_END_ALPHA - DrawAnimation.CHARACTER_STARTING_ALPHA) * progress;

		const imgWidth = this.svgImage.naturalWidth;
		const imgHeight = this.svgImage.naturalHeight;
		const imgScale =
			DrawAnimation.CHARACTER_STARTING_SIZE + (DrawAnimation.CHARACTER_END_SIZE - DrawAnimation.CHARACTER_STARTING_SIZE) * progress;
		const imgScaledHeight = this.canvasHeight * imgScale;
		// Scale image width to height
		const imgScaledWidth = imgWidth / (imgHeight / imgScaledHeight);
		const imgCenterX = canvasCenterX - imgScaledWidth / 2;
		const imgCenterY = canvasCenterY - imgScaledHeight / 2;

		this.drawingContext.drawImage(this.svgImage, imgCenterX, imgCenterY, imgScaledWidth, imgScaledHeight);

		/*
				Styling in ts so it gets scaled with the circleRadius -
				in css we would need to update the size every frame manually as well,
				and the text wouldnt be on the canvas, but below it or overlayed over it -
				seems more difficult
			*/
		const fontSize = imgScaledWidth * 0.15;
		this.drawingContext.font = `${fontSize}px sans-serif`;
		this.drawingContext.fillStyle = `rgba(255, 255, 255)`;
		this.drawingContext.textAlign = 'center';
		this.drawingContext.textBaseline = 'top';
		const textY = imgCenterY + imgScaledHeight + 10;
		this.drawingContext.fillText('x ' + this.resultEntry().amount.toString(), canvasCenterX, textY);

		requestAnimationFrame((t) => this.animateCharacter(t));
	}
}
