import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RankingDialog } from './ranking-dialog';

describe('RankingDialog', () => {
  let component: RankingDialog;
  let fixture: ComponentFixture<RankingDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RankingDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RankingDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
