import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IWorkInfo } from '../work-info.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../work-info.test-samples';

import { WorkInfoService } from './work-info.service';

const requireRestSample: IWorkInfo = {
  ...sampleWithRequiredData,
};

describe('WorkInfo Service', () => {
  let service: WorkInfoService;
  let httpMock: HttpTestingController;
  let expectedResult: IWorkInfo | IWorkInfo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(WorkInfoService);
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

    it('should create a WorkInfo', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const workInfo = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(workInfo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a WorkInfo', () => {
      const workInfo = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(workInfo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a WorkInfo', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of WorkInfo', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a WorkInfo', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addWorkInfoToCollectionIfMissing', () => {
      it('should add a WorkInfo to an empty array', () => {
        const workInfo: IWorkInfo = sampleWithRequiredData;
        expectedResult = service.addWorkInfoToCollectionIfMissing([], workInfo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(workInfo);
      });

      it('should not add a WorkInfo to an array that contains it', () => {
        const workInfo: IWorkInfo = sampleWithRequiredData;
        const workInfoCollection: IWorkInfo[] = [
          {
            ...workInfo,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addWorkInfoToCollectionIfMissing(workInfoCollection, workInfo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a WorkInfo to an array that doesn't contain it", () => {
        const workInfo: IWorkInfo = sampleWithRequiredData;
        const workInfoCollection: IWorkInfo[] = [sampleWithPartialData];
        expectedResult = service.addWorkInfoToCollectionIfMissing(workInfoCollection, workInfo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(workInfo);
      });

      it('should add only unique WorkInfo to an array', () => {
        const workInfoArray: IWorkInfo[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const workInfoCollection: IWorkInfo[] = [sampleWithRequiredData];
        expectedResult = service.addWorkInfoToCollectionIfMissing(workInfoCollection, ...workInfoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const workInfo: IWorkInfo = sampleWithRequiredData;
        const workInfo2: IWorkInfo = sampleWithPartialData;
        expectedResult = service.addWorkInfoToCollectionIfMissing([], workInfo, workInfo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(workInfo);
        expect(expectedResult).toContain(workInfo2);
      });

      it('should accept null and undefined values', () => {
        const workInfo: IWorkInfo = sampleWithRequiredData;
        expectedResult = service.addWorkInfoToCollectionIfMissing([], null, workInfo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(workInfo);
      });

      it('should return initial array if no WorkInfo is added', () => {
        const workInfoCollection: IWorkInfo[] = [sampleWithRequiredData];
        expectedResult = service.addWorkInfoToCollectionIfMissing(workInfoCollection, undefined, null);
        expect(expectedResult).toEqual(workInfoCollection);
      });
    });

    describe('compareWorkInfo', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareWorkInfo(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareWorkInfo(entity1, entity2);
        const compareResult2 = service.compareWorkInfo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareWorkInfo(entity1, entity2);
        const compareResult2 = service.compareWorkInfo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareWorkInfo(entity1, entity2);
        const compareResult2 = service.compareWorkInfo(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
