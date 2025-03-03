/// <reference types="Cypress" />
import { admin } from './mockdata';

describe('Login spec', () => {
  afterEach(() => {
    cy.logout();
  });

  it('Should Login successfully when valid credentials are used', () => {
    cy.login(admin);

    cy.url().should('include', '/sessions');
  });

  it('Should Logout successfully when /logout is visited', () => {
    cy.login(admin);

    cy.url().should('include', '/sessions');
    cy.visit('/logout');
    cy.url().should('not.include', '/sessions');
  });

  it('should mark the email field as invalid and disable the submit button when an incorrectly formatted email is typed', () => {
    cy.visit('/login');
    cy.get('input[formControlName=email]').type('notavalidemailformat');
    cy.get('input[formControlName=email]').should('have.class', 'ng-invalid');
    cy.get('button[type=submit]')
      .should('be.disabled')
      .and('have.attr', 'disabled');
  });

  it('should mark the password field as invalid and disable the submit button when password is empty', () => {
    cy.visit('/login');
    cy.get('input[formControlName=password]').clear;
    cy.get('input[formControlName=password]').should(
      'have.class',
      'ng-invalid'
    );
    cy.get('button[type=submit]')
      .should('be.disabled')
      .and('have.attr', 'disabled');
  });
});
