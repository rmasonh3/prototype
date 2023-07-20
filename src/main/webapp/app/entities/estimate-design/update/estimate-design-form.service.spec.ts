import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../estimate-design.test-samples';

import { EstimateDesignFormService } from './estimate-design-form.service';

describe('EstimateDesign Form Service', () => {
  let service: EstimateDesignFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EstimateDesignFormService);
  });

  describe('Service methods', () => {
    describe('createEstimateDesignFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEstimateDesignFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            qpproachNumber: expect.any(Object),
            complexity: expect.any(Object),
            workrequest: expect.any(Object),
            elementtypes: expect.any(Object),
          })
        );
      });

      it('passing IEstimateDesign should create a new form with FormGroup', () => {
        const formGroup = service.createEstimateDesignFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            qpproachNumber: expect.any(Object),
            complexity: expect.any(Object),
            workrequest: expect.any(Object),
            elementtypes: expect.any(Object),
          })
        );
      });
    });

    describe('getEstimateDesign', () => {
      it('should return NewEstimateDesign for default EstimateDesign initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createEstimateDesignFormGroup(sampleWithNewData);

        const estimateDesign = service.getEstimateDesign(formGroup) as any;

        expect(estimateDesign).toMatchObject(sampleWithNewData);
      });

      it('should return NewEstimateDesign for empty EstimateDesign initial value', () => {
        const formGroup = service.createEstimateDesignFormGroup();

        const estimateDesign = service.getEstimateDesign(formGroup) as any;

        expect(estimateDesign).toMatchObject({});
      });

      it('should return IEstimateDesign', () => {
        const formGroup = service.createEstimateDesignFormGroup(sampleWithRequiredData);

        const estimateDesign = service.getEstimateDesign(formGroup) as any;

        expect(estimateDesign).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEstimateDesign should not enable id FormControl', () => {
        const formGroup = service.createEstimateDesignFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEstimateDesign should disable id FormControl', () => {
        const formGroup = service.createEstimateDesignFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
