import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { Session } from '../interfaces/session.interface';
import { mockSession } from 'src/app/mockdata/session-mocks';

describe('SessionsService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should delete session on delete call', () => {
    //Arrange
    const deleteResponse = {};
    const id = '1';
    //Act
    service.delete(id).subscribe((reponse) => {
      //Check
      expect(reponse).toEqual(deleteResponse);
    });

    const req = httpMock.expectOne(`api/session/${id}`);
    expect(req.request.method).toBe('DELETE');

    req.flush(deleteResponse);
  });

  it('should create session on create call', () => {
    //Act
    service.create(mockSession).subscribe((response) => {
      //Check
      expect(response).toEqual(mockSession);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    req.flush(mockSession);
  });

  it('should update session on update call', () => {
    //Arrange
    const id = '1';
    //Act
    service.update(id, mockSession).subscribe((response) => {
      //Check
      expect(response).toEqual(mockSession);
    });

    const req = httpMock.expectOne(`api/session/${id}`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockSession);
  });

  it('should not allow the id to be modified in update call', () => {
    //Arrange
    const originalId = '1';
    const newId = '2';
    const sessionToUpdate: Session = { ...mockSession, id: Number(newId) };
    //Act
    service.update(originalId, sessionToUpdate).subscribe((response) => {
      //Check
      expect(response).toEqual(mockSession);
    });

    const req = httpMock.expectOne(`api/session/${originalId}`);
    expect(req.request.method).toBe('PUT');
    expect(String(mockSession.id)).toBe(originalId);

    req.flush(mockSession);
  });

  it('should participate to a session', () => {
    //Arrange
    const id = '1';
    const userId = '1';
    //Act
    service.participate(id, userId).subscribe((response) => {
      //Check
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne(`api/session/${id}/participate/${userId}`);
    expect(req.request.method).toBe('POST');
    req.flush(null);
  });
  it('should remove a participation to a session', () => {
    //Arrange
    const id = '1';
    const userId = '1';
    //Act
    service.unParticipate(id, userId).subscribe((response) => {
      //Check
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne(`api/session/${id}/participate/${userId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
