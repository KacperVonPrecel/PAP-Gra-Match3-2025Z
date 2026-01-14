import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FindingMatch } from './finding-match';

describe('FindingMatch', () => {
  let component: FindingMatch;
  let fixture: ComponentFixture<FindingMatch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FindingMatch]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FindingMatch);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
