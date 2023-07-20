import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../costing-percentages.test-samples';

import { CostingPercentagesFormService } from './costing-percentages-form.service';

describe('CostingPercentages Form Service', () => {
  let service: CostingPercentagesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CostingPercentagesFormService);
  });

  describe('Service methods', () => {
    describe('createCostingPercentagesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCostingPercentagesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            costingSystem: expect.any(Object),
            costingQual: expect.any(Object),
            costingImp: expect.any(Object),
            costingPostImp: expect.any(Object),
            active: expect.any(Object),
            dateAdded: expect.any(Object),
          })
        );
      });

      it('passing ICostingPercentages should create a new form with FormGroup', () => {
        const formGroup = service.createCostingPercentagesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            costingSystem: expect.any(Object),
            costingQual: expect.any(Object),
            costingImp: expect.any(Object),
            costingPostImp: expect.any(Object),
            active: expect.any(Object),
            dateAdded: expect.any(Object),
          })
        );
      });
    });

    describe('getCostingPercentages', () => {
      it('should return NewCostingPercentages for default CostingPercentages initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCostingPercentagesFormGroup(sampleWithNewData);

        const costingPercentages = service.getCostingPercentages(formGroup) as any;

        expect(costingPercentages).toMatchObject(sampleWithNewData);
      });

      it('should return NewCostingPercentages for empty CostingPercentages initial value', () => {
        const formGroup = service.createCostingPercentagesFormGroup();

        const costingPercentages = service.getCostingPercentages(formGroup) as any;

        expect(costingPercentages).toMatchObject({});
      });

      it('should return ICostingPercentages', () => {
        const formGroup = service.createCostingPercentagesFormGroup(sampleWithRequiredData);

        const costingPercentages = service.getCostingPercentages(formGroup) as any;

        expect(costingPercentages).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICostingPercentages should not enable id FormControl', () => {
        const formGroup = service.createCostingPercentagesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCostingPercentages should disable id FormControl', () => {
        const formGroup = service.createCostingPercentagesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
