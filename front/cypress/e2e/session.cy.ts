/// <reference types="Cypress" />

import {
  jdoe,
  admin,
  sessions,
  anothersamplesession,
  teachers,
} from './mockdata';

describe('Session spec', () => {
  it('should show session list', () => {
    cy.login(admin);
    cy.url().should('include', '/sessions');
  });

  it('should an admin be able to create a session', () => {
    const user = admin;
    cy.login(user);
    cy.url().should('include', '/sessions');
    //first get the teacher list
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
  });
});
