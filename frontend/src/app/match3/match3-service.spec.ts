import { TestBed } from '@angular/core/testing';

import { Match3Service } from './match3-service';

describe('Match3Service', () => {
  let service: Match3Service;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Match3Service);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
