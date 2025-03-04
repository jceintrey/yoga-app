/// <reference types="Cypress" />
import { jdoe } from './mockdata';

describe('Me spec', () => {
  beforeEach(() => {
    cy.login(jdoe);
  });
  it.only('Should show me informations when navigate on me', () => {
    // cy.visit('/me');
    cy.intercept('GET', `/api/user/${jdoe.id}`, {
      body: jdoe,
    }).as('meIntercept');
    cy.get('span[routerLink=me]').should('exist').click();
    cy.wait('@meIntercept');
    cy.intercept('DELETE', `/api/user/${jdoe.id}`, '{}').as('deleteIntercept');
    cy.get('button[data-testid=delete-button]').should('exist').click();
    cy.wait('@deleteIntercept');
    cy.get('.mat-simple-snackbar')
      .should('exist')
      .and('contain', 'Your account has been deleted !');

    cy.url().should('include', '/');
    cy.get('span[routerLink=me]').should('not.exist');
  });
  it('Should a user be able to delete himself show me informations when navigate on me', () => {});
});
