declare namespace Cypress {
  interface Chainable {
    login(user): Chainable<void>;
    logout(): Chainable<void>;
  }
}
