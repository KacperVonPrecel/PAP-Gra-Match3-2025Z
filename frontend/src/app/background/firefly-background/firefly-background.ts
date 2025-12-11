import { AfterViewInit, Component, ElementRef, HostListener, ViewChild } from '@angular/core';

@Component({
	selector: 'app-firefly-background',
	imports: [],
	templateUrl: './firefly-background.html',
	styleUrl: './firefly-background.scss'
})
export class FireflyBackground implements AfterViewInit {
	// XXXW particles number maybe depended on canvas size?
	private static readonly PARTICLES_COUNT = 40; 
	@ViewChild('fireflyCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;
	private drawingContext!: CanvasRenderingContext2D;
	private particles: Particle[] = [];
	private width!: number;
	private height!: number;
	private lastAnimationTimestamp?: number;

	ngAfterViewInit() {
		const canvas = this.canvasRef.nativeElement;
		this.drawingContext = canvas.getContext('2d')!;
		this.readCanvasSize();
		this.createParticles();

		// Request first animation frame, than animate function will request next animation frame.
		requestAnimationFrame((timestamp) => this.animate(timestamp));
	}

	private readCanvasSize() {
		const canvas = this.canvasRef.nativeElement;
		this.width = canvas.width = window.innerWidth;
		this.height = canvas.height = window.innerHeight;
	}

	@HostListener('window:resize')
	handleWindowResize() {
		this.readCanvasSize();

		// Create new list of particles because when screen was small, and then resized to being larger than
		// all particles will be on small area. And in reverse way the particles can be outside of screen after resizing
		// window to be smaller, so the best way is create new particles to change it positon.
		// It doesn't call animate(), because canvas will be updated when rendering new frame, and it's better because browser.
		// possibly can send multiple time resize event.
		this.createParticles();
	}
	/**
	 * Method clear actual particles list and create new one.
	 * Before calling this method, {@link width} and {@link height} need to be set by {@link readCanvasSize()}.
	 */
	private createParticles() {
		this.particles = [];
		// XXXW set radius and speeds dependeding on canvas size.

		for (let i = 0; i < FireflyBackground.PARTICLES_COUNT; i++) {
			this.particles.push({
				x: Math.random() * this.width,
				y: Math.random() * this.height,
				radius: Math.random() * 3 + 2,
				horizontal_speed: (Math.random() - 0.5) * 0.5,
				vertical_speed: (Math.random() - 0.5) * 0.5,
				glow_opacity: Math.random() * 0.7 + 0.3
			});
		}
	}

	/**
	 * Function handles drawing new frame of animation.
	 * Before calling this method, {@link width} and {@link height} need to be set by {@link readCanvasSize()}.
	 */
	private animate(animation_timestamp: number) {
		const timeChange = this.lastAnimationTimestamp == undefined ? 0 : animation_timestamp - this.lastAnimationTimestamp;
		this.lastAnimationTimestamp = animation_timestamp;

		/// XXXW calculate step by timeChange
		this.drawingContext.clearRect(0, 0, this.width, this.height);
		this.particles.forEach((p) => {
			p.x += p.horizontal_speed;
			p.y += p.vertical_speed;

			if (p.x < 0 || p.x > this.width) p.horizontal_speed *= -1;
			if (p.y < 0 || p.y > this.height) p.vertical_speed *= -1;

			const gradient = this.drawingContext.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.radius * 3);
			gradient.addColorStop(0, `rgba(240, 226, 163, ${p.glow_opacity})`);
			gradient.addColorStop(1, 'transparent');

			this.drawingContext.fillStyle = gradient;

			this.drawingContext.beginPath();
			this.drawingContext.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
			this.drawingContext.fill();
		});
		requestAnimationFrame((timestamp) => this.animate(timestamp));
	}
}

interface Particle {
	x: number;
	y: number;
	/** In pixels */
	radius: number;
	/** In pixels/second */
	horizontal_speed: number;
	/** In pixels/second */
	vertical_speed: number;
	glow_opacity: number;
}
