import { IWorkRequest } from 'app/entities/work-request/work-request.model';
import { IElementTypes } from 'app/entities/element-types/element-types.model';
import { Complexity } from 'app/entities/enumerations/complexity.model';

export interface IEstimateDesign {
  id: number;
  qpproachNumber?: number | null;
  complexity?: Complexity | null;
  workrequest?: Pick<IWorkRequest, 'id' | 'projectId'> | null;
  elementtypes?: Pick<IElementTypes, 'id' | 'element'> | null;
}

export type NewEstimateDesign = Omit<IEstimateDesign, 'id'> & { id: null };
