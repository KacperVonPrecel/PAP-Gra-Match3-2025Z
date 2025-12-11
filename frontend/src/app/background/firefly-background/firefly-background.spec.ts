import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FireflyBackground } from './firefly-background';

describe('FireflyBackground', () => {
  let component: FireflyBackground;
  let fixture: ComponentFixture<FireflyBackground>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FireflyBackground]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FireflyBackground);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
