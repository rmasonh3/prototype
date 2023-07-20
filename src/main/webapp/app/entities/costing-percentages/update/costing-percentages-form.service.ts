import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICostingPercentages, NewCostingPercentages } from '../costing-percentages.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICostingPercentages for edit and NewCostingPercentagesFormGroupInput for create.
 */
type CostingPercentagesFormGroupInput = ICostingPercentages | PartialWithRequiredKeyOf<NewCostingPercentages>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICostingPercentages | NewCostingPercentages> = Omit<T, 'dateAdded'> & {
  dateAdded?: string | null;
};

type CostingPercentagesFormRawValue = FormValueOf<ICostingPercentages>;

type NewCostingPercentagesFormRawValue = FormValueOf<NewCostingPercentages>;

type CostingPercentagesFormDefaults = Pick<NewCostingPercentages, 'id' | 'active' | 'dateAdded'>;

type CostingPercentagesFormGroupContent = {
  id: FormControl<CostingPercentagesFormRawValue['id'] | NewCostingPercentages['id']>;
  costingSystem: FormControl<CostingPercentagesFormRawValue['costingSystem']>;
  costingQual: FormControl<CostingPercentagesFormRawValue['costingQual']>;
  costingImp: FormControl<CostingPercentagesFormRawValue['costingImp']>;
  costingPostImp: FormControl<CostingPercentagesFormRawValue['costingPostImp']>;
  active: FormControl<CostingPercentagesFormRawValue['active']>;
  dateAdded: FormControl<CostingPercentagesFormRawValue['dateAdded']>;
};

export type CostingPercentagesFormGroup = FormGroup<CostingPercentagesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CostingPercentagesFormService {
  createCostingPercentagesFormGroup(costingPercentages: CostingPercentagesFormGroupInput = { id: null }): CostingPercentagesFormGroup {
    const costingPercentagesRawValue = this.convertCostingPercentagesToCostingPercentagesRawValue({
      ...this.getFormDefaults(),
      ...costingPercentages,
    });
    return new FormGroup<CostingPercentagesFormGroupContent>({
      id: new FormControl(
        { value: costingPercentagesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      costingSystem: new FormControl(costingPercentagesRawValue.costingSystem, {
        validators: [Validators.required],
      }),
      costingQual: new FormControl(costingPercentagesRawValue.costingQual, {
        validators: [Validators.required],
      }),
      costingImp: new FormControl(costingPercentagesRawValue.costingImp, {
        validators: [Validators.required],
      }),
      costingPostImp: new FormControl(costingPercentagesRawValue.costingPostImp, {
        validators: [Validators.required],
      }),
      active: new FormControl(costingPercentagesRawValue.active),
      dateAdded: new FormControl(costingPercentagesRawValue.dateAdded, {
        validators: [Validators.required],
      }),
    });
  }

  getCostingPercentages(form: CostingPercentagesFormGroup): ICostingPercentages | NewCostingPercentages {
    return this.convertCostingPercentagesRawValueToCostingPercentages(
      form.getRawValue() as CostingPercentagesFormRawValue | NewCostingPercentagesFormRawValue
    );
  }

  resetForm(form: CostingPercentagesFormGroup, costingPercentages: CostingPercentagesFormGroupInput): void {
    const costingPercentagesRawValue = this.convertCostingPercentagesToCostingPercentagesRawValue({
      ...this.getFormDefaults(),
      ...costingPercentages,
    });
    form.reset(
      {
        ...costingPercentagesRawValue,
        id: { value: costingPercentagesRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CostingPercentagesFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      dateAdded: currentTime,
    };
  }

  private convertCostingPercentagesRawValueToCostingPercentages(
    rawCostingPercentages: CostingPercentagesFormRawValue | NewCostingPercentagesFormRawValue
  ): ICostingPercentages | NewCostingPercentages {
    return {
      ...rawCostingPercentages,
      dateAdded: dayjs(rawCostingPercentages.dateAdded, DATE_TIME_FORMAT),
    };
  }

  private convertCostingPercentagesToCostingPercentagesRawValue(
    costingPercentages: ICostingPercentages | (Partial<NewCostingPercentages> & CostingPercentagesFormDefaults)
  ): CostingPercentagesFormRawValue | PartialWithRequiredKeyOf<NewCostingPercentagesFormRawValue> {
    return {
      ...costingPercentages,
      dateAdded: costingPercentages.dateAdded ? costingPercentages.dateAdded.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
