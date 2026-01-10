import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RankingElement } from './ranking-element';

describe('RankingElement', () => {
  let component: RankingElement;
  let fixture: ComponentFixture<RankingElement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RankingElement]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RankingElement);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
