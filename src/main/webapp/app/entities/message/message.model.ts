import dayjs from 'dayjs/esm';
import { IReport } from 'app/entities/report/report.model';

export interface IMessage {
  id: number;
  date?: dayjs.Dayjs | null;
  frequency?: string | null;
  senderCallSign?: string | null;
  recieverCallSign?: string | null;
  text?: string | null;
  sourceUuid?: string | null;
  report?: Pick<IReport, 'id'> | null;
}

export type NewMessage = Omit<IMessage, 'id'> & { id: null };
