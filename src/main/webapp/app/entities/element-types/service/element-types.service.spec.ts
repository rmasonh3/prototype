import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IElementTypes } from '../element-types.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../element-types.test-samples';

import { ElementTypesService } from './element-types.service';

const requireRestSample: IElementTypes = {
  ...sampleWithRequiredData,
};

describe('ElementTypes Service', () => {
  let service: ElementTypesService;
  let httpMock: HttpTestingController;
  let expectedResult: IElementTypes | IElementTypes[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ElementTypesService);
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

    it('should create a ElementTypes', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const elementTypes = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(elementTypes).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ElementTypes', () => {
      const elementTypes = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(elementTypes).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ElementTypes', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ElementTypes', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ElementTypes', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addElementTypesToCollectionIfMissing', () => {
      it('should add a ElementTypes to an empty array', () => {
        const elementTypes: IElementTypes = sampleWithRequiredData;
        expectedResult = service.addElementTypesToCollectionIfMissing([], elementTypes);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(elementTypes);
      });

      it('should not add a ElementTypes to an array that contains it', () => {
        const elementTypes: IElementTypes = sampleWithRequiredData;
        const elementTypesCollection: IElementTypes[] = [
          {
            ...elementTypes,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addElementTypesToCollectionIfMissing(elementTypesCollection, elementTypes);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ElementTypes to an array that doesn't contain it", () => {
        const elementTypes: IElementTypes = sampleWithRequiredData;
        const elementTypesCollection: IElementTypes[] = [sampleWithPartialData];
        expectedResult = service.addElementTypesToCollectionIfMissing(elementTypesCollection, elementTypes);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(elementTypes);
      });

      it('should add only unique ElementTypes to an array', () => {
        const elementTypesArray: IElementTypes[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const elementTypesCollection: IElementTypes[] = [sampleWithRequiredData];
        expectedResult = service.addElementTypesToCollectionIfMissing(elementTypesCollection, ...elementTypesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const elementTypes: IElementTypes = sampleWithRequiredData;
        const elementTypes2: IElementTypes = sampleWithPartialData;
        expectedResult = service.addElementTypesToCollectionIfMissing([], elementTypes, elementTypes2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(elementTypes);
        expect(expectedResult).toContain(elementTypes2);
      });

      it('should accept null and undefined values', () => {
        const elementTypes: IElementTypes = sampleWithRequiredData;
        expectedResult = service.addElementTypesToCollectionIfMissing([], null, elementTypes, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(elementTypes);
      });

      it('should return initial array if no ElementTypes is added', () => {
        const elementTypesCollection: IElementTypes[] = [sampleWithRequiredData];
        expectedResult = service.addElementTypesToCollectionIfMissing(elementTypesCollection, undefined, null);
        expect(expectedResult).toEqual(elementTypesCollection);
      });
    });

    describe('compareElementTypes', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareElementTypes(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareElementTypes(entity1, entity2);
        const compareResult2 = service.compareElementTypes(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareElementTypes(entity1, entity2);
        const compareResult2 = service.compareElementTypes(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareElementTypes(entity1, entity2);
        const compareResult2 = service.compareElementTypes(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
