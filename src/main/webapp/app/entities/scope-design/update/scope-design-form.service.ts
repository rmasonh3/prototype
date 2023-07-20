import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IScopeDesign, NewScopeDesign } from '../scope-design.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IScopeDesign for edit and NewScopeDesignFormGroupInput for create.
 */
type ScopeDesignFormGroupInput = IScopeDesign | PartialWithRequiredKeyOf<NewScopeDesign>;

type ScopeDesignFormDefaults = Pick<NewScopeDesign, 'id'>;

type ScopeDesignFormGroupContent = {
  id: FormControl<IScopeDesign['id'] | NewScopeDesign['id']>;
  designEstimate: FormControl<IScopeDesign['designEstimate']>;
  codeEstimate: FormControl<IScopeDesign['codeEstimate']>;
  syst1Estimate: FormControl<IScopeDesign['syst1Estimate']>;
  syst2Estimate: FormControl<IScopeDesign['syst2Estimate']>;
  qualEstimate: FormControl<IScopeDesign['qualEstimate']>;
  impEstimate: FormControl<IScopeDesign['impEstimate']>;
  postImpEstimate: FormControl<IScopeDesign['postImpEstimate']>;
  totalHours: FormControl<IScopeDesign['totalHours']>;
  workrequest: FormControl<IScopeDesign['workrequest']>;
};

export type ScopeDesignFormGroup = FormGroup<ScopeDesignFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ScopeDesignFormService {
  createScopeDesignFormGroup(scopeDesign: ScopeDesignFormGroupInput = { id: null }): ScopeDesignFormGroup {
    const scopeDesignRawValue = {
      ...this.getFormDefaults(),
      ...scopeDesign,
    };
    return new FormGroup<ScopeDesignFormGroupContent>({
      id: new FormControl(
        { value: scopeDesignRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      designEstimate: new FormControl(scopeDesignRawValue.designEstimate),
      codeEstimate: new FormControl(scopeDesignRawValue.codeEstimate),
      syst1Estimate: new FormControl(scopeDesignRawValue.syst1Estimate),
      syst2Estimate: new FormControl(scopeDesignRawValue.syst2Estimate),
      qualEstimate: new FormControl(scopeDesignRawValue.qualEstimate),
      impEstimate: new FormControl(scopeDesignRawValue.impEstimate),
      postImpEstimate: new FormControl(scopeDesignRawValue.postImpEstimate),
      totalHours: new FormControl(scopeDesignRawValue.totalHours),
      workrequest: new FormControl(scopeDesignRawValue.workrequest, {
        validators: [Validators.required],
      }),
    });
  }

  getScopeDesign(form: ScopeDesignFormGroup): IScopeDesign | NewScopeDesign {
    return form.getRawValue() as IScopeDesign | NewScopeDesign;
  }

  resetForm(form: ScopeDesignFormGroup, scopeDesign: ScopeDesignFormGroupInput): void {
    const scopeDesignRawValue = { ...this.getFormDefaults(), ...scopeDesign };
    form.reset(
      {
        ...scopeDesignRawValue,
        id: { value: scopeDesignRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ScopeDesignFormDefaults {
    return {
      id: null,
    };
  }
}
