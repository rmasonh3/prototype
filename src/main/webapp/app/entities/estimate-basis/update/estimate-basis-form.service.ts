import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEstimateBasis, NewEstimateBasis } from '../estimate-basis.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEstimateBasis for edit and NewEstimateBasisFormGroupInput for create.
 */
type EstimateBasisFormGroupInput = IEstimateBasis | PartialWithRequiredKeyOf<NewEstimateBasis>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEstimateBasis | NewEstimateBasis> = Omit<T, 'lastUpdate'> & {
  lastUpdate?: string | null;
};

type EstimateBasisFormRawValue = FormValueOf<IEstimateBasis>;

type NewEstimateBasisFormRawValue = FormValueOf<NewEstimateBasis>;

type EstimateBasisFormDefaults = Pick<NewEstimateBasis, 'id' | 'lastUpdate'>;

type EstimateBasisFormGroupContent = {
  id: FormControl<EstimateBasisFormRawValue['id'] | NewEstimateBasis['id']>;
  subsystemId: FormControl<EstimateBasisFormRawValue['subsystemId']>;
  basisOfEstimate: FormControl<EstimateBasisFormRawValue['basisOfEstimate']>;
  assumptions: FormControl<EstimateBasisFormRawValue['assumptions']>;
  lastUpdate: FormControl<EstimateBasisFormRawValue['lastUpdate']>;
  workrequest: FormControl<EstimateBasisFormRawValue['workrequest']>;
};

export type EstimateBasisFormGroup = FormGroup<EstimateBasisFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EstimateBasisFormService {
  createEstimateBasisFormGroup(estimateBasis: EstimateBasisFormGroupInput = { id: null }): EstimateBasisFormGroup {
    const estimateBasisRawValue = this.convertEstimateBasisToEstimateBasisRawValue({
      ...this.getFormDefaults(),
      ...estimateBasis,
    });
    return new FormGroup<EstimateBasisFormGroupContent>({
      id: new FormControl(
        { value: estimateBasisRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      subsystemId: new FormControl(estimateBasisRawValue.subsystemId, {
        validators: [Validators.required],
      }),
      basisOfEstimate: new FormControl(estimateBasisRawValue.basisOfEstimate, {
        validators: [Validators.required],
      }),
      assumptions: new FormControl(estimateBasisRawValue.assumptions, {
        validators: [Validators.required],
      }),
      lastUpdate: new FormControl(estimateBasisRawValue.lastUpdate, {
        validators: [Validators.required],
      }),
      workrequest: new FormControl(estimateBasisRawValue.workrequest, {
        validators: [Validators.required],
      }),
    });
  }

  getEstimateBasis(form: EstimateBasisFormGroup): IEstimateBasis | NewEstimateBasis {
    return this.convertEstimateBasisRawValueToEstimateBasis(form.getRawValue() as EstimateBasisFormRawValue | NewEstimateBasisFormRawValue);
  }

  resetForm(form: EstimateBasisFormGroup, estimateBasis: EstimateBasisFormGroupInput): void {
    const estimateBasisRawValue = this.convertEstimateBasisToEstimateBasisRawValue({ ...this.getFormDefaults(), ...estimateBasis });
    form.reset(
      {
        ...estimateBasisRawValue,
        id: { value: estimateBasisRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EstimateBasisFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastUpdate: currentTime,
    };
  }

  private convertEstimateBasisRawValueToEstimateBasis(
    rawEstimateBasis: EstimateBasisFormRawValue | NewEstimateBasisFormRawValue
  ): IEstimateBasis | NewEstimateBasis {
    return {
      ...rawEstimateBasis,
      lastUpdate: dayjs(rawEstimateBasis.lastUpdate, DATE_TIME_FORMAT),
    };
  }

  private convertEstimateBasisToEstimateBasisRawValue(
    estimateBasis: IEstimateBasis | (Partial<NewEstimateBasis> & EstimateBasisFormDefaults)
  ): EstimateBasisFormRawValue | PartialWithRequiredKeyOf<NewEstimateBasisFormRawValue> {
    return {
      ...estimateBasis,
      lastUpdate: estimateBasis.lastUpdate ? estimateBasis.lastUpdate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
