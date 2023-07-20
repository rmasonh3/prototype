import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IScopeDesign } from '../scope-design.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../scope-design.test-samples';

import { ScopeDesignService } from './scope-design.service';

const requireRestSample: IScopeDesign = {
  ...sampleWithRequiredData,
};

describe('ScopeDesign Service', () => {
  let service: ScopeDesignService;
  let httpMock: HttpTestingController;
  let expectedResult: IScopeDesign | IScopeDesign[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ScopeDesignService);
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

    it('should create a ScopeDesign', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const scopeDesign = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(scopeDesign).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ScopeDesign', () => {
      const scopeDesign = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(scopeDesign).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ScopeDesign', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ScopeDesign', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ScopeDesign', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addScopeDesignToCollectionIfMissing', () => {
      it('should add a ScopeDesign to an empty array', () => {
        const scopeDesign: IScopeDesign = sampleWithRequiredData;
        expectedResult = service.addScopeDesignToCollectionIfMissing([], scopeDesign);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scopeDesign);
      });

      it('should not add a ScopeDesign to an array that contains it', () => {
        const scopeDesign: IScopeDesign = sampleWithRequiredData;
        const scopeDesignCollection: IScopeDesign[] = [
          {
            ...scopeDesign,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addScopeDesignToCollectionIfMissing(scopeDesignCollection, scopeDesign);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ScopeDesign to an array that doesn't contain it", () => {
        const scopeDesign: IScopeDesign = sampleWithRequiredData;
        const scopeDesignCollection: IScopeDesign[] = [sampleWithPartialData];
        expectedResult = service.addScopeDesignToCollectionIfMissing(scopeDesignCollection, scopeDesign);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scopeDesign);
      });

      it('should add only unique ScopeDesign to an array', () => {
        const scopeDesignArray: IScopeDesign[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const scopeDesignCollection: IScopeDesign[] = [sampleWithRequiredData];
        expectedResult = service.addScopeDesignToCollectionIfMissing(scopeDesignCollection, ...scopeDesignArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const scopeDesign: IScopeDesign = sampleWithRequiredData;
        const scopeDesign2: IScopeDesign = sampleWithPartialData;
        expectedResult = service.addScopeDesignToCollectionIfMissing([], scopeDesign, scopeDesign2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scopeDesign);
        expect(expectedResult).toContain(scopeDesign2);
      });

      it('should accept null and undefined values', () => {
        const scopeDesign: IScopeDesign = sampleWithRequiredData;
        expectedResult = service.addScopeDesignToCollectionIfMissing([], null, scopeDesign, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scopeDesign);
      });

      it('should return initial array if no ScopeDesign is added', () => {
        const scopeDesignCollection: IScopeDesign[] = [sampleWithRequiredData];
        expectedResult = service.addScopeDesignToCollectionIfMissing(scopeDesignCollection, undefined, null);
        expect(expectedResult).toEqual(scopeDesignCollection);
      });
    });

    describe('compareScopeDesign', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareScopeDesign(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareScopeDesign(entity1, entity2);
        const compareResult2 = service.compareScopeDesign(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareScopeDesign(entity1, entity2);
        const compareResult2 = service.compareScopeDesign(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareScopeDesign(entity1, entity2);
        const compareResult2 = service.compareScopeDesign(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
