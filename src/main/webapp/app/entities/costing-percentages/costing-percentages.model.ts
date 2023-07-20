import dayjs from 'dayjs/esm';

export interface ICostingPercentages {
  id: number;
  costingSystem?: number | null;
  costingQual?: number | null;
  costingImp?: number | null;
  costingPostImp?: number | null;
  active?: boolean | null;
  dateAdded?: dayjs.Dayjs | null;
}

export type NewCostingPercentages = Omit<ICostingPercentages, 'id'> & { id: null };
