import { IWorkRequest } from 'app/entities/work-request/work-request.model';

export interface IWorkInfo {
  id: number;
  scopeAct?: number | null;
  designAct?: number | null;
  codeAct?: number | null;
  syst1Act?: number | null;
  syst2Act?: number | null;
  qualAct?: number | null;
  impAct?: number | null;
  postImpAct?: number | null;
  totalAct?: number | null;
  workrequest?: Pick<IWorkRequest, 'id' | 'projectId'> | null;
}

export type NewWorkInfo = Omit<IWorkInfo, 'id'> & { id: null };
