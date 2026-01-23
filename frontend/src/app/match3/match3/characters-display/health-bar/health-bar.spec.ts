import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HealthBar } from './health-bar';

describe('HealthBar', () => {
  let component: HealthBar;
  let fixture: ComponentFixture<HealthBar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HealthBar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HealthBar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
