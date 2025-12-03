import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DrawResultPage } from './draw-result-page';

describe('DrawResultPage', () => {
  let component: DrawResultPage;
  let fixture: ComponentFixture<DrawResultPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DrawResultPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DrawResultPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
