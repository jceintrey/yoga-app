import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { mockUser } from '../mockdata/global-mocks';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });
  afterEach(() => {
    httpMock.verify();
  });
  describe('Unit Tests', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });
    it('should return user by their Id', () => {
      //Arrange
      const id = '1';
      //Act
      service.getById(id).subscribe((response) => {
        //Check
        expect(response).toEqual(mockUser);
      });

      const req = httpMock.expectOne(`api/user/${id}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockUser);
    });
    it('should delete user by their Id', () => {
      //Arrange
      const id = '1';
      const response = '{}';
      //Act
      service.delete(id).subscribe((response) => {
        //Check
        expect(response).toEqual(response);
      });

      const req = httpMock.expectOne(`api/user/${id}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(response);
    });
  });
});
