import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DrawResultControllerPage } from './draw-result-controller-page';

describe('DrawResultPage', () => {
	let component: DrawResultControllerPage;
	let fixture: ComponentFixture<DrawResultControllerPage>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [DrawResultControllerPage]
		}).compileComponents();

		fixture = TestBed.createComponent(DrawResultControllerPage);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	//XXXW tests
	// - for empty route state
	// - that read properly data  from route state
	// - mock animationDraw check if animationFinish callback activate next entry or summary if ended
	// - showing summary navigate out after timeout
	// - skip navigate out if is on summary
});
