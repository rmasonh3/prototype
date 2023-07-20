import { Complexity } from 'app/entities/enumerations/complexity.model';

import { IEstimateDesign, NewEstimateDesign } from './estimate-design.model';

export const sampleWithRequiredData: IEstimateDesign = {
  id: 2264,
  qpproachNumber: 76235,
};

export const sampleWithPartialData: IEstimateDesign = {
  id: 77662,
  qpproachNumber: 44269,
  complexity: Complexity['Complex'],
};

export const sampleWithFullData: IEstimateDesign = {
  id: 22305,
  qpproachNumber: 95909,
  complexity: Complexity['Average'],
};

export const sampleWithNewData: NewEstimateDesign = {
  qpproachNumber: 48173,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
