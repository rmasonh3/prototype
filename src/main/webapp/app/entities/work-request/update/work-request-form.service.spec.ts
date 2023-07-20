import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../work-request.test-samples';

import { WorkRequestFormService } from './work-request-form.service';

describe('WorkRequest Form Service', () => {
  let service: WorkRequestFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WorkRequestFormService);
  });

  describe('Service methods', () => {
    describe('createWorkRequestFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createWorkRequestFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            projectId: expect.any(Object),
            workRequest: expect.any(Object),
            workRequestDescription: expect.any(Object),
            workRwquestPhase: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            status: expect.any(Object),
            design: expect.any(Object),
          })
        );
      });

      it('passing IWorkRequest should create a new form with FormGroup', () => {
        const formGroup = service.createWorkRequestFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            projectId: expect.any(Object),
            workRequest: expect.any(Object),
            workRequestDescription: expect.any(Object),
            workRwquestPhase: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            status: expect.any(Object),
            design: expect.any(Object),
          })
        );
      });
    });

    describe('getWorkRequest', () => {
      it('should return NewWorkRequest for default WorkRequest initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createWorkRequestFormGroup(sampleWithNewData);

        const workRequest = service.getWorkRequest(formGroup) as any;

        expect(workRequest).toMatchObject(sampleWithNewData);
      });

      it('should return NewWorkRequest for empty WorkRequest initial value', () => {
        const formGroup = service.createWorkRequestFormGroup();

        const workRequest = service.getWorkRequest(formGroup) as any;

        expect(workRequest).toMatchObject({});
      });

      it('should return IWorkRequest', () => {
        const formGroup = service.createWorkRequestFormGroup(sampleWithRequiredData);

        const workRequest = service.getWorkRequest(formGroup) as any;

        expect(workRequest).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IWorkRequest should not enable id FormControl', () => {
        const formGroup = service.createWorkRequestFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewWorkRequest should disable id FormControl', () => {
        const formGroup = service.createWorkRequestFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
