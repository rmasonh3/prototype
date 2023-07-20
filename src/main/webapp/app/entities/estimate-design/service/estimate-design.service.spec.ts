import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEstimateDesign } from '../estimate-design.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../estimate-design.test-samples';

import { EstimateDesignService } from './estimate-design.service';

const requireRestSample: IEstimateDesign = {
  ...sampleWithRequiredData,
};

describe('EstimateDesign Service', () => {
  let service: EstimateDesignService;
  let httpMock: HttpTestingController;
  let expectedResult: IEstimateDesign | IEstimateDesign[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EstimateDesignService);
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

    it('should create a EstimateDesign', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const estimateDesign = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(estimateDesign).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a EstimateDesign', () => {
      const estimateDesign = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(estimateDesign).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a EstimateDesign', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of EstimateDesign', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a EstimateDesign', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addEstimateDesignToCollectionIfMissing', () => {
      it('should add a EstimateDesign to an empty array', () => {
        const estimateDesign: IEstimateDesign = sampleWithRequiredData;
        expectedResult = service.addEstimateDesignToCollectionIfMissing([], estimateDesign);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(estimateDesign);
      });

      it('should not add a EstimateDesign to an array that contains it', () => {
        const estimateDesign: IEstimateDesign = sampleWithRequiredData;
        const estimateDesignCollection: IEstimateDesign[] = [
          {
            ...estimateDesign,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEstimateDesignToCollectionIfMissing(estimateDesignCollection, estimateDesign);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a EstimateDesign to an array that doesn't contain it", () => {
        const estimateDesign: IEstimateDesign = sampleWithRequiredData;
        const estimateDesignCollection: IEstimateDesign[] = [sampleWithPartialData];
        expectedResult = service.addEstimateDesignToCollectionIfMissing(estimateDesignCollection, estimateDesign);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(estimateDesign);
      });

      it('should add only unique EstimateDesign to an array', () => {
        const estimateDesignArray: IEstimateDesign[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const estimateDesignCollection: IEstimateDesign[] = [sampleWithRequiredData];
        expectedResult = service.addEstimateDesignToCollectionIfMissing(estimateDesignCollection, ...estimateDesignArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const estimateDesign: IEstimateDesign = sampleWithRequiredData;
        const estimateDesign2: IEstimateDesign = sampleWithPartialData;
        expectedResult = service.addEstimateDesignToCollectionIfMissing([], estimateDesign, estimateDesign2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(estimateDesign);
        expect(expectedResult).toContain(estimateDesign2);
      });

      it('should accept null and undefined values', () => {
        const estimateDesign: IEstimateDesign = sampleWithRequiredData;
        expectedResult = service.addEstimateDesignToCollectionIfMissing([], null, estimateDesign, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(estimateDesign);
      });

      it('should return initial array if no EstimateDesign is added', () => {
        const estimateDesignCollection: IEstimateDesign[] = [sampleWithRequiredData];
        expectedResult = service.addEstimateDesignToCollectionIfMissing(estimateDesignCollection, undefined, null);
        expect(expectedResult).toEqual(estimateDesignCollection);
      });
    });

    describe('compareEstimateDesign', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEstimateDesign(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEstimateDesign(entity1, entity2);
        const compareResult2 = service.compareEstimateDesign(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEstimateDesign(entity1, entity2);
        const compareResult2 = service.compareEstimateDesign(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEstimateDesign(entity1, entity2);
        const compareResult2 = service.compareEstimateDesign(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
