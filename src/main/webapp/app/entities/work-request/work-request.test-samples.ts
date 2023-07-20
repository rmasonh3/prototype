import dayjs from 'dayjs/esm';

import { ProjectStatus } from 'app/entities/enumerations/project-status.model';
import { DesignStatus } from 'app/entities/enumerations/design-status.model';

import { IWorkRequest, NewWorkRequest } from './work-request.model';

export const sampleWithRequiredData: IWorkRequest = {
  id: 44153,
  projectId: 'web',
  workRequest: 'TCP markets',
  workRequestDescription: 'digital',
  workRwquestPhase: 'virtual application upward-trending',
  startDate: dayjs('2023-07-19T20:51'),
  endDate: dayjs('2023-07-20T17:19'),
};

export const sampleWithPartialData: IWorkRequest = {
  id: 96134,
  projectId: 'Beauty tertiary',
  workRequest: 'RAM invoice (EURCO)',
  workRequestDescription: 'Cotton best-of-breed Argentina',
  workRwquestPhase: 'Frozen Fall',
  startDate: dayjs('2023-07-20T17:28'),
  endDate: dayjs('2023-07-20T06:09'),
};

export const sampleWithFullData: IWorkRequest = {
  id: 55434,
  projectId: 'bluetooth Saint Cotton',
  workRequest: 'International Incredible',
  workRequestDescription: 'Incredible virtual protocol',
  workRwquestPhase: 'reboot',
  startDate: dayjs('2023-07-20T07:35'),
  endDate: dayjs('2023-07-20T17:11'),
  status: ProjectStatus['Pending'],
  design: DesignStatus['OnHold'],
};

export const sampleWithNewData: NewWorkRequest = {
  projectId: 'RSS Wooden redundant',
  workRequest: 'Market',
  workRequestDescription: 'deposit PCI National',
  workRwquestPhase: 'Solutions',
  startDate: dayjs('2023-07-20T11:01'),
  endDate: dayjs('2023-07-20T13:12'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
