import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../estimate-basis.test-samples';

import { EstimateBasisFormService } from './estimate-basis-form.service';

describe('EstimateBasis Form Service', () => {
  let service: EstimateBasisFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EstimateBasisFormService);
  });

  describe('Service methods', () => {
    describe('createEstimateBasisFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEstimateBasisFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            subsystemId: expect.any(Object),
            basisOfEstimate: expect.any(Object),
            assumptions: expect.any(Object),
            lastUpdate: expect.any(Object),
            workrequest: expect.any(Object),
          })
        );
      });

      it('passing IEstimateBasis should create a new form with FormGroup', () => {
        const formGroup = service.createEstimateBasisFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            subsystemId: expect.any(Object),
            basisOfEstimate: expect.any(Object),
            assumptions: expect.any(Object),
            lastUpdate: expect.any(Object),
            workrequest: expect.any(Object),
          })
        );
      });
    });

    describe('getEstimateBasis', () => {
      it('should return NewEstimateBasis for default EstimateBasis initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createEstimateBasisFormGroup(sampleWithNewData);

        const estimateBasis = service.getEstimateBasis(formGroup) as any;

        expect(estimateBasis).toMatchObject(sampleWithNewData);
      });

      it('should return NewEstimateBasis for empty EstimateBasis initial value', () => {
        const formGroup = service.createEstimateBasisFormGroup();

        const estimateBasis = service.getEstimateBasis(formGroup) as any;

        expect(estimateBasis).toMatchObject({});
      });

      it('should return IEstimateBasis', () => {
        const formGroup = service.createEstimateBasisFormGroup(sampleWithRequiredData);

        const estimateBasis = service.getEstimateBasis(formGroup) as any;

        expect(estimateBasis).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEstimateBasis should not enable id FormControl', () => {
        const formGroup = service.createEstimateBasisFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEstimateBasis should disable id FormControl', () => {
        const formGroup = service.createEstimateBasisFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
