import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoryCharacterComponent } from './history-character-component';

describe('HistoryCharacterComponent', () => {
  let component: HistoryCharacterComponent;
  let fixture: ComponentFixture<HistoryCharacterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoryCharacterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoryCharacterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
