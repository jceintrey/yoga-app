/// <reference types="Cypress" />

import {
  admin,
  anothersamplesession,
  jdoe,
  sessions,
  teachers,
} from './mockdata';

describe('Session spec', () => {
  beforeEach(() => {
    const user = admin;
    cy.login(user);
  });
  afterEach(() => {
    cy.logout();
  });
  it('should show session list', () => {
    cy.url().should('include', '/sessions');
  });

  it('should an admin be able to create a session', () => {
    cy.url().should('include', '/sessions');
    cy.intercept('GET', '/api/teacher', {
      body: teachers,
    });

    cy.get('button[routerLink=create]').click();

    cy.intercept('POST', '/api/session', { statusCode: 200 });
    cy.get('input[formControlName=name]').type(anothersamplesession.name);
    cy.get('input[formControlName=date]').type(anothersamplesession.date);
    cy.get('mat-select[formControlName=teacher_id]').click();

    cy.get('mat-option')
      .contains(
        `${teachers[anothersamplesession.teacher_id].firstName} ${
          teachers[anothersamplesession.teacher_id].lastName
        }`
      )
      .click();

    cy.get('textarea[formControlName=description]').type(
      anothersamplesession.description
    );

    cy.get('button[type=submit]').click();

    cy.contains('Session created !').should('be.visible');
    cy.get('.mat-simple-snackbar')
      .should('exist')
      .and('contain', 'Session created !');

    cy.url().should('include', '/sessions');
  });

  it('should an admin be able to click on detail button of a session', () => {
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

  it('should an admin be able to click on edit button of a session', () => {
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

  it('Should not be able to see and click on Create button if not an admin', () => {
    cy.logout();
    cy.login(jdoe);
    cy.get('button[routerLink=create]').should('not.exist');
  });

  it.only('should a user be able to participate to a session if not already participating and if is not admin', () => {
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
