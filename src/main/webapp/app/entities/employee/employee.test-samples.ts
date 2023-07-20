import { Department } from 'app/entities/enumerations/department.model';
import { Role } from 'app/entities/enumerations/role.model';

import { IEmployee, NewEmployee } from './employee.model';

export const sampleWithRequiredData: IEmployee = {
  id: 7813,
  employeeId: 'transmitting',
  firstName: 'Marlene',
  lastName: 'Hilpert',
  email: 'yaVP~@;(..~G;r',
  phone: '493-644-9231 x823',
};

export const sampleWithPartialData: IEmployee = {
  id: 93602,
  employeeId: 'National Analyst Cambridgeshire',
  firstName: 'Filomena',
  lastName: 'Hermiston',
  email: '.k7Af@uR<0.`EKG',
  phone: '1-629-854-7566',
  department: Department['PGBA_Client'],
};

export const sampleWithFullData: IEmployee = {
  id: 27248,
  employeeId: 'Music Divide',
  firstName: 'Thomas',
  lastName: 'Monahan',
  email: 'e@}/`C,.xB',
  phone: '368.421.6596 x9089',
  department: Department['PGBA_Business_Support'],
  role: Role['Software_Developer'],
};

export const sampleWithNewData: NewEmployee = {
  employeeId: 'Loan Brand Crossroad',
  firstName: 'Stephany',
  lastName: 'Wisoky',
  email: 'A:ft@|4C.[U7sT',
  phone: '569.378.6293',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
