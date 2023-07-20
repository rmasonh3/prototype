import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEstimateBasis } from '../estimate-basis.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../estimate-basis.test-samples';

import { EstimateBasisService, RestEstimateBasis } from './estimate-basis.service';

const requireRestSample: RestEstimateBasis = {
  ...sampleWithRequiredData,
  lastUpdate: sampleWithRequiredData.lastUpdate?.toJSON(),
};

describe('EstimateBasis Service', () => {
  let service: EstimateBasisService;
  let httpMock: HttpTestingController;
  let expectedResult: IEstimateBasis | IEstimateBasis[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EstimateBasisService);
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

    it('should create a EstimateBasis', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const estimateBasis = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(estimateBasis).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a EstimateBasis', () => {
      const estimateBasis = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(estimateBasis).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a EstimateBasis', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of EstimateBasis', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a EstimateBasis', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addEstimateBasisToCollectionIfMissing', () => {
      it('should add a EstimateBasis to an empty array', () => {
        const estimateBasis: IEstimateBasis = sampleWithRequiredData;
        expectedResult = service.addEstimateBasisToCollectionIfMissing([], estimateBasis);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(estimateBasis);
      });

      it('should not add a EstimateBasis to an array that contains it', () => {
        const estimateBasis: IEstimateBasis = sampleWithRequiredData;
        const estimateBasisCollection: IEstimateBasis[] = [
          {
            ...estimateBasis,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEstimateBasisToCollectionIfMissing(estimateBasisCollection, estimateBasis);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a EstimateBasis to an array that doesn't contain it", () => {
        const estimateBasis: IEstimateBasis = sampleWithRequiredData;
        const estimateBasisCollection: IEstimateBasis[] = [sampleWithPartialData];
        expectedResult = service.addEstimateBasisToCollectionIfMissing(estimateBasisCollection, estimateBasis);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(estimateBasis);
      });

      it('should add only unique EstimateBasis to an array', () => {
        const estimateBasisArray: IEstimateBasis[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const estimateBasisCollection: IEstimateBasis[] = [sampleWithRequiredData];
        expectedResult = service.addEstimateBasisToCollectionIfMissing(estimateBasisCollection, ...estimateBasisArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const estimateBasis: IEstimateBasis = sampleWithRequiredData;
        const estimateBasis2: IEstimateBasis = sampleWithPartialData;
        expectedResult = service.addEstimateBasisToCollectionIfMissing([], estimateBasis, estimateBasis2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(estimateBasis);
        expect(expectedResult).toContain(estimateBasis2);
      });

      it('should accept null and undefined values', () => {
        const estimateBasis: IEstimateBasis = sampleWithRequiredData;
        expectedResult = service.addEstimateBasisToCollectionIfMissing([], null, estimateBasis, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(estimateBasis);
      });

      it('should return initial array if no EstimateBasis is added', () => {
        const estimateBasisCollection: IEstimateBasis[] = [sampleWithRequiredData];
        expectedResult = service.addEstimateBasisToCollectionIfMissing(estimateBasisCollection, undefined, null);
        expect(expectedResult).toEqual(estimateBasisCollection);
      });
    });

    describe('compareEstimateBasis', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEstimateBasis(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEstimateBasis(entity1, entity2);
        const compareResult2 = service.compareEstimateBasis(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEstimateBasis(entity1, entity2);
        const compareResult2 = service.compareEstimateBasis(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEstimateBasis(entity1, entity2);
        const compareResult2 = service.compareEstimateBasis(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
