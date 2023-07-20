import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IWorkInfo, NewWorkInfo } from '../work-info.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWorkInfo for edit and NewWorkInfoFormGroupInput for create.
 */
type WorkInfoFormGroupInput = IWorkInfo | PartialWithRequiredKeyOf<NewWorkInfo>;

type WorkInfoFormDefaults = Pick<NewWorkInfo, 'id'>;

type WorkInfoFormGroupContent = {
  id: FormControl<IWorkInfo['id'] | NewWorkInfo['id']>;
  scopeAct: FormControl<IWorkInfo['scopeAct']>;
  designAct: FormControl<IWorkInfo['designAct']>;
  codeAct: FormControl<IWorkInfo['codeAct']>;
  syst1Act: FormControl<IWorkInfo['syst1Act']>;
  syst2Act: FormControl<IWorkInfo['syst2Act']>;
  qualAct: FormControl<IWorkInfo['qualAct']>;
  impAct: FormControl<IWorkInfo['impAct']>;
  postImpAct: FormControl<IWorkInfo['postImpAct']>;
  totalAct: FormControl<IWorkInfo['totalAct']>;
  workrequest: FormControl<IWorkInfo['workrequest']>;
};

export type WorkInfoFormGroup = FormGroup<WorkInfoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WorkInfoFormService {
  createWorkInfoFormGroup(workInfo: WorkInfoFormGroupInput = { id: null }): WorkInfoFormGroup {
    const workInfoRawValue = {
      ...this.getFormDefaults(),
      ...workInfo,
    };
    return new FormGroup<WorkInfoFormGroupContent>({
      id: new FormControl(
        { value: workInfoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      scopeAct: new FormControl(workInfoRawValue.scopeAct, {
        validators: [Validators.required],
      }),
      designAct: new FormControl(workInfoRawValue.designAct, {
        validators: [Validators.required],
      }),
      codeAct: new FormControl(workInfoRawValue.codeAct, {
        validators: [Validators.required],
      }),
      syst1Act: new FormControl(workInfoRawValue.syst1Act, {
        validators: [Validators.required],
      }),
      syst2Act: new FormControl(workInfoRawValue.syst2Act, {
        validators: [Validators.required],
      }),
      qualAct: new FormControl(workInfoRawValue.qualAct, {
        validators: [Validators.required],
      }),
      impAct: new FormControl(workInfoRawValue.impAct, {
        validators: [Validators.required],
      }),
      postImpAct: new FormControl(workInfoRawValue.postImpAct, {
        validators: [Validators.required],
      }),
      totalAct: new FormControl(workInfoRawValue.totalAct, {
        validators: [Validators.required],
      }),
      workrequest: new FormControl(workInfoRawValue.workrequest, {
        validators: [Validators.required],
      }),
    });
  }

  getWorkInfo(form: WorkInfoFormGroup): IWorkInfo | NewWorkInfo {
    return form.getRawValue() as IWorkInfo | NewWorkInfo;
  }

  resetForm(form: WorkInfoFormGroup, workInfo: WorkInfoFormGroupInput): void {
    const workInfoRawValue = { ...this.getFormDefaults(), ...workInfo };
    form.reset(
      {
        ...workInfoRawValue,
        id: { value: workInfoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): WorkInfoFormDefaults {
    return {
      id: null,
    };
  }
}
