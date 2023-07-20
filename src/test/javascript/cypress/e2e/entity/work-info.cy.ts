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

describe('WorkInfo e2e test', () => {
  const workInfoPageUrl = '/work-info';
  const workInfoPageUrlPattern = new RegExp('/work-info(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const workInfoSample = {"scopeAct":12619,"designAct":16888,"codeAct":81036,"syst1Act":674,"syst2Act":15851,"qualAct":13848,"impAct":49448,"postImpAct":12115,"totalAct":30185};

  let workInfo;
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
      body: {"projectId":"Buckinghamshire","workRequest":"Research context-sensitive","workRequestDescription":"Loan green Interactions","workRwquestPhase":"Executive copying","startDate":"2023-07-19T21:50:37.257Z","endDate":"2023-07-20T15:03:03.878Z","status":"OnHold","design":"Pending"},
    }).then(({ body }) => {
      workRequest = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/work-infos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/work-infos').as('postEntityRequest');
    cy.intercept('DELETE', '/api/work-infos/*').as('deleteEntityRequest');
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
    if (workInfo) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/work-infos/${workInfo.id}`,
      }).then(() => {
        workInfo = undefined;
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

  it('WorkInfos menu should load WorkInfos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('work-info');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('WorkInfo').should('exist');
    cy.url().should('match', workInfoPageUrlPattern);
  });

  describe('WorkInfo page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(workInfoPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create WorkInfo page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/work-info/new$'));
        cy.getEntityCreateUpdateHeading('WorkInfo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', workInfoPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/work-infos',
          body: {
            ...workInfoSample,
            workrequest: workRequest,
          },
        }).then(({ body }) => {
          workInfo = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/work-infos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/work-infos?page=0&size=20>; rel="last",<http://localhost/api/work-infos?page=0&size=20>; rel="first"',
              },
              body: [workInfo],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(workInfoPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(workInfoPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details WorkInfo page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('workInfo');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', workInfoPageUrlPattern);
      });

      it('edit button click should load edit WorkInfo page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WorkInfo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', workInfoPageUrlPattern);
      });

      it('edit button click should load edit WorkInfo page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WorkInfo');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', workInfoPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of WorkInfo', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('workInfo').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', workInfoPageUrlPattern);

        workInfo = undefined;
      });
    });
  });

  describe('new WorkInfo page', () => {
    beforeEach(() => {
      cy.visit(`${workInfoPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('WorkInfo');
    });

    it.skip('should create an instance of WorkInfo', () => {
      cy.get(`[data-cy="scopeAct"]`).type('81540').should('have.value', '81540');

      cy.get(`[data-cy="designAct"]`).type('95289').should('have.value', '95289');

      cy.get(`[data-cy="codeAct"]`).type('54341').should('have.value', '54341');

      cy.get(`[data-cy="syst1Act"]`).type('48281').should('have.value', '48281');

      cy.get(`[data-cy="syst2Act"]`).type('97791').should('have.value', '97791');

      cy.get(`[data-cy="qualAct"]`).type('47569').should('have.value', '47569');

      cy.get(`[data-cy="impAct"]`).type('18994').should('have.value', '18994');

      cy.get(`[data-cy="postImpAct"]`).type('16658').should('have.value', '16658');

      cy.get(`[data-cy="totalAct"]`).type('68502').should('have.value', '68502');

      cy.get(`[data-cy="workrequest"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        workInfo = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', workInfoPageUrlPattern);
    });
  });
});
