import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DrawAnimation } from './draw-animation';

describe('DrawAnimation', () => {
  let component: DrawAnimation;
  let fixture: ComponentFixture<DrawAnimation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DrawAnimation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DrawAnimation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
