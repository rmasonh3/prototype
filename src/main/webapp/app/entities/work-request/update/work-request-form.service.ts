import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IWorkRequest, NewWorkRequest } from '../work-request.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWorkRequest for edit and NewWorkRequestFormGroupInput for create.
 */
type WorkRequestFormGroupInput = IWorkRequest | PartialWithRequiredKeyOf<NewWorkRequest>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IWorkRequest | NewWorkRequest> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

type WorkRequestFormRawValue = FormValueOf<IWorkRequest>;

type NewWorkRequestFormRawValue = FormValueOf<NewWorkRequest>;

type WorkRequestFormDefaults = Pick<NewWorkRequest, 'id' | 'startDate' | 'endDate'>;

type WorkRequestFormGroupContent = {
  id: FormControl<WorkRequestFormRawValue['id'] | NewWorkRequest['id']>;
  projectId: FormControl<WorkRequestFormRawValue['projectId']>;
  workRequest: FormControl<WorkRequestFormRawValue['workRequest']>;
  workRequestDescription: FormControl<WorkRequestFormRawValue['workRequestDescription']>;
  workRwquestPhase: FormControl<WorkRequestFormRawValue['workRwquestPhase']>;
  startDate: FormControl<WorkRequestFormRawValue['startDate']>;
  endDate: FormControl<WorkRequestFormRawValue['endDate']>;
  status: FormControl<WorkRequestFormRawValue['status']>;
  design: FormControl<WorkRequestFormRawValue['design']>;
};

export type WorkRequestFormGroup = FormGroup<WorkRequestFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WorkRequestFormService {
  createWorkRequestFormGroup(workRequest: WorkRequestFormGroupInput = { id: null }): WorkRequestFormGroup {
    const workRequestRawValue = this.convertWorkRequestToWorkRequestRawValue({
      ...this.getFormDefaults(),
      ...workRequest,
    });
    return new FormGroup<WorkRequestFormGroupContent>({
      id: new FormControl(
        { value: workRequestRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      projectId: new FormControl(workRequestRawValue.projectId, {
        validators: [Validators.required],
      }),
      workRequest: new FormControl(workRequestRawValue.workRequest, {
        validators: [Validators.required],
      }),
      workRequestDescription: new FormControl(workRequestRawValue.workRequestDescription, {
        validators: [Validators.required],
      }),
      workRwquestPhase: new FormControl(workRequestRawValue.workRwquestPhase, {
        validators: [Validators.required],
      }),
      startDate: new FormControl(workRequestRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(workRequestRawValue.endDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(workRequestRawValue.status),
      design: new FormControl(workRequestRawValue.design),
    });
  }

  getWorkRequest(form: WorkRequestFormGroup): IWorkRequest | NewWorkRequest {
    return this.convertWorkRequestRawValueToWorkRequest(form.getRawValue() as WorkRequestFormRawValue | NewWorkRequestFormRawValue);
  }

  resetForm(form: WorkRequestFormGroup, workRequest: WorkRequestFormGroupInput): void {
    const workRequestRawValue = this.convertWorkRequestToWorkRequestRawValue({ ...this.getFormDefaults(), ...workRequest });
    form.reset(
      {
        ...workRequestRawValue,
        id: { value: workRequestRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): WorkRequestFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDate: currentTime,
      endDate: currentTime,
    };
  }

  private convertWorkRequestRawValueToWorkRequest(
    rawWorkRequest: WorkRequestFormRawValue | NewWorkRequestFormRawValue
  ): IWorkRequest | NewWorkRequest {
    return {
      ...rawWorkRequest,
      startDate: dayjs(rawWorkRequest.startDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawWorkRequest.endDate, DATE_TIME_FORMAT),
    };
  }

  private convertWorkRequestToWorkRequestRawValue(
    workRequest: IWorkRequest | (Partial<NewWorkRequest> & WorkRequestFormDefaults)
  ): WorkRequestFormRawValue | PartialWithRequiredKeyOf<NewWorkRequestFormRawValue> {
    return {
      ...workRequest,
      startDate: workRequest.startDate ? workRequest.startDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: workRequest.endDate ? workRequest.endDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
