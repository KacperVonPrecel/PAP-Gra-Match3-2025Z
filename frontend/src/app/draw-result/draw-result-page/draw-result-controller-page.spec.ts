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
});
