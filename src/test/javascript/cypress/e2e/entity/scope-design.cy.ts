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

describe('ScopeDesign e2e test', () => {
  const scopeDesignPageUrl = '/scope-design';
  const scopeDesignPageUrlPattern = new RegExp('/scope-design(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const scopeDesignSample = {};

  let scopeDesign;
  // let workRequest;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/work-requests',
      body: {"projectId":"Washington Ethiopian Virtual","workRequest":"demand-driven Maine","workRequestDescription":"Garden contextually-based back-end","workRwquestPhase":"Sleek","startDate":"2023-07-20T04:55:05.151Z","endDate":"2023-07-19T22:31:12.958Z","status":"Pending","design":"Pending"},
    }).then(({ body }) => {
      workRequest = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/scope-designs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/scope-designs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/scope-designs/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/work-requests', {
      statusCode: 200,
      body: [workRequest],
    });

  });
   */

  afterEach(() => {
    if (scopeDesign) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/scope-designs/${scopeDesign.id}`,
      }).then(() => {
        scopeDesign = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (workRequest) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/work-requests/${workRequest.id}`,
      }).then(() => {
        workRequest = undefined;
      });
    }
  });
   */

  it('ScopeDesigns menu should load ScopeDesigns page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('scope-design');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ScopeDesign').should('exist');
    cy.url().should('match', scopeDesignPageUrlPattern);
  });

  describe('ScopeDesign page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(scopeDesignPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ScopeDesign page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/scope-design/new$'));
        cy.getEntityCreateUpdateHeading('ScopeDesign');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', scopeDesignPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/scope-designs',
          body: {
            ...scopeDesignSample,
            workrequest: workRequest,
          },
        }).then(({ body }) => {
          scopeDesign = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/scope-designs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/scope-designs?page=0&size=20>; rel="last",<http://localhost/api/scope-designs?page=0&size=20>; rel="first"',
              },
              body: [scopeDesign],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(scopeDesignPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(scopeDesignPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ScopeDesign page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('scopeDesign');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', scopeDesignPageUrlPattern);
      });

      it('edit button click should load edit ScopeDesign page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ScopeDesign');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', scopeDesignPageUrlPattern);
      });

      it('edit button click should load edit ScopeDesign page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ScopeDesign');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', scopeDesignPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ScopeDesign', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('scopeDesign').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', scopeDesignPageUrlPattern);

        scopeDesign = undefined;
      });
    });
  });

  describe('new ScopeDesign page', () => {
    beforeEach(() => {
      cy.visit(`${scopeDesignPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ScopeDesign');
    });

    it.skip('should create an instance of ScopeDesign', () => {
      cy.get(`[data-cy="designEstimate"]`).type('60536').should('have.value', '60536');

      cy.get(`[data-cy="codeEstimate"]`).type('75025').should('have.value', '75025');

      cy.get(`[data-cy="syst1Estimate"]`).type('24658').should('have.value', '24658');

      cy.get(`[data-cy="syst2Estimate"]`).type('12129').should('have.value', '12129');

      cy.get(`[data-cy="qualEstimate"]`).type('56032').should('have.value', '56032');

      cy.get(`[data-cy="impEstimate"]`).type('57167').should('have.value', '57167');

      cy.get(`[data-cy="postImpEstimate"]`).type('16388').should('have.value', '16388');

      cy.get(`[data-cy="totalHours"]`).type('59159').should('have.value', '59159');

      cy.get(`[data-cy="workrequest"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        scopeDesign = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', scopeDesignPageUrlPattern);
    });
  });
});
