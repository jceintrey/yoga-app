import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';
import { expect } from '@jest/globals';

import { MeComponent } from './me.component';
import { of } from 'rxjs';
import { User } from 'src/app/interfaces/user.interface';
import { UserService } from 'src/app/services/user.service';
import { Router } from '@angular/router';
import { mockUser } from 'src/app/mockdata/global-mocks';
import { mockSessionService } from 'src/app/mockdata/global-mocks';
import { mockSession } from 'src/app/mockdata/session-mocks';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: UserService;
  let sessionService: SessionService;
  let snackBar: MatSnackBar;
  let router: Router;

  const mockSessionService = {
    sessionInformation: mockSession,
    logOut: jest.fn(),
  };

  const mockUserService = {
    getById: jest.fn().mockReturnValue(of(mockUser)),
    delete: jest.fn().mockReturnValue(of(null)),
  };

  const mockSnackBar = {
    open: jest.fn(),
  };

  const mockRouter = {
    navigate: jest.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;

    userService = TestBed.inject(UserService);
    sessionService = TestBed.inject(SessionService);
    snackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  describe('Unit Tests', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fetch user data on init', () => {
      //Act and Check
      expect(userService.getById).toHaveBeenCalledWith('1');
      expect(component.user).toEqual(mockUser);
    });
  });

  describe('Integration Tests', () => {
    it('should delete user and log out', () => {
      //Act
      component.delete();
      //Check
      expect(userService.delete).toHaveBeenCalledWith('1');

      expect(snackBar.open).toHaveBeenCalledWith(
        'Your account has been deleted !',
        'Close',
        { duration: 3000 }
      );
      expect(sessionService.logOut).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/']);
    });
  });
});
