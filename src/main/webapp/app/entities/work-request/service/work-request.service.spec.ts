import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IWorkRequest } from '../work-request.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../work-request.test-samples';

import { WorkRequestService, RestWorkRequest } from './work-request.service';

const requireRestSample: RestWorkRequest = {
  ...sampleWithRequiredData,
  startDate: sampleWithRequiredData.startDate?.toJSON(),
  endDate: sampleWithRequiredData.endDate?.toJSON(),
};

describe('WorkRequest Service', () => {
  let service: WorkRequestService;
  let httpMock: HttpTestingController;
  let expectedResult: IWorkRequest | IWorkRequest[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(WorkRequestService);
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

    it('should create a WorkRequest', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const workRequest = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(workRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a WorkRequest', () => {
      const workRequest = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(workRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a WorkRequest', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of WorkRequest', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a WorkRequest', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addWorkRequestToCollectionIfMissing', () => {
      it('should add a WorkRequest to an empty array', () => {
        const workRequest: IWorkRequest = sampleWithRequiredData;
        expectedResult = service.addWorkRequestToCollectionIfMissing([], workRequest);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(workRequest);
      });

      it('should not add a WorkRequest to an array that contains it', () => {
        const workRequest: IWorkRequest = sampleWithRequiredData;
        const workRequestCollection: IWorkRequest[] = [
          {
            ...workRequest,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addWorkRequestToCollectionIfMissing(workRequestCollection, workRequest);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a WorkRequest to an array that doesn't contain it", () => {
        const workRequest: IWorkRequest = sampleWithRequiredData;
        const workRequestCollection: IWorkRequest[] = [sampleWithPartialData];
        expectedResult = service.addWorkRequestToCollectionIfMissing(workRequestCollection, workRequest);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(workRequest);
      });

      it('should add only unique WorkRequest to an array', () => {
        const workRequestArray: IWorkRequest[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const workRequestCollection: IWorkRequest[] = [sampleWithRequiredData];
        expectedResult = service.addWorkRequestToCollectionIfMissing(workRequestCollection, ...workRequestArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const workRequest: IWorkRequest = sampleWithRequiredData;
        const workRequest2: IWorkRequest = sampleWithPartialData;
        expectedResult = service.addWorkRequestToCollectionIfMissing([], workRequest, workRequest2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(workRequest);
        expect(expectedResult).toContain(workRequest2);
      });

      it('should accept null and undefined values', () => {
        const workRequest: IWorkRequest = sampleWithRequiredData;
        expectedResult = service.addWorkRequestToCollectionIfMissing([], null, workRequest, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(workRequest);
      });

      it('should return initial array if no WorkRequest is added', () => {
        const workRequestCollection: IWorkRequest[] = [sampleWithRequiredData];
        expectedResult = service.addWorkRequestToCollectionIfMissing(workRequestCollection, undefined, null);
        expect(expectedResult).toEqual(workRequestCollection);
      });
    });

    describe('compareWorkRequest', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareWorkRequest(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareWorkRequest(entity1, entity2);
        const compareResult2 = service.compareWorkRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareWorkRequest(entity1, entity2);
        const compareResult2 = service.compareWorkRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareWorkRequest(entity1, entity2);
        const compareResult2 = service.compareWorkRequest(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
