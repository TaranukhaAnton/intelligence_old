import dayjs from 'dayjs/esm';

import { ITriangulationReport, NewTriangulationReport } from './triangulation-report.model';

export const sampleWithRequiredData: ITriangulationReport = {
  id: 68045,
};

export const sampleWithPartialData: ITriangulationReport = {
  id: 14483,
  date: dayjs('2023-07-30T21:20'),
  name: 'streamline',
  conclusion: 'Technician',
};

export const sampleWithFullData: ITriangulationReport = {
  id: 53447,
  date: dayjs('2023-07-30T16:35'),
  name: 'interface',
  conclusion: 'Card',
};

export const sampleWithNewData: NewTriangulationReport = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
