import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { registerRequest } from 'src/app/mockdata/auth-mocks';
import { mockLoginRequest } from 'src/app/mockdata/auth-mocks';
import { mockSessionInformation } from 'src/app/mockdata/global-mocks';

describe('Authservice', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should register a user', () => {
    //Act and Check
    service.register(registerRequest).subscribe((response) => {
      expect(response).toBeUndefined();
    });
    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(registerRequest);
    req.flush(null);
  });

  it('should login a user', () => {
    //Act and Check
    service.login(mockLoginRequest).subscribe((response) => {
      expect(response).toEqual(mockSessionInformation);
    });

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockLoginRequest);
    req.flush(mockSessionInformation);
  });
});
