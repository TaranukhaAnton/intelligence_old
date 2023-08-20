import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITriangulationReport } from '../triangulation-report.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../triangulation-report.test-samples';

import { TriangulationReportService, RestTriangulationReport } from './triangulation-report.service';

const requireRestSample: RestTriangulationReport = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('TriangulationReport Service', () => {
  let service: TriangulationReportService;
  let httpMock: HttpTestingController;
  let expectedResult: ITriangulationReport | ITriangulationReport[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TriangulationReportService);
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

    it('should create a TriangulationReport', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const triangulationReport = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(triangulationReport).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TriangulationReport', () => {
      const triangulationReport = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(triangulationReport).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TriangulationReport', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TriangulationReport', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TriangulationReport', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTriangulationReportToCollectionIfMissing', () => {
      it('should add a TriangulationReport to an empty array', () => {
        const triangulationReport: ITriangulationReport = sampleWithRequiredData;
        expectedResult = service.addTriangulationReportToCollectionIfMissing([], triangulationReport);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(triangulationReport);
      });

      it('should not add a TriangulationReport to an array that contains it', () => {
        const triangulationReport: ITriangulationReport = sampleWithRequiredData;
        const triangulationReportCollection: ITriangulationReport[] = [
          {
            ...triangulationReport,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTriangulationReportToCollectionIfMissing(triangulationReportCollection, triangulationReport);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TriangulationReport to an array that doesn't contain it", () => {
        const triangulationReport: ITriangulationReport = sampleWithRequiredData;
        const triangulationReportCollection: ITriangulationReport[] = [sampleWithPartialData];
        expectedResult = service.addTriangulationReportToCollectionIfMissing(triangulationReportCollection, triangulationReport);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(triangulationReport);
      });

      it('should add only unique TriangulationReport to an array', () => {
        const triangulationReportArray: ITriangulationReport[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const triangulationReportCollection: ITriangulationReport[] = [sampleWithRequiredData];
        expectedResult = service.addTriangulationReportToCollectionIfMissing(triangulationReportCollection, ...triangulationReportArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const triangulationReport: ITriangulationReport = sampleWithRequiredData;
        const triangulationReport2: ITriangulationReport = sampleWithPartialData;
        expectedResult = service.addTriangulationReportToCollectionIfMissing([], triangulationReport, triangulationReport2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(triangulationReport);
        expect(expectedResult).toContain(triangulationReport2);
      });

      it('should accept null and undefined values', () => {
        const triangulationReport: ITriangulationReport = sampleWithRequiredData;
        expectedResult = service.addTriangulationReportToCollectionIfMissing([], null, triangulationReport, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(triangulationReport);
      });

      it('should return initial array if no TriangulationReport is added', () => {
        const triangulationReportCollection: ITriangulationReport[] = [sampleWithRequiredData];
        expectedResult = service.addTriangulationReportToCollectionIfMissing(triangulationReportCollection, undefined, null);
        expect(expectedResult).toEqual(triangulationReportCollection);
      });
    });

    describe('compareTriangulationReport', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTriangulationReport(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTriangulationReport(entity1, entity2);
        const compareResult2 = service.compareTriangulationReport(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTriangulationReport(entity1, entity2);
        const compareResult2 = service.compareTriangulationReport(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTriangulationReport(entity1, entity2);
        const compareResult2 = service.compareTriangulationReport(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
