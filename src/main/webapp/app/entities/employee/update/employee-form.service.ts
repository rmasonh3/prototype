import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IEmployee, NewEmployee } from '../employee.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEmployee for edit and NewEmployeeFormGroupInput for create.
 */
type EmployeeFormGroupInput = IEmployee | PartialWithRequiredKeyOf<NewEmployee>;

type EmployeeFormDefaults = Pick<NewEmployee, 'id'>;

type EmployeeFormGroupContent = {
  id: FormControl<IEmployee['id'] | NewEmployee['id']>;
  employeeId: FormControl<IEmployee['employeeId']>;
  firstName: FormControl<IEmployee['firstName']>;
  lastName: FormControl<IEmployee['lastName']>;
  email: FormControl<IEmployee['email']>;
  phone: FormControl<IEmployee['phone']>;
  department: FormControl<IEmployee['department']>;
  role: FormControl<IEmployee['role']>;
  user: FormControl<IEmployee['user']>;
  workrequest: FormControl<IEmployee['workrequest']>;
};

export type EmployeeFormGroup = FormGroup<EmployeeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EmployeeFormService {
  createEmployeeFormGroup(employee: EmployeeFormGroupInput = { id: null }): EmployeeFormGroup {
    const employeeRawValue = {
      ...this.getFormDefaults(),
      ...employee,
    };
    return new FormGroup<EmployeeFormGroupContent>({
      id: new FormControl(
        { value: employeeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      employeeId: new FormControl(employeeRawValue.employeeId, {
        validators: [Validators.required],
      }),
      firstName: new FormControl(employeeRawValue.firstName, {
        validators: [Validators.required],
      }),
      lastName: new FormControl(employeeRawValue.lastName, {
        validators: [Validators.required],
      }),
      email: new FormControl(employeeRawValue.email, {
        validators: [Validators.required, Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$')],
      }),
      phone: new FormControl(employeeRawValue.phone, {
        validators: [Validators.required],
      }),
      department: new FormControl(employeeRawValue.department),
      role: new FormControl(employeeRawValue.role),
      user: new FormControl(employeeRawValue.user, {
        validators: [Validators.required],
      }),
      workrequest: new FormControl(employeeRawValue.workrequest, {
        validators: [Validators.required],
      }),
    });
  }

  getEmployee(form: EmployeeFormGroup): IEmployee | NewEmployee {
    return form.getRawValue() as IEmployee | NewEmployee;
  }

  resetForm(form: EmployeeFormGroup, employee: EmployeeFormGroupInput): void {
    const employeeRawValue = { ...this.getFormDefaults(), ...employee };
    form.reset(
      {
        ...employeeRawValue,
        id: { value: employeeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EmployeeFormDefaults {
    return {
      id: null,
    };
  }
}
