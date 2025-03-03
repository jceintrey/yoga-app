import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { mockSessionInformation } from '../mockdata/global-mocks';


describe('SessionService', () => {
  let service: SessionService;
  


  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should log in a user and return void', async () => {
    //Act
    service.logIn(mockSessionInformation);
    //Check
    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockSessionInformation);
  });

});
