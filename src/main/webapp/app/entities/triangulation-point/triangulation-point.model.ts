import dayjs from 'dayjs/esm';
import { ITriangulationReport } from 'app/entities/triangulation-report/triangulation-report.model';

export interface ITriangulationPoint {
  id: number;
  frequency?: string | null;
  date?: dayjs.Dayjs | null;
  description?: string | null;
  latitude?: number | null;
  longitude?: number | null;
  triangulationReport?: Pick<ITriangulationReport, 'id'> | null;
}

export type NewTriangulationPoint = Omit<ITriangulationPoint, 'id'> & { id: null };
