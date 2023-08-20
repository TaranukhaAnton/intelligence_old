import dayjs from 'dayjs/esm';

import { IReport, NewReport } from './report.model';

export const sampleWithRequiredData: IReport = {
  id: 35851,
};

export const sampleWithPartialData: IReport = {
  id: 85044,
  name: 'payment compress',
  date: dayjs('2023-07-31T00:18'),
  conclusion: 'transparent Account Fish',
};

export const sampleWithFullData: IReport = {
  id: 17444,
  name: 'Account extensible',
  date: dayjs('2023-07-30T22:47'),
  path: 'users',
  resourceId: 'Outdoors',
  conclusion: 'adapter Pants Accounts',
  content: '../fake-data/blob/hipster.png',
  contentContentType: 'unknown',
};

export const sampleWithNewData: NewReport = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
