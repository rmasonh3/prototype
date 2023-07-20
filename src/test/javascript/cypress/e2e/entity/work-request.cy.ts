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

describe('WorkRequest e2e test', () => {
  const workRequestPageUrl = '/work-request';
  const workRequestPageUrlPattern = new RegExp('/work-request(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const workRequestSample = {"projectId":"Reverse-engineered","workRequest":"initiatives","workRequestDescription":"program Fish","workRwquestPhase":"Frozen","startDate":"2023-07-20T15:53:31.159Z","endDate":"2023-07-20T11:03:15.612Z"};

  let workRequest;
  // let employee;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/employees',
      body: {"employeeId":"ivory Mill","firstName":"Mike","lastName":"Reynolds","email":"f1g12@^qyJ*.G3","phone":"373.970.5231 x156","department":"PGBA_PMO","role":"Software_Developer"},
    }).then(({ body }) => {
      employee = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/work-requests+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/work-requests').as('postEntityRequest');
    cy.intercept('DELETE', '/api/work-requests/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/employees', {
      statusCode: 200,
      body: [employee],
    });

  });
   */

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

  /* Disabled due to incompatibility
  afterEach(() => {
    if (employee) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/employees/${employee.id}`,
      }).then(() => {
        employee = undefined;
      });
    }
  });
   */

  it('WorkRequests menu should load WorkRequests page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('work-request');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('WorkRequest').should('exist');
    cy.url().should('match', workRequestPageUrlPattern);
  });

  describe('WorkRequest page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(workRequestPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create WorkRequest page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/work-request/new$'));
        cy.getEntityCreateUpdateHeading('WorkRequest');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', workRequestPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/work-requests',
          body: {
            ...workRequestSample,
            employee: employee,
          },
        }).then(({ body }) => {
          workRequest = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/work-requests+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/work-requests?page=0&size=20>; rel="last",<http://localhost/api/work-requests?page=0&size=20>; rel="first"',
              },
              body: [workRequest],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(workRequestPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(workRequestPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details WorkRequest page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('workRequest');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', workRequestPageUrlPattern);
      });

      it('edit button click should load edit WorkRequest page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WorkRequest');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', workRequestPageUrlPattern);
      });

      it('edit button click should load edit WorkRequest page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WorkRequest');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', workRequestPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of WorkRequest', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('workRequest').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', workRequestPageUrlPattern);

        workRequest = undefined;
      });
    });
  });

  describe('new WorkRequest page', () => {
    beforeEach(() => {
      cy.visit(`${workRequestPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('WorkRequest');
    });

    it.skip('should create an instance of WorkRequest', () => {
      cy.get(`[data-cy="projectId"]`).type('innovative').should('have.value', 'innovative');

      cy.get(`[data-cy="workRequest"]`).type('optical').should('have.value', 'optical');

      cy.get(`[data-cy="workRequestDescription"]`).type('Toys GB haptic').should('have.value', 'Toys GB haptic');

      cy.get(`[data-cy="workRwquestPhase"]`).type('invoice capacity mindshare').should('have.value', 'invoice capacity mindshare');

      cy.get(`[data-cy="startDate"]`).type('2023-07-20T16:43').blur().should('have.value', '2023-07-20T16:43');

      cy.get(`[data-cy="endDate"]`).type('2023-07-20T02:44').blur().should('have.value', '2023-07-20T02:44');

      cy.get(`[data-cy="status"]`).select('Canceled');

      cy.get(`[data-cy="design"]`).select('InProgress');

      cy.get(`[data-cy="employee"]`).select([0]);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        workRequest = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', workRequestPageUrlPattern);
    });
  });
});
