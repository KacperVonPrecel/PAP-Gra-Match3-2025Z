import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DrawSummary } from './draw-summary';

describe('DrawSummary', () => {
  let component: DrawSummary;
  let fixture: ComponentFixture<DrawSummary>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DrawSummary]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DrawSummary);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
