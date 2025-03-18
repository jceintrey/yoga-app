import { HttpClientModule } from '@angular/common/http';
import {
  ComponentFixture,
  fakeAsync,
  flush,
  TestBed,
} from '@angular/core/testing';

import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';
import { RegisterRequest } from '../../interfaces/registerRequest.interface';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';
import { registerRequest } from 'src/app/mockdata/auth-mocks';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  let mockAuthService: Partial<AuthService>;
  let router: Partial<Router>;

  beforeEach(async () => {
    mockAuthService = {
      register: jest.fn().mockReturnValue(of(void 0)),
    };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
      providers: [{ provide: AuthService, useValue: mockAuthService }],
    }).compileComponents();

    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  function submitForm() {
    component.form.setValue(registerRequest);
    const form = fixture.debugElement.query(By.css('form'));
    form.triggerEventHandler('ngSubmit');
  }

  describe('Unit Tests', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should set onError flag to true on Error when register', fakeAsync(() => {
      //Arrange
      mockAuthService.register = jest
        .fn()
        .mockReturnValueOnce(throwError(() => 'error'));
      //Act
      submitForm();
      flush();
      //Check
      expect(component.onError).toBe(true);
    }));
  });

  describe('Integration Tests', () => {
    it('should navigate on Login page if success', () => {
      //Arrange
      const navigateSpy = jest
        .spyOn(router, 'navigate')
        .mockResolvedValue(true);
      //Act
      submitForm();
      //Check
      expect(mockAuthService.register).toHaveBeenCalledWith(registerRequest);
      expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    });
  });
});
