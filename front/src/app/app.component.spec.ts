import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { expect } from '@jest/globals';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';

import { of } from 'rxjs';
import { Router } from '@angular/router';
import { SessionService } from './services/session.service';

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let app: AppComponent;
  let sessionService: SessionService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, MatToolbarModule, RouterTestingModule],
      declarations: [AppComponent],
      providers: [SessionService],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    app = fixture.componentInstance;

    fixture.detectChanges();
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
  });

  it('should create the app', () => {
    expect(app).toBeTruthy();
  });

  it('should return is logged as a boolean Observable', () => {
    //Arrange
    jest.spyOn(sessionService, '$isLogged').mockReturnValue(of(true));
    //Act and check
    app.$isLogged().subscribe((isLogged) => expect(isLogged).toBe(true));
  });

  it('should logout and navigate to root path', async () => {
    //Arrange
    const logoutSpy = jest.spyOn(sessionService, 'logOut');
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    //Act
    await app.logout();
    await fixture.whenStable();
    //Check
    expect(logoutSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  });
});
