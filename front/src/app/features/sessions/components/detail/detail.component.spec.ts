import { HttpClientModule } from '@angular/common/http';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import {
  async,
  ComponentFixture,
  fakeAsync,
  flush,
  TestBed,
  tick,
} from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { DetailComponent } from './detail.component';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { By } from '@angular/platform-browser';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TeacherService } from 'src/app/services/teacher.service';

import { mockSessionService, mockTeacher } from 'src/app/mockdata/global-mocks';
import { mockSession } from 'src/app/mockdata/session-mocks';

describe('DetailComponent Test suite', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let router: Router;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        NoopAnimationsModule,
        HttpClientModule,
        MatSnackBarModule,
        MatCardModule,
        MatIconModule,
        ReactiveFormsModule,
      ],
      declarations: [DetailComponent],
      providers: [
        SessionApiService,
        TeacherService,
        { provide: SessionService, useValue: mockSessionService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(DetailComponent);
    sessionApiService = TestBed.inject(SessionApiService);
    teacherService = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Unit Tests', () => {
    it('should create the component', () => {
      expect(component).toBeTruthy();
    });

    it('should navigate to prevous window when component.back is called', () => {
      // Arrange
      const historySpy = jest.spyOn(window.history, 'back');

      // Act
      component.back();

      // Check
      expect(historySpy).toHaveBeenCalled();
    });
  });

  describe('Integration Tests', () => {
    it('should fetch session details and update relatives properties when ngOnInit is invoked', async () => {
      // Arrange
      component.sessionId = String(mockSession.id);
      component.userId = String(mockSession.users[0]);
      const detailSpy = jest
        .spyOn(sessionApiService, 'detail')
        .mockReturnValue(of(mockSession));
      const teacherServiceDetailSpy = jest
        .spyOn(teacherService, 'detail')
        .mockReturnValue(of(mockTeacher));

      // Act
      component.ngOnInit();
      await fixture.whenStable();
      fixture.detectChanges();

      // Check
      expect(detailSpy).toHaveBeenCalledWith(String(mockSession.id));
      expect(component.session).toEqual(mockSession);
      expect(component.isParticipate).toBe(true);
      expect(teacherServiceDetailSpy).toHaveBeenCalledWith(
        mockTeacher.id.toString()
      );
      expect(component.teacher).toEqual(mockTeacher);
    });
    it('should call sessionApiService.participate and fetch sessions when participate button is clicked', () => {
      // Arrange

      component.isAdmin = false;
      component.isParticipate = false;
      component.session = mockSession;
      component.sessionId = String(mockSession.id);
      component.userId = String(mockSession.users[0]);

      const participateSpy = jest
        .spyOn(sessionApiService, 'participate')
        .mockReturnValue(of(void 0));
      const detailSessionSpy = jest
        .spyOn(sessionApiService, 'detail')
        .mockReturnValue(of(mockSession));

      //Act
      fixture.detectChanges();
      clickParticipate();

      // Check
      expect(participateSpy).toHaveBeenCalledWith(
        component.sessionId,
        component.userId
      );
      expect(component.isParticipate).toBe(true);
      expect(detailSessionSpy).toHaveBeenCalled();
      expect(component.session).toEqual(mockSession);
    });

    it('should call sessionApiService.unparticipate and fetch session when unparticipate button is clicked', async () => {
      // Arrange
      component.isAdmin = false;
      component.isParticipate = true;
      component.session = mockSession;
      component.sessionId = String(mockSession.id);
      component.userId = String(mockSession.users[0]);

      jest.spyOn(component, 'fetchSession');
      const unParticipateSpy = jest
        .spyOn(sessionApiService, 'unParticipate')
        .mockReturnValue(of(undefined));
      const detailSessionSpy = jest
        .spyOn(sessionApiService, 'detail')
        .mockReturnValue(
          of({
            ...mockSession,
            users: mockSession.users.filter(
              (user) => user !== mockSession.users[0]
            ),
          })
        );

      // Act
      fixture.detectChanges();
      clickParticipationButton('unparticipate');
      await fixture.whenStable();
      fixture.detectChanges();

      // Check
      expect(unParticipateSpy).toHaveBeenCalledWith(
        component.sessionId,
        component.userId
      );
      expect(component.fetchSession).toHaveBeenCalled();
      expect(detailSessionSpy).toHaveBeenCalled();

      expect(component.session.id).toEqual(mockSession.id);
      expect(component.session.users).not.toContain(mockSession.users[0]);
      expect(component.isParticipate).toBe(false);
    });

    it('should delete the session when delete is called', fakeAsync(() => {
      // Arrange
      component.isAdmin = true;
      component.session = mockSession;
      const snackBarSpy = jest.spyOn(component['matSnackBar'], 'open');
      const navigateSpy = jest
        .spyOn(router, 'navigate')
        .mockResolvedValue(true);
      const deleteSpy = jest
        .spyOn(sessionApiService, 'delete')
        .mockReturnValue(of(null));

      // Act
      clickDelete();
      flush();

      // Check
      expect(deleteSpy).toHaveBeenCalledWith(component.sessionId);
      expect(snackBarSpy).toHaveBeenCalledWith('Session deleted !', 'Close', {
        duration: 3000,
      });

      expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
    }));
  });

  function clickDelete() {
    if (!component.isAdmin)
      throw new Error('Only Admin can click on delete button');
    if (!component.session)
      throw new Error('Cannot delete session because session is undefined');
    fixture.detectChanges();

    const deleteButton = fixture.debugElement.query(
      By.css('[data-testid="delete-button"]')
    );

    if (!deleteButton) throw new Error('Delete button not found in the DOM');

    deleteButton.triggerEventHandler('click', {});
  }

  function clickParticipate() {
    if (component.isAdmin)
      throw new Error('Admin cannot click on participate button');
    if (component.isParticipate) throw new Error('User is already participate');

    const participateButton = fixture.debugElement.query(
      By.css('[data-testid="participate-button"]')
    );

    if (!participateButton)
      throw new Error('Participate button not found in the DOM');

    participateButton.triggerEventHandler('click', {});
  }

  function clickParticipationButton(
    participation: 'participate' | 'unparticipate'
  ) {
    if (component.isAdmin)
      throw new Error('Admin cannot click on participation button');
    let buttonTestId = '';
    switch (participation) {
      case 'participate':
        if (component.isParticipate)
          throw new Error(
            'Cannot add user participation because user already participate'
          );
        buttonTestId = 'participate-button';
        break;
      case 'unparticipate':
        if (!component.isParticipate)
          throw new Error(
            'Cannot remove user participation because user do not participate'
          );
        buttonTestId = 'unparticipate-button';
        break;
      default:
        throw new Error('Invalid participation type');
    }
    const button = fixture.debugElement.query(
      By.css(`[data-testid="${buttonTestId}"]`)
    );
    if (!button)
      throw new Error(`Button with id ${buttonTestId} not found in the DOM`);

    button.triggerEventHandler('click', {});
  }
});
