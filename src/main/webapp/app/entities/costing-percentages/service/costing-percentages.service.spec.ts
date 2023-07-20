import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICostingPercentages } from '../costing-percentages.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../costing-percentages.test-samples';

import { CostingPercentagesService, RestCostingPercentages } from './costing-percentages.service';

const requireRestSample: RestCostingPercentages = {
  ...sampleWithRequiredData,
  dateAdded: sampleWithRequiredData.dateAdded?.toJSON(),
};

describe('CostingPercentages Service', () => {
  let service: CostingPercentagesService;
  let httpMock: HttpTestingController;
  let expectedResult: ICostingPercentages | ICostingPercentages[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CostingPercentagesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a CostingPercentages', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const costingPercentages = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(costingPercentages).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CostingPercentages', () => {
      const costingPercentages = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(costingPercentages).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CostingPercentages', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CostingPercentages', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CostingPercentages', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCostingPercentagesToCollectionIfMissing', () => {
      it('should add a CostingPercentages to an empty array', () => {
        const costingPercentages: ICostingPercentages = sampleWithRequiredData;
        expectedResult = service.addCostingPercentagesToCollectionIfMissing([], costingPercentages);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(costingPercentages);
      });

      it('should not add a CostingPercentages to an array that contains it', () => {
        const costingPercentages: ICostingPercentages = sampleWithRequiredData;
        const costingPercentagesCollection: ICostingPercentages[] = [
          {
            ...costingPercentages,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCostingPercentagesToCollectionIfMissing(costingPercentagesCollection, costingPercentages);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CostingPercentages to an array that doesn't contain it", () => {
        const costingPercentages: ICostingPercentages = sampleWithRequiredData;
        const costingPercentagesCollection: ICostingPercentages[] = [sampleWithPartialData];
        expectedResult = service.addCostingPercentagesToCollectionIfMissing(costingPercentagesCollection, costingPercentages);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(costingPercentages);
      });

      it('should add only unique CostingPercentages to an array', () => {
        const costingPercentagesArray: ICostingPercentages[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const costingPercentagesCollection: ICostingPercentages[] = [sampleWithRequiredData];
        expectedResult = service.addCostingPercentagesToCollectionIfMissing(costingPercentagesCollection, ...costingPercentagesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const costingPercentages: ICostingPercentages = sampleWithRequiredData;
        const costingPercentages2: ICostingPercentages = sampleWithPartialData;
        expectedResult = service.addCostingPercentagesToCollectionIfMissing([], costingPercentages, costingPercentages2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(costingPercentages);
        expect(expectedResult).toContain(costingPercentages2);
      });

      it('should accept null and undefined values', () => {
        const costingPercentages: ICostingPercentages = sampleWithRequiredData;
        expectedResult = service.addCostingPercentagesToCollectionIfMissing([], null, costingPercentages, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(costingPercentages);
      });

      it('should return initial array if no CostingPercentages is added', () => {
        const costingPercentagesCollection: ICostingPercentages[] = [sampleWithRequiredData];
        expectedResult = service.addCostingPercentagesToCollectionIfMissing(costingPercentagesCollection, undefined, null);
        expect(expectedResult).toEqual(costingPercentagesCollection);
      });
    });

    describe('compareCostingPercentages', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCostingPercentages(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCostingPercentages(entity1, entity2);
        const compareResult2 = service.compareCostingPercentages(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCostingPercentages(entity1, entity2);
        const compareResult2 = service.compareCostingPercentages(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCostingPercentages(entity1, entity2);
        const compareResult2 = service.compareCostingPercentages(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
