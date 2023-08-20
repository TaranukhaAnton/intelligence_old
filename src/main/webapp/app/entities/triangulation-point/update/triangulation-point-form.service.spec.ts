import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../triangulation-point.test-samples';

import { TriangulationPointFormService } from './triangulation-point-form.service';

describe('TriangulationPoint Form Service', () => {
  let service: TriangulationPointFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TriangulationPointFormService);
  });

  describe('Service methods', () => {
    describe('createTriangulationPointFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTriangulationPointFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            frequency: expect.any(Object),
            date: expect.any(Object),
            description: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            triangulationReport: expect.any(Object),
          })
        );
      });

      it('passing ITriangulationPoint should create a new form with FormGroup', () => {
        const formGroup = service.createTriangulationPointFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            frequency: expect.any(Object),
            date: expect.any(Object),
            description: expect.any(Object),
            latitude: expect.any(Object),
            longitude: expect.any(Object),
            triangulationReport: expect.any(Object),
          })
        );
      });
    });

    describe('getTriangulationPoint', () => {
      it('should return NewTriangulationPoint for default TriangulationPoint initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTriangulationPointFormGroup(sampleWithNewData);

        const triangulationPoint = service.getTriangulationPoint(formGroup) as any;

        expect(triangulationPoint).toMatchObject(sampleWithNewData);
      });

      it('should return NewTriangulationPoint for empty TriangulationPoint initial value', () => {
        const formGroup = service.createTriangulationPointFormGroup();

        const triangulationPoint = service.getTriangulationPoint(formGroup) as any;

        expect(triangulationPoint).toMatchObject({});
      });

      it('should return ITriangulationPoint', () => {
        const formGroup = service.createTriangulationPointFormGroup(sampleWithRequiredData);

        const triangulationPoint = service.getTriangulationPoint(formGroup) as any;

        expect(triangulationPoint).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITriangulationPoint should not enable id FormControl', () => {
        const formGroup = service.createTriangulationPointFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTriangulationPoint should disable id FormControl', () => {
        const formGroup = service.createTriangulationPointFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
