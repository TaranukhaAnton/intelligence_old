import dayjs from 'dayjs/esm';

import { ITriangulationPoint, NewTriangulationPoint } from './triangulation-point.model';

export const sampleWithRequiredData: ITriangulationPoint = {
  id: 87586,
  frequency: 'SAS Rubber bricks-and-clicks',
  latitude: 16258,
  longitude: 11160,
};

export const sampleWithPartialData: ITriangulationPoint = {
  id: 81082,
  frequency: 'focus indexing',
  latitude: 30199,
  longitude: 30556,
};

export const sampleWithFullData: ITriangulationPoint = {
  id: 36348,
  frequency: 'Central Kuna Pataca',
  date: dayjs('2023-07-31T12:33'),
  description: 'Refined Administrator',
  latitude: 80409,
  longitude: 47569,
};

export const sampleWithNewData: NewTriangulationPoint = {
  frequency: 'Oklahoma intuitive encryption',
  latitude: 88011,
  longitude: 22601,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
