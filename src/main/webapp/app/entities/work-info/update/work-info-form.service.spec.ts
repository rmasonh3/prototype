import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../work-info.test-samples';

import { WorkInfoFormService } from './work-info-form.service';

describe('WorkInfo Form Service', () => {
  let service: WorkInfoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WorkInfoFormService);
  });

  describe('Service methods', () => {
    describe('createWorkInfoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createWorkInfoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            scopeAct: expect.any(Object),
            designAct: expect.any(Object),
            codeAct: expect.any(Object),
            syst1Act: expect.any(Object),
            syst2Act: expect.any(Object),
            qualAct: expect.any(Object),
            impAct: expect.any(Object),
            postImpAct: expect.any(Object),
            totalAct: expect.any(Object),
            workrequest: expect.any(Object),
          })
        );
      });

      it('passing IWorkInfo should create a new form with FormGroup', () => {
        const formGroup = service.createWorkInfoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            scopeAct: expect.any(Object),
            designAct: expect.any(Object),
            codeAct: expect.any(Object),
            syst1Act: expect.any(Object),
            syst2Act: expect.any(Object),
            qualAct: expect.any(Object),
            impAct: expect.any(Object),
            postImpAct: expect.any(Object),
            totalAct: expect.any(Object),
            workrequest: expect.any(Object),
          })
        );
      });
    });

    describe('getWorkInfo', () => {
      it('should return NewWorkInfo for default WorkInfo initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createWorkInfoFormGroup(sampleWithNewData);

        const workInfo = service.getWorkInfo(formGroup) as any;

        expect(workInfo).toMatchObject(sampleWithNewData);
      });

      it('should return NewWorkInfo for empty WorkInfo initial value', () => {
        const formGroup = service.createWorkInfoFormGroup();

        const workInfo = service.getWorkInfo(formGroup) as any;

        expect(workInfo).toMatchObject({});
      });

      it('should return IWorkInfo', () => {
        const formGroup = service.createWorkInfoFormGroup(sampleWithRequiredData);

        const workInfo = service.getWorkInfo(formGroup) as any;

        expect(workInfo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IWorkInfo should not enable id FormControl', () => {
        const formGroup = service.createWorkInfoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewWorkInfo should disable id FormControl', () => {
        const formGroup = service.createWorkInfoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
