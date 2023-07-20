import dayjs from 'dayjs/esm';

import { ICostingPercentages, NewCostingPercentages } from './costing-percentages.model';

export const sampleWithRequiredData: ICostingPercentages = {
  id: 61206,
  costingSystem: 76525,
  costingQual: 28633,
  costingImp: 6464,
  costingPostImp: 55844,
  dateAdded: dayjs('2023-07-20T17:40'),
};

export const sampleWithPartialData: ICostingPercentages = {
  id: 38520,
  costingSystem: 79760,
  costingQual: 41626,
  costingImp: 30331,
  costingPostImp: 16391,
  active: false,
  dateAdded: dayjs('2023-07-20T12:55'),
};

export const sampleWithFullData: ICostingPercentages = {
  id: 62969,
  costingSystem: 48561,
  costingQual: 69147,
  costingImp: 34521,
  costingPostImp: 71607,
  active: false,
  dateAdded: dayjs('2023-07-20T00:40'),
};

export const sampleWithNewData: NewCostingPercentages = {
  costingSystem: 36248,
  costingQual: 33715,
  costingImp: 69797,
  costingPostImp: 94296,
  dateAdded: dayjs('2023-07-19T22:31'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
