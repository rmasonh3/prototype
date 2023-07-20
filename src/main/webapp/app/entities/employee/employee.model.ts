import { IUser } from 'app/entities/user/user.model';
import { IWorkRequest } from 'app/entities/work-request/work-request.model';
import { Department } from 'app/entities/enumerations/department.model';
import { Role } from 'app/entities/enumerations/role.model';

export interface IEmployee {
  id: number;
  employeeId?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  phone?: string | null;
  department?: Department | null;
  role?: Role | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  workrequest?: Pick<IWorkRequest, 'id' | 'projectId'> | null;
}

export type NewEmployee = Omit<IEmployee, 'id'> & { id: null };
