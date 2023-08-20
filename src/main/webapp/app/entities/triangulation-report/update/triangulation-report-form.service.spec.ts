import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../triangulation-report.test-samples';

import { TriangulationReportFormService } from './triangulation-report-form.service';

describe('TriangulationReport Form Service', () => {
  let service: TriangulationReportFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TriangulationReportFormService);
  });

  describe('Service methods', () => {
    describe('createTriangulationReportFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTriangulationReportFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            name: expect.any(Object),
            conclusion: expect.any(Object),
          })
        );
      });

      it('passing ITriangulationReport should create a new form with FormGroup', () => {
        const formGroup = service.createTriangulationReportFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            name: expect.any(Object),
            conclusion: expect.any(Object),
          })
        );
      });
    });

    describe('getTriangulationReport', () => {
      it('should return NewTriangulationReport for default TriangulationReport initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTriangulationReportFormGroup(sampleWithNewData);

        const triangulationReport = service.getTriangulationReport(formGroup) as any;

        expect(triangulationReport).toMatchObject(sampleWithNewData);
      });

      it('should return NewTriangulationReport for empty TriangulationReport initial value', () => {
        const formGroup = service.createTriangulationReportFormGroup();

        const triangulationReport = service.getTriangulationReport(formGroup) as any;

        expect(triangulationReport).toMatchObject({});
      });

      it('should return ITriangulationReport', () => {
        const formGroup = service.createTriangulationReportFormGroup(sampleWithRequiredData);

        const triangulationReport = service.getTriangulationReport(formGroup) as any;

        expect(triangulationReport).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITriangulationReport should not enable id FormControl', () => {
        const formGroup = service.createTriangulationReportFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTriangulationReport should disable id FormControl', () => {
        const formGroup = service.createTriangulationReportFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
