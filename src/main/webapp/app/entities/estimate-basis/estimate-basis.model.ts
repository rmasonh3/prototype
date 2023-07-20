import dayjs from 'dayjs/esm';
import { IWorkRequest } from 'app/entities/work-request/work-request.model';

export interface IEstimateBasis {
  id: number;
  subsystemId?: number | null;
  basisOfEstimate?: string | null;
  assumptions?: string | null;
  lastUpdate?: dayjs.Dayjs | null;
  workrequest?: Pick<IWorkRequest, 'id' | 'projectId'> | null;
}

export type NewEstimateBasis = Omit<IEstimateBasis, 'id'> & { id: null };
