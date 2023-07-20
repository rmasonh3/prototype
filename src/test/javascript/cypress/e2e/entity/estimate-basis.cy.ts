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

describe('EstimateBasis e2e test', () => {
  const estimateBasisPageUrl = '/estimate-basis';
  const estimateBasisPageUrlPattern = new RegExp('/estimate-basis(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const estimateBasisSample = {"subsystemId":86940,"basisOfEstimate":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","assumptions":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","lastUpdate":"2023-07-20T02:20:18.952Z"};

  let estimateBasis;
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
      body: {"projectId":"virtual Spring Gorgeous","workRequest":"Overpass Accounts Technician","workRequestDescription":"viral repurpose Orchestrator","workRwquestPhase":"Quality digital National","startDate":"2023-07-20T11:47:23.530Z","endDate":"2023-07-20T03:56:09.941Z","status":"Canceled","design":"Completed"},
    }).then(({ body }) => {
      workRequest = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/estimate-bases+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/estimate-bases').as('postEntityRequest');
    cy.intercept('DELETE', '/api/estimate-bases/*').as('deleteEntityRequest');
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
    if (estimateBasis) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/estimate-bases/${estimateBasis.id}`,
      }).then(() => {
        estimateBasis = undefined;
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

  it('EstimateBases menu should load EstimateBases page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('estimate-basis');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('EstimateBasis').should('exist');
    cy.url().should('match', estimateBasisPageUrlPattern);
  });

  describe('EstimateBasis page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(estimateBasisPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create EstimateBasis page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/estimate-basis/new$'));
        cy.getEntityCreateUpdateHeading('EstimateBasis');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', estimateBasisPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/estimate-bases',
          body: {
            ...estimateBasisSample,
            workrequest: workRequest,
          },
        }).then(({ body }) => {
          estimateBasis = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/estimate-bases+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/estimate-bases?page=0&size=20>; rel="last",<http://localhost/api/estimate-bases?page=0&size=20>; rel="first"',
              },
              body: [estimateBasis],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(estimateBasisPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(estimateBasisPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details EstimateBasis page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('estimateBasis');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', estimateBasisPageUrlPattern);
      });

      it('edit button click should load edit EstimateBasis page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EstimateBasis');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', estimateBasisPageUrlPattern);
      });

      it('edit button click should load edit EstimateBasis page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EstimateBasis');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', estimateBasisPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of EstimateBasis', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('estimateBasis').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', estimateBasisPageUrlPattern);

        estimateBasis = undefined;
      });
    });
  });

  describe('new EstimateBasis page', () => {
    beforeEach(() => {
      cy.visit(`${estimateBasisPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('EstimateBasis');
    });

    it.skip('should create an instance of EstimateBasis', () => {
      cy.get(`[data-cy="subsystemId"]`).type('17796').should('have.value', '17796');

      cy.get(`[data-cy="basisOfEstimate"]`)
        .type('../fake-data/blob/hipster.txt')
        .invoke('val')
        .should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="assumptions"]`)
        .type('../fake-data/blob/hipster.txt')
        .invoke('val')
        .should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="lastUpdate"]`).type('2023-07-20T06:14').blur().should('have.value', '2023-07-20T06:14');

      cy.get(`[data-cy="workrequest"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        estimateBasis = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', estimateBasisPageUrlPattern);
    });
  });
});
