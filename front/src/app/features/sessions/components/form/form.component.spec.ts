import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { NgZone } from '@angular/core';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

import { mockSession } from '../../../../mockdata/session-mocks';
import { mockSessionService } from 'src/app/mockdata/global-mocks';
import { AppComponent } from 'src/app/app.component';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Partial<Router>;
  let activatedRoute: ActivatedRoute;
  let sessionApiService: Partial<SessionApiService>;
  let ngZone: NgZone;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: AppComponent },
        ]),

        HttpClientTestingModule,
        NoopAnimationsModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        SessionApiService,
      ],
      declarations: [FormComponent],
    }).compileComponents();

    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ngZone = TestBed.inject(NgZone);
    sessionApiService = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should  navigate to sessions when admin is false', () => {
    // Arrange
    const navigateSpy = jest.spyOn(router, 'navigate');
    mockSessionService.sessionInformation.admin = false;

    ngZone.run(() => {
      // Arrange
      component.ngOnInit();
      //Check
      expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
    });
  });

  it('should call initForm with details when the route contains update', () => {
    //Arrange
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    const id = '1';
    jest.spyOn(router, 'url', 'get').mockReturnValue('update');
    jest.spyOn(activatedRoute.snapshot.paramMap, 'get').mockReturnValue(id);

    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));

    ngZone.run(() => {
      //Act
      component.ngOnInit();
      //check
      expect(component.onUpdate).toBe(true);
      expect(component['id']).toBe(id);

      if (component.sessionForm) {
        expect(component.sessionForm.get('name')?.value).toEqual(
          mockSession.name
        );
      } else throw new Error('sessionForm is undefined');
    });
  });

  it('should create empty sessionForm when the route is created', () => {
    //Act
    component.ngOnInit();
    //Check
    expect(component.sessionForm).toBeTruthy();
    expect(component.sessionForm?.get('name')?.value).toEqual('');
    expect(component.sessionForm?.get('date')?.value).toEqual('');
    expect(component.sessionForm?.get('teacher_id')?.value).toEqual('');
    expect(component.sessionForm?.get('description')?.value).toEqual('');
  });

  it('should submit session Form values when onUpdate', () => {
    //Arrange
    component.onUpdate = true;
    component['id'] = '1';

    ngZone.run(() => {
      //Act
      component.submit();
      //Check
      const req = httpMock.expectOne('api/session/1');
      expect(req.request.method).toEqual('PUT');
      req.flush(mockSession);
    });
  });
});
