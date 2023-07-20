import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IElementTypes, NewElementTypes } from '../element-types.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IElementTypes for edit and NewElementTypesFormGroupInput for create.
 */
type ElementTypesFormGroupInput = IElementTypes | PartialWithRequiredKeyOf<NewElementTypes>;

type ElementTypesFormDefaults = Pick<NewElementTypes, 'id'>;

type ElementTypesFormGroupContent = {
  id: FormControl<IElementTypes['id'] | NewElementTypes['id']>;
  element: FormControl<IElementTypes['element']>;
  type: FormControl<IElementTypes['type']>;
};

export type ElementTypesFormGroup = FormGroup<ElementTypesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ElementTypesFormService {
  createElementTypesFormGroup(elementTypes: ElementTypesFormGroupInput = { id: null }): ElementTypesFormGroup {
    const elementTypesRawValue = {
      ...this.getFormDefaults(),
      ...elementTypes,
    };
    return new FormGroup<ElementTypesFormGroupContent>({
      id: new FormControl(
        { value: elementTypesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      element: new FormControl(elementTypesRawValue.element, {
        validators: [Validators.required],
      }),
      type: new FormControl(elementTypesRawValue.type),
    });
  }

  getElementTypes(form: ElementTypesFormGroup): IElementTypes | NewElementTypes {
    return form.getRawValue() as IElementTypes | NewElementTypes;
  }

  resetForm(form: ElementTypesFormGroup, elementTypes: ElementTypesFormGroupInput): void {
    const elementTypesRawValue = { ...this.getFormDefaults(), ...elementTypes };
    form.reset(
      {
        ...elementTypesRawValue,
        id: { value: elementTypesRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ElementTypesFormDefaults {
    return {
      id: null,
    };
  }
}
