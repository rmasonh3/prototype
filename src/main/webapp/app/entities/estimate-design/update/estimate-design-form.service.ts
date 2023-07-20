import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IEstimateDesign, NewEstimateDesign } from '../estimate-design.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEstimateDesign for edit and NewEstimateDesignFormGroupInput for create.
 */
type EstimateDesignFormGroupInput = IEstimateDesign | PartialWithRequiredKeyOf<NewEstimateDesign>;

type EstimateDesignFormDefaults = Pick<NewEstimateDesign, 'id'>;

type EstimateDesignFormGroupContent = {
  id: FormControl<IEstimateDesign['id'] | NewEstimateDesign['id']>;
  qpproachNumber: FormControl<IEstimateDesign['qpproachNumber']>;
  complexity: FormControl<IEstimateDesign['complexity']>;
  workrequest: FormControl<IEstimateDesign['workrequest']>;
  elementtypes: FormControl<IEstimateDesign['elementtypes']>;
};

export type EstimateDesignFormGroup = FormGroup<EstimateDesignFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EstimateDesignFormService {
  createEstimateDesignFormGroup(estimateDesign: EstimateDesignFormGroupInput = { id: null }): EstimateDesignFormGroup {
    const estimateDesignRawValue = {
      ...this.getFormDefaults(),
      ...estimateDesign,
    };
    return new FormGroup<EstimateDesignFormGroupContent>({
      id: new FormControl(
        { value: estimateDesignRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      qpproachNumber: new FormControl(estimateDesignRawValue.qpproachNumber, {
        validators: [Validators.required],
      }),
      complexity: new FormControl(estimateDesignRawValue.complexity),
      workrequest: new FormControl(estimateDesignRawValue.workrequest, {
        validators: [Validators.required],
      }),
      elementtypes: new FormControl(estimateDesignRawValue.elementtypes, {
        validators: [Validators.required],
      }),
    });
  }

  getEstimateDesign(form: EstimateDesignFormGroup): IEstimateDesign | NewEstimateDesign {
    return form.getRawValue() as IEstimateDesign | NewEstimateDesign;
  }

  resetForm(form: EstimateDesignFormGroup, estimateDesign: EstimateDesignFormGroupInput): void {
    const estimateDesignRawValue = { ...this.getFormDefaults(), ...estimateDesign };
    form.reset(
      {
        ...estimateDesignRawValue,
        id: { value: estimateDesignRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EstimateDesignFormDefaults {
    return {
      id: null,
    };
  }
}
