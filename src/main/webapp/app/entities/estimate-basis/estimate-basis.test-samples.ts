import dayjs from 'dayjs/esm';

import { IEstimateBasis, NewEstimateBasis } from './estimate-basis.model';

export const sampleWithRequiredData: IEstimateBasis = {
  id: 80045,
  subsystemId: 82414,
  basisOfEstimate: '../fake-data/blob/hipster.txt',
  assumptions: '../fake-data/blob/hipster.txt',
  lastUpdate: dayjs('2023-07-20T12:02'),
};

export const sampleWithPartialData: IEstimateBasis = {
  id: 22335,
  subsystemId: 86923,
  basisOfEstimate: '../fake-data/blob/hipster.txt',
  assumptions: '../fake-data/blob/hipster.txt',
  lastUpdate: dayjs('2023-07-19T22:46'),
};

export const sampleWithFullData: IEstimateBasis = {
  id: 52535,
  subsystemId: 51774,
  basisOfEstimate: '../fake-data/blob/hipster.txt',
  assumptions: '../fake-data/blob/hipster.txt',
  lastUpdate: dayjs('2023-07-20T03:22'),
};

export const sampleWithNewData: NewEstimateBasis = {
  subsystemId: 80827,
  basisOfEstimate: '../fake-data/blob/hipster.txt',
  assumptions: '../fake-data/blob/hipster.txt',
  lastUpdate: dayjs('2023-07-20T14:34'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
