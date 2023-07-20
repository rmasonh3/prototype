import { IScopeDesign, NewScopeDesign } from './scope-design.model';

export const sampleWithRequiredData: IScopeDesign = {
  id: 34211,
};

export const sampleWithPartialData: IScopeDesign = {
  id: 84002,
  designEstimate: 73965,
  syst1Estimate: 61457,
  postImpEstimate: 115,
  totalHours: 11861,
};

export const sampleWithFullData: IScopeDesign = {
  id: 37709,
  designEstimate: 70311,
  codeEstimate: 30556,
  syst1Estimate: 800,
  syst2Estimate: 34761,
  qualEstimate: 8243,
  impEstimate: 74137,
  postImpEstimate: 40026,
  totalHours: 11007,
};

export const sampleWithNewData: NewScopeDesign = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
