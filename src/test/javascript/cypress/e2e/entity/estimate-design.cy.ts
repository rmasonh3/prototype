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

describe('EstimateDesign e2e test', () => {
  const estimateDesignPageUrl = '/estimate-design';
  const estimateDesignPageUrlPattern = new RegExp('/estimate-design(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const estimateDesignSample = {"qpproachNumber":79954};

  let estimateDesign;
  // let workRequest;
  // let elementTypes;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/work-requests',
      body: {"projectId":"COM Wooden Brazil","workRequest":"Bedfordshire","workRequestDescription":"Bhutan Practical","workRwquestPhase":"Eritrea interface","startDate":"2023-07-20T14:34:10.290Z","endDate":"2023-07-20T15:06:11.013Z","status":"InProgress","design":"Completed"},
    }).then(({ body }) => {
      workRequest = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/element-types',
      body: {"element":"dcda7d29-ec60-46ec-aff1-e2dd7e175ddb","type":"Modify"},
    }).then(({ body }) => {
      elementTypes = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/estimate-designs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/estimate-designs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/estimate-designs/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/work-requests', {
      statusCode: 200,
      body: [workRequest],
    });

    cy.intercept('GET', '/api/element-types', {
      statusCode: 200,
      body: [elementTypes],
    });

  });
   */

  afterEach(() => {
    if (estimateDesign) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/estimate-designs/${estimateDesign.id}`,
      }).then(() => {
        estimateDesign = undefined;
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
    if (elementTypes) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/element-types/${elementTypes.id}`,
      }).then(() => {
        elementTypes = undefined;
      });
    }
  });
   */

  it('EstimateDesigns menu should load EstimateDesigns page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('estimate-design');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('EstimateDesign').should('exist');
    cy.url().should('match', estimateDesignPageUrlPattern);
  });

  describe('EstimateDesign page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(estimateDesignPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create EstimateDesign page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/estimate-design/new$'));
        cy.getEntityCreateUpdateHeading('EstimateDesign');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', estimateDesignPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/estimate-designs',
          body: {
            ...estimateDesignSample,
            workrequest: workRequest,
            elementtypes: elementTypes,
          },
        }).then(({ body }) => {
          estimateDesign = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/estimate-designs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/estimate-designs?page=0&size=20>; rel="last",<http://localhost/api/estimate-designs?page=0&size=20>; rel="first"',
              },
              body: [estimateDesign],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(estimateDesignPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(estimateDesignPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details EstimateDesign page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('estimateDesign');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', estimateDesignPageUrlPattern);
      });

      it('edit button click should load edit EstimateDesign page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EstimateDesign');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', estimateDesignPageUrlPattern);
      });

      it('edit button click should load edit EstimateDesign page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('EstimateDesign');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', estimateDesignPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of EstimateDesign', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('estimateDesign').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', estimateDesignPageUrlPattern);

        estimateDesign = undefined;
      });
    });
  });

  describe('new EstimateDesign page', () => {
    beforeEach(() => {
      cy.visit(`${estimateDesignPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('EstimateDesign');
    });

    it.skip('should create an instance of EstimateDesign', () => {
      cy.get(`[data-cy="qpproachNumber"]`).type('55546').should('have.value', '55546');

      cy.get(`[data-cy="complexity"]`).select('Average');

      cy.get(`[data-cy="workrequest"]`).select(1);
      cy.get(`[data-cy="elementtypes"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        estimateDesign = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', estimateDesignPageUrlPattern);
    });
  });
});
