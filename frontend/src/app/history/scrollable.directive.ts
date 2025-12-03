// scrollable.directive.ts
import { Directive, ElementRef, OnInit, Output, EventEmitter, HostListener } from '@angular/core';

@Directive({
	selector: '[appScrollable]',
	standalone: true
})
export class ScrollableDirective implements OnInit {
	@Output() hasOverflow = new EventEmitter<boolean>();
	@Output() scrollState = new EventEmitter<boolean>();

	constructor(private el: ElementRef) {}

	ngOnInit() {
		this.emitScrollState();

		window.addEventListener('resize', () => this.emitScrollState());
	}

	@HostListener('scroll')
	onScroll() {
		this.emitScrollState();
	}

	emitScrollState() {
		const element = this.el.nativeElement;

		this.scrollState.emit(Math.abs(element.scrollHeight - element.scrollTop - element.clientHeight) < 300);
	}
}
