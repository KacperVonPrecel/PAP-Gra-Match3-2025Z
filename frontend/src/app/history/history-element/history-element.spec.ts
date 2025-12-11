import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoryElement } from './history-element';

describe('HistoryElement', () => {
  let component: HistoryElement;
  let fixture: ComponentFixture<HistoryElement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoryElement]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoryElement);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
