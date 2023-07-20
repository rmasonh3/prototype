import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('ElementTypes e2e test', () => {
  const elementTypesPageUrl = '/element-types';
  const elementTypesPageUrlPattern = new RegExp('/element-types(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const elementTypesSample = { element: 'a8ce6375-cb05-4204-8d77-db2ebb840e6f' };

  let elementTypes;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/element-types+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/element-types').as('postEntityRequest');
    cy.intercept('DELETE', '/api/element-types/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (elementTypes) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/element-types/${elementTypes.id}`,
      }).then(() => {
        elementTypes = undefined;
      });
    }
  });

  it('ElementTypes menu should load ElementTypes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('element-types');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ElementTypes').should('exist');
    cy.url().should('match', elementTypesPageUrlPattern);
  });

  describe('ElementTypes page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(elementTypesPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ElementTypes page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/element-types/new$'));
        cy.getEntityCreateUpdateHeading('ElementTypes');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', elementTypesPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/element-types',
          body: elementTypesSample,
        }).then(({ body }) => {
          elementTypes = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/element-types+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [elementTypes],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(elementTypesPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ElementTypes page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('elementTypes');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', elementTypesPageUrlPattern);
      });

      it('edit button click should load edit ElementTypes page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ElementTypes');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', elementTypesPageUrlPattern);
      });

      it('edit button click should load edit ElementTypes page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ElementTypes');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', elementTypesPageUrlPattern);
      });

      it('last delete button click should delete instance of ElementTypes', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('elementTypes').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', elementTypesPageUrlPattern);

        elementTypes = undefined;
      });
    });
  });

  describe('new ElementTypes page', () => {
    beforeEach(() => {
      cy.visit(`${elementTypesPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ElementTypes');
    });

    it('should create an instance of ElementTypes', () => {
      cy.get(`[data-cy="element"]`)
        .type('17b5eb2d-2a17-4177-ba7b-aba56cef92b8')
        .invoke('val')
        .should('match', new RegExp('17b5eb2d-2a17-4177-ba7b-aba56cef92b8'));

      cy.get(`[data-cy="type"]`).select('Regen');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        elementTypes = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', elementTypesPageUrlPattern);
    });
  });
});
