import dayjs from 'dayjs/esm';

import { IMessage, NewMessage } from './message.model';

export const sampleWithRequiredData: IMessage = {
  id: 29027,
};

export const sampleWithPartialData: IMessage = {
  id: 5211,
  date: dayjs('2023-07-31T01:08'),
  senderCallSign: 'Group hack Global',
  text: 'customized Table',
};

export const sampleWithFullData: IMessage = {
  id: 29692,
  date: dayjs('2023-07-31T09:31'),
  frequency: 'cross-platform',
  senderCallSign: 'invoice aggregate Germany',
  recieverCallSign: 'Crescent',
  text: 'circuit',
  sourceUuid: 'functionalities',
};

export const sampleWithNewData: NewMessage = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
