import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectTeam } from './select-team';

describe('SelectTeam', () => {
  let component: SelectTeam;
  let fixture: ComponentFixture<SelectTeam>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectTeam]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectTeam);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
