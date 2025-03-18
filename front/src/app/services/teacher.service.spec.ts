import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

import { TeacherService } from './teacher.service';
import { mockTeacher } from '../mockdata/global-mocks';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(TeacherService);
  });

  afterEach(() => {
    httpMock.verify();
  });
  describe('Unit Tests', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });
    it('should return all teachers', () => {
      //Arrange
      const teachers = [mockTeacher];
      //Act
      service.all().subscribe((response) => {
        //Check
        expect(response).toEqual(teachers);
      });
      const req = httpMock.expectOne('api/teacher');
      expect(req.request.method).toBe('GET');
      req.flush(teachers);
    });
    it('should return teacher detail by their Id', () => {
      //Arrange
      const id = String(mockTeacher.id);
      //Act
      service.detail(id).subscribe((response) => {
        //Check
        expect(response).toEqual(mockTeacher);
      });
      const req = httpMock.expectOne(`api/teacher/${id}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockTeacher);
    });
  });
});
