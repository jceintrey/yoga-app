/// <reference types="Cypress" />

import {
  admin,
  anothersamplesession,
  jdoe,
  sessions,
  teachers,
} from './mockdata';
/**
 * Sessions admin test suite
 */
// should an admin be able to show the session list
// should show an error message on session create when the mandatory name field is missing
// should show an error message on session create when the mandatory date field is missing
// should an admin be able to create a session
// should an admin be able to edit a session
// Should an admin be able to delete a session
describe('Sessions admin test suite', () => {
  beforeEach(() => {
    const user = admin;
    if (!admin.admin) throw new Error('This test suite is only for admins');
    cy.login(user);
  });
  afterEach(() => {
    cy.logout();
  });

  it('should an admin be able to show the session list', () => {
    cy.url().should('include', '/sessions');
    cy.intercept('GET', `/api/session`, sessions).as('sessionIntercept');
    cy.get('.item mat-card-title').should('have.length', sessions.length);
  });

  it('should show an error message on session create when the mandatory name field is missing', () => {
    cy.intercept('GET', '/api/teacher', {
      body: teachers,
    }).as('teacherIntercept');

    cy.get('button[routerLink=create]').click();
    cy.fillSessionForm(anothersamplesession);
    cy.get('input[formControlName=name]').clear();
    cy.get('input[formControlName=name]').should('have.class', 'ng-invalid');
  });

  it('should show an error message on session create when the mandatory date field is missing', () => {
    cy.intercept('GET', '/api/teacher', {
      body: teachers,
    }).as('teacherIntercept');

    cy.get('button[routerLink=create]').click();
    cy.fillSessionForm(anothersamplesession);
    cy.get('input[formControlName=date]').clear();
    cy.get('input[formControlName=date]').should('have.class', 'ng-invalid');
  });

  it('should an admin be able to create a session', () => {
    cy.url().should('include', '/sessions');
    cy.intercept('GET', '/api/teacher', {
      body: teachers,
    });

    cy.get('button[routerLink=create]').click();

    cy.intercept('POST', '/api/session', { statusCode: 200 });
    cy.fillSessionForm(anothersamplesession);

    cy.get('button[type=submit]').click();

    cy.contains('Session created !').should('be.visible');
    cy.get('.mat-simple-snackbar')
      .should('exist')
      .and('contain', 'Session created !');

    cy.url().should('include', '/sessions');
  });
  it('should an admin be able to edit a session', () => {
    const sessionId = 3;
    const session = sessions.find((s) => s.id === sessionId);
    const teacher = session
      ? teachers.find((t) => t.id === session.teacher_id)
      : undefined;

    if (!session || !teacher) {
      throw new Error('Session or teacher not found');
    }
    cy.intercept('GET', `/api/session/${sessionId}`, {
      body: session,
    }).as('sessionIntercept');
    cy.intercept('GET', `/api/teacher`, {
      body: teacher,
    }).as('teacherIntercept');

    cy.contains('.item mat-card-title', session.name)
      .parents('.item')
      .find('mat-card-actions button')
      .eq(1)
      .should('be.visible')
      .click();

    cy.wait('@sessionIntercept');
    cy.wait('@teacherIntercept');

    cy.url().should('include', `/sessions/update/${sessionId}`);
    cy.get('input[formControlName=name]').should('have.value', session.name);
  });

  it('Should an admin be able to delete a session', () => {
    const sessionId = 3;
    const session = sessions.find((s) => s.id === sessionId);
    const teacher = session
      ? teachers.find((t) => t.id === session.teacher_id)
      : undefined;

    if (!session || !teacher) {
      throw new Error('Session or teacher not found');
    }
    cy.intercept('GET', `/api/session/${sessionId}`, {
      body: session,
    }).as('sessionIntercept');
    cy.intercept('GET', `/api/teacher/${teacher.id}`, {
      body: teacher,
    }).as('teacherIntercept');

    cy.contains('.item mat-card-title', session.name)
      .parents('.item')
      .find('mat-card-actions button')
      .eq(0)
      .should('be.visible')
      .click();

    cy.wait('@sessionIntercept');
    cy.wait('@teacherIntercept');

    cy.intercept('DELETE', `/api/session/${sessionId}`, {
      body: teacher,
    }).as('delete');

    cy.get('button[data-testid=delete-button]').should('exist').click();

    cy.wait('@delete');
    cy.contains('Session deleted !').should('be.visible');
    cy.url().should('include', '/sessions');
  });
});

/**
 * Session user test suite
 */
// should a simple user or admin be able to click on detail button of a session and show detailed informations
// Should a simple user not be able to see and click on Create button
// should a simple user be able to participate to a session if not already participating

describe('Session user test suite', () => {
  beforeEach(() => {
    // const user = admin;
    // cy.login(user);
  });
  afterEach(() => {
    cy.logout();
  });

  it('should a simple user or admin be able to click on detail button of a session and show detailed informations', () => {
    const users = [jdoe, admin];
    users.forEach((user) => {
      cy.logout();
      cy.login(user);
      const sessionId = 2;
      const session = sessions.find((s) => s.id === sessionId);
      const teacher = session
        ? teachers.find((t) => t.id === session.teacher_id)
        : undefined;

      if (!session || !teacher) {
        throw new Error('Session or teacher not found');
      }
      cy.intercept('GET', `/api/session/${sessionId}`, {
        body: session,
      }).as('sessionIntercept');
      cy.intercept('GET', `/api/teacher/${teacher.id}`, {
        body: teacher,
      }).as('teacherIntercept');

      cy.contains('.item mat-card-title', session.name)
        .parents('.item')
        .find('mat-card-actions button')
        .first()
        .should('be.visible')
        .click();

      cy.wait('@sessionIntercept');
      cy.wait('@teacherIntercept');

      cy.url().should('include', `/sessions/detail/${sessionId}`);

      cy.contains(session.name).should('be.visible');
    });
  });

  it('Should a simple user not be able to see and click on Create button', () => {
    cy.logout();
    cy.login(jdoe);
    cy.get('button[routerLink=create]').should('not.exist');
  });

  it('should a simple user be able to participate to a session if not already participating', () => {
    cy.logout();
    cy.login(jdoe);

    if (jdoe.admin) throw new Error('User should not be admin for that test');

    const sessionId = 3;
    const session = sessions.find((s) => s.id === sessionId);
    if (session) {
      session.users = session.users.filter((userId) => userId !== jdoe.id);
    }

    const teacher = session
      ? teachers.find((t) => t.id === session.teacher_id)
      : undefined;

    if (!session || !teacher) {
      throw new Error('Session or teacher not found');
    }
    cy.intercept('GET', `/api/session/${sessionId}`, {
      body: session,
    }).as('sessionIntercept');
    cy.intercept('GET', `/api/teacher/${teacher.id}`, {
      body: teacher,
    }).as('teacherIntercept');

    cy.contains('.item mat-card-title', session.name)
      .parents('.item')
      .find('mat-card-actions button')
      .eq(0)
      .should('be.visible')
      .click();

    cy.intercept('POST', `/api/session/${sessionId}/participate/${jdoe.id}`, {
      body: teacher,
    }).as('participate');

    cy.get('button[data-testid=participate-button]').should('exist').click();

    cy.wait('@participate');
  });
});
