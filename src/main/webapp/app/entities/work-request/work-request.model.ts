import dayjs from 'dayjs/esm';
import { ProjectStatus } from 'app/entities/enumerations/project-status.model';
import { DesignStatus } from 'app/entities/enumerations/design-status.model';

export interface IWorkRequest {
  id: number;
  projectId?: string | null;
  workRequest?: string | null;
  workRequestDescription?: string | null;
  workRwquestPhase?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  status?: ProjectStatus | null;
  design?: DesignStatus | null;
}

export type NewWorkRequest = Omit<IWorkRequest, 'id'> & { id: null };
