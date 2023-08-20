import dayjs from 'dayjs/esm';

export interface IReport {
  id: number;
  name?: string | null;
  date?: dayjs.Dayjs | null;
  path?: string | null;
  resourceId?: string | null;
  conclusion?: string | null;
  content?: string | null;
  contentContentType?: string | null;
}

export type NewReport = Omit<IReport, 'id'> & { id: null };
