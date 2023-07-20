import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../element-types.test-samples';

import { ElementTypesFormService } from './element-types-form.service';

describe('ElementTypes Form Service', () => {
  let service: ElementTypesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ElementTypesFormService);
  });

  describe('Service methods', () => {
    describe('createElementTypesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createElementTypesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            element: expect.any(Object),
            type: expect.any(Object),
          })
        );
      });

      it('passing IElementTypes should create a new form with FormGroup', () => {
        const formGroup = service.createElementTypesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            element: expect.any(Object),
            type: expect.any(Object),
          })
        );
      });
    });

    describe('getElementTypes', () => {
      it('should return NewElementTypes for default ElementTypes initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createElementTypesFormGroup(sampleWithNewData);

        const elementTypes = service.getElementTypes(formGroup) as any;

        expect(elementTypes).toMatchObject(sampleWithNewData);
      });

      it('should return NewElementTypes for empty ElementTypes initial value', () => {
        const formGroup = service.createElementTypesFormGroup();

        const elementTypes = service.getElementTypes(formGroup) as any;

        expect(elementTypes).toMatchObject({});
      });

      it('should return IElementTypes', () => {
        const formGroup = service.createElementTypesFormGroup(sampleWithRequiredData);

        const elementTypes = service.getElementTypes(formGroup) as any;

        expect(elementTypes).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IElementTypes should not enable id FormControl', () => {
        const formGroup = service.createElementTypesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewElementTypes should disable id FormControl', () => {
        const formGroup = service.createElementTypesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
