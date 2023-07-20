import { IWorkRequest } from 'app/entities/work-request/work-request.model';

export interface IScopeDesign {
  id: number;
  designEstimate?: number | null;
  codeEstimate?: number | null;
  syst1Estimate?: number | null;
  syst2Estimate?: number | null;
  qualEstimate?: number | null;
  impEstimate?: number | null;
  postImpEstimate?: number | null;
  totalHours?: number | null;
  workrequest?: Pick<IWorkRequest, 'id' | 'projectId'> | null;
}

export type NewScopeDesign = Omit<IScopeDesign, 'id'> & { id: null };
