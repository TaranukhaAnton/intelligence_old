import dayjs from 'dayjs/esm';

export interface ITriangulationReport {
  id: number;
  date?: dayjs.Dayjs | null;
  name?: string | null;
  conclusion?: string | null;
}

export type NewTriangulationReport = Omit<ITriangulationReport, 'id'> & { id: null };
