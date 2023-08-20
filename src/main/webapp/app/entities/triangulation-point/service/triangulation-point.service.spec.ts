import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITriangulationPoint } from '../triangulation-point.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../triangulation-point.test-samples';

import { TriangulationPointService, RestTriangulationPoint } from './triangulation-point.service';

const requireRestSample: RestTriangulationPoint = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('TriangulationPoint Service', () => {
  let service: TriangulationPointService;
  let httpMock: HttpTestingController;
  let expectedResult: ITriangulationPoint | ITriangulationPoint[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TriangulationPointService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a TriangulationPoint', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const triangulationPoint = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(triangulationPoint).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TriangulationPoint', () => {
      const triangulationPoint = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(triangulationPoint).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TriangulationPoint', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TriangulationPoint', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TriangulationPoint', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTriangulationPointToCollectionIfMissing', () => {
      it('should add a TriangulationPoint to an empty array', () => {
        const triangulationPoint: ITriangulationPoint = sampleWithRequiredData;
        expectedResult = service.addTriangulationPointToCollectionIfMissing([], triangulationPoint);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(triangulationPoint);
      });

      it('should not add a TriangulationPoint to an array that contains it', () => {
        const triangulationPoint: ITriangulationPoint = sampleWithRequiredData;
        const triangulationPointCollection: ITriangulationPoint[] = [
          {
            ...triangulationPoint,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTriangulationPointToCollectionIfMissing(triangulationPointCollection, triangulationPoint);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TriangulationPoint to an array that doesn't contain it", () => {
        const triangulationPoint: ITriangulationPoint = sampleWithRequiredData;
        const triangulationPointCollection: ITriangulationPoint[] = [sampleWithPartialData];
        expectedResult = service.addTriangulationPointToCollectionIfMissing(triangulationPointCollection, triangulationPoint);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(triangulationPoint);
      });

      it('should add only unique TriangulationPoint to an array', () => {
        const triangulationPointArray: ITriangulationPoint[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const triangulationPointCollection: ITriangulationPoint[] = [sampleWithRequiredData];
        expectedResult = service.addTriangulationPointToCollectionIfMissing(triangulationPointCollection, ...triangulationPointArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const triangulationPoint: ITriangulationPoint = sampleWithRequiredData;
        const triangulationPoint2: ITriangulationPoint = sampleWithPartialData;
        expectedResult = service.addTriangulationPointToCollectionIfMissing([], triangulationPoint, triangulationPoint2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(triangulationPoint);
        expect(expectedResult).toContain(triangulationPoint2);
      });

      it('should accept null and undefined values', () => {
        const triangulationPoint: ITriangulationPoint = sampleWithRequiredData;
        expectedResult = service.addTriangulationPointToCollectionIfMissing([], null, triangulationPoint, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(triangulationPoint);
      });

      it('should return initial array if no TriangulationPoint is added', () => {
        const triangulationPointCollection: ITriangulationPoint[] = [sampleWithRequiredData];
        expectedResult = service.addTriangulationPointToCollectionIfMissing(triangulationPointCollection, undefined, null);
        expect(expectedResult).toEqual(triangulationPointCollection);
      });
    });

    describe('compareTriangulationPoint', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTriangulationPoint(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTriangulationPoint(entity1, entity2);
        const compareResult2 = service.compareTriangulationPoint(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTriangulationPoint(entity1, entity2);
        const compareResult2 = service.compareTriangulationPoint(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTriangulationPoint(entity1, entity2);
        const compareResult2 = service.compareTriangulationPoint(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
