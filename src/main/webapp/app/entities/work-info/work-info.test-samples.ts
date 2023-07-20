import { IWorkInfo, NewWorkInfo } from './work-info.model';

export const sampleWithRequiredData: IWorkInfo = {
  id: 71377,
  scopeAct: 65921,
  designAct: 71878,
  codeAct: 6304,
  syst1Act: 14488,
  syst2Act: 20467,
  qualAct: 39408,
  impAct: 75852,
  postImpAct: 29221,
  totalAct: 38344,
};

export const sampleWithPartialData: IWorkInfo = {
  id: 81517,
  scopeAct: 51745,
  designAct: 76874,
  codeAct: 88925,
  syst1Act: 68526,
  syst2Act: 83555,
  qualAct: 60161,
  impAct: 20454,
  postImpAct: 3902,
  totalAct: 52050,
};

export const sampleWithFullData: IWorkInfo = {
  id: 491,
  scopeAct: 17716,
  designAct: 52480,
  codeAct: 98571,
  syst1Act: 73555,
  syst2Act: 73312,
  qualAct: 25316,
  impAct: 98011,
  postImpAct: 97368,
  totalAct: 98035,
};

export const sampleWithNewData: NewWorkInfo = {
  scopeAct: 42958,
  designAct: 42025,
  codeAct: 25732,
  syst1Act: 69284,
  syst2Act: 94706,
  qualAct: 9483,
  impAct: 20095,
  postImpAct: 35107,
  totalAct: 43893,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
