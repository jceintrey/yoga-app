import { HttpClientModule } from '@angular/common/http';
import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
} from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { LoginRequest } from '../../interfaces/loginRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { By } from '@angular/platform-browser';
import { mockSessionInformation } from '../../../../mockdata/global-mocks';
import { mockLoginRequest } from 'src/app/mockdata/auth-mocks';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: Partial<AuthService>;
  let mockSessionService: Partial<SessionService>;
  let mockRouter: Partial<Router>;

  beforeEach(async () => {
    mockAuthService = {
      login: jest.fn().mockReturnValue(of(mockSessionInformation)),
    };
    mockSessionService = {
      logIn: jest.fn(),
    };

    mockRouter = {
      navigate: jest.fn(),
    };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter },
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should submit the form and navigate on success', () => {
    //Arrange
    component.form.setValue(mockLoginRequest);
    //Act
    component.submit();
    //Check
    expect(mockAuthService.login).toHaveBeenCalledWith(mockLoginRequest);

    expect(mockSessionService.logIn).toHaveBeenCalledWith(
      mockSessionInformation
    );

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });
  it('should set error flag to true on error', () => {
    //Arrange
    mockAuthService.login = jest.fn().mockReturnValueOnce(throwError('error'));
    component.form.setValue(mockLoginRequest);
    //Act
    const form = fixture.debugElement.query(By.css('form'));
    form.triggerEventHandler('ngSubmit');

    fixture.detectChanges();
    //Check
    expect(component['onError']).toBe(true);
  });
});
