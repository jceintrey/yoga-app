// ***********************************************
// This example namespace declaration will help
// with Intellisense and code completion in your
// IDE or Text Editor.
// ***********************************************
// declare namespace Cypress {
//   interface Chainable<Subject = any> {
//     customCommand(param: any): typeof customCommand;
//   }
// }
//
// function customCommand(param: any): void {
//   console.warn(param);
// }
//
// NOTE: You can use it like so:
// Cypress.Commands.add('customCommand', customCommand);
//
// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

import { admin, sessions, teachers } from '../e2e/mockdata';

Cypress.Commands.add('logout', () => {
  cy.clearCookies();
  cy.clearLocalStorage();
});

Cypress.Commands.add('fillSessionForm', (session) => {
  cy.get('input[formControlName=name]').type(session.name);

  cy.get('input[formControlName=date]').type(session.date);
  cy.get('mat-select[formControlName=teacher_id]').click();

  cy.get('mat-option')
    .contains(
      `${teachers[session.teacher_id].firstName} ${
        teachers[session.teacher_id].lastName
      }`
    )
    .click();
  cy.get('textarea[formControlName=description]').type(session.description);
});

Cypress.Commands.add('login', (user) => {
  cy.visit('/login');

  cy.intercept('POST', '/api/auth/login', {
    body: {
      id: user['id'],
      email: user['email'],
      lastName: user['lastName'],
      firstName: user['firstName'],
      admin: user['admin'],
      createdAt: user['createdAt'],
      updatedAt: user['updatedAt'],
      password: user['password'],
    },
  });

  cy.intercept(
    {
      method: 'GET',
      url: '/api/session',
    },
    sessions
  ).as('session');

  cy.get('input[formControlName=email]').type(user['email']);
  cy.get('input[formControlName=password]').type(user['password']);
  cy.get('button[type=submit]').click();
});
