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

describe('CostingPercentages e2e test', () => {
  const costingPercentagesPageUrl = '/costing-percentages';
  const costingPercentagesPageUrlPattern = new RegExp('/costing-percentages(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const costingPercentagesSample = {
    costingSystem: 56443,
    costingQual: 25717,
    costingImp: 51505,
    costingPostImp: 66465,
    dateAdded: '2023-07-19T21:41:56.036Z',
  };

  let costingPercentages;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/costing-percentages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/costing-percentages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/costing-percentages/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (costingPercentages) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/costing-percentages/${costingPercentages.id}`,
      }).then(() => {
        costingPercentages = undefined;
      });
    }
  });

  it('CostingPercentages menu should load CostingPercentages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('costing-percentages');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CostingPercentages').should('exist');
    cy.url().should('match', costingPercentagesPageUrlPattern);
  });

  describe('CostingPercentages page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(costingPercentagesPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CostingPercentages page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/costing-percentages/new$'));
        cy.getEntityCreateUpdateHeading('CostingPercentages');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', costingPercentagesPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/costing-percentages',
          body: costingPercentagesSample,
        }).then(({ body }) => {
          costingPercentages = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/costing-percentages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [costingPercentages],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(costingPercentagesPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CostingPercentages page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('costingPercentages');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', costingPercentagesPageUrlPattern);
      });

      it('edit button click should load edit CostingPercentages page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CostingPercentages');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', costingPercentagesPageUrlPattern);
      });

      it('edit button click should load edit CostingPercentages page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CostingPercentages');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', costingPercentagesPageUrlPattern);
      });

      it('last delete button click should delete instance of CostingPercentages', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('costingPercentages').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', costingPercentagesPageUrlPattern);

        costingPercentages = undefined;
      });
    });
  });

  describe('new CostingPercentages page', () => {
    beforeEach(() => {
      cy.visit(`${costingPercentagesPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CostingPercentages');
    });

    it('should create an instance of CostingPercentages', () => {
      cy.get(`[data-cy="costingSystem"]`).type('4580').should('have.value', '4580');

      cy.get(`[data-cy="costingQual"]`).type('62788').should('have.value', '62788');

      cy.get(`[data-cy="costingImp"]`).type('75783').should('have.value', '75783');

      cy.get(`[data-cy="costingPostImp"]`).type('15554').should('have.value', '15554');

      cy.get(`[data-cy="active"]`).should('not.be.checked');
      cy.get(`[data-cy="active"]`).click().should('be.checked');

      cy.get(`[data-cy="dateAdded"]`).type('2023-07-20T07:00').blur().should('have.value', '2023-07-20T07:00');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        costingPercentages = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', costingPercentagesPageUrlPattern);
    });
  });
});
