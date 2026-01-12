import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CharactersDetails } from './characters-details';

describe('CharactersDetails', () => {
  let component: CharactersDetails;
  let fixture: ComponentFixture<CharactersDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CharactersDetails]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CharactersDetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
