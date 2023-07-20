import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../scope-design.test-samples';

import { ScopeDesignFormService } from './scope-design-form.service';

describe('ScopeDesign Form Service', () => {
  let service: ScopeDesignFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScopeDesignFormService);
  });

  describe('Service methods', () => {
    describe('createScopeDesignFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createScopeDesignFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            designEstimate: expect.any(Object),
            codeEstimate: expect.any(Object),
            syst1Estimate: expect.any(Object),
            syst2Estimate: expect.any(Object),
            qualEstimate: expect.any(Object),
            impEstimate: expect.any(Object),
            postImpEstimate: expect.any(Object),
            totalHours: expect.any(Object),
            workrequest: expect.any(Object),
          })
        );
      });

      it('passing IScopeDesign should create a new form with FormGroup', () => {
        const formGroup = service.createScopeDesignFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            designEstimate: expect.any(Object),
            codeEstimate: expect.any(Object),
            syst1Estimate: expect.any(Object),
            syst2Estimate: expect.any(Object),
            qualEstimate: expect.any(Object),
            impEstimate: expect.any(Object),
            postImpEstimate: expect.any(Object),
            totalHours: expect.any(Object),
            workrequest: expect.any(Object),
          })
        );
      });
    });

    describe('getScopeDesign', () => {
      it('should return NewScopeDesign for default ScopeDesign initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createScopeDesignFormGroup(sampleWithNewData);

        const scopeDesign = service.getScopeDesign(formGroup) as any;

        expect(scopeDesign).toMatchObject(sampleWithNewData);
      });

      it('should return NewScopeDesign for empty ScopeDesign initial value', () => {
        const formGroup = service.createScopeDesignFormGroup();

        const scopeDesign = service.getScopeDesign(formGroup) as any;

        expect(scopeDesign).toMatchObject({});
      });

      it('should return IScopeDesign', () => {
        const formGroup = service.createScopeDesignFormGroup(sampleWithRequiredData);

        const scopeDesign = service.getScopeDesign(formGroup) as any;

        expect(scopeDesign).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IScopeDesign should not enable id FormControl', () => {
        const formGroup = service.createScopeDesignFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewScopeDesign should disable id FormControl', () => {
        const formGroup = service.createScopeDesignFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
