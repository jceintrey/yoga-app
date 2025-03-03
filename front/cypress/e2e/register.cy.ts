/// <reference types="Cypress" />

import { jdoe } from './mockdata';

describe('Register spec', () => {
  it('Should register successfully when the register form is correctly filled', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', { statusCode: 200 });

    cy.get('input[formControlName=firstName]').type(jdoe.firstName);
    cy.get('input[formControlName=lastName]').type(jdoe.lastName);
    cy.get('input[formControlName=email]').type(jdoe.username);
    cy.get('input[formControlName=password]').type(
      `${jdoe.password}{enter}{enter}`
    );
    cy.url().should('include', '/login');
  });
  it('should mark the email field as invalid and disable the submit button when an incorrectly formatted email is entered', () => {
    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type(jdoe.firstName);
    cy.get('input[formControlName=lastName]').type(jdoe.lastName);
    cy.get('input[formControlName=email]').type('notavalidemailformat');
    cy.get('input[formControlName=password]').type(
      `${jdoe.password}{enter}{enter}`
    );
    cy.get('input[formControlName=email]').should('have.class', 'ng-invalid');
    cy.get('button[type=submit]')
      .should('be.disabled')
      .and('have.attr', 'disabled');
  });

  const fields = ['firstName', 'lastName', 'email', 'password'];
  fields.forEach((field) => {
    it(`should mark the ${field} field as invalid and disable the submit button when ${field} field is null`, () => {
      cy.visit('/register');

      fields.forEach((f) => {
        if (field == f) {
          cy.get(`input[formControlName=${f}`).clear();
        } else {
          cy.get(`input[formControlName=${f}`).type(`jdoe.${f}`);
        }
      });

      cy.get(`input[formControlName=${field}]`).should(
        'have.class',
        'ng-invalid'
      );
      cy.get('button[type=submit]')
        .should('be.disabled')
        .and('have.attr', 'disabled');
    });
  });
});
