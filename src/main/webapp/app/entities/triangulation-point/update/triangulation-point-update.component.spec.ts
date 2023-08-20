import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TriangulationPointFormService } from './triangulation-point-form.service';
import { TriangulationPointService } from '../service/triangulation-point.service';
import { ITriangulationPoint } from '../triangulation-point.model';
import { ITriangulationReport } from 'app/entities/triangulation-report/triangulation-report.model';
import { TriangulationReportService } from 'app/entities/triangulation-report/service/triangulation-report.service';

import { TriangulationPointUpdateComponent } from './triangulation-point-update.component';

describe('TriangulationPoint Management Update Component', () => {
  let comp: TriangulationPointUpdateComponent;
  let fixture: ComponentFixture<TriangulationPointUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let triangulationPointFormService: TriangulationPointFormService;
  let triangulationPointService: TriangulationPointService;
  let triangulationReportService: TriangulationReportService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TriangulationPointUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TriangulationPointUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TriangulationPointUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    triangulationPointFormService = TestBed.inject(TriangulationPointFormService);
    triangulationPointService = TestBed.inject(TriangulationPointService);
    triangulationReportService = TestBed.inject(TriangulationReportService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TriangulationReport query and add missing value', () => {
      const triangulationPoint: ITriangulationPoint = { id: 456 };
      const triangulationReport: ITriangulationReport = { id: 70711 };
      triangulationPoint.triangulationReport = triangulationReport;

      const triangulationReportCollection: ITriangulationReport[] = [{ id: 170 }];
      jest.spyOn(triangulationReportService, 'query').mockReturnValue(of(new HttpResponse({ body: triangulationReportCollection })));
      const additionalTriangulationReports = [triangulationReport];
      const expectedCollection: ITriangulationReport[] = [...additionalTriangulationReports, ...triangulationReportCollection];
      jest.spyOn(triangulationReportService, 'addTriangulationReportToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ triangulationPoint });
      comp.ngOnInit();

      expect(triangulationReportService.query).toHaveBeenCalled();
      expect(triangulationReportService.addTriangulationReportToCollectionIfMissing).toHaveBeenCalledWith(
        triangulationReportCollection,
        ...additionalTriangulationReports.map(expect.objectContaining)
      );
      expect(comp.triangulationReportsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const triangulationPoint: ITriangulationPoint = { id: 456 };
      const triangulationReport: ITriangulationReport = { id: 46176 };
      triangulationPoint.triangulationReport = triangulationReport;

      activatedRoute.data = of({ triangulationPoint });
      comp.ngOnInit();

      expect(comp.triangulationReportsSharedCollection).toContain(triangulationReport);
      expect(comp.triangulationPoint).toEqual(triangulationPoint);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITriangulationPoint>>();
      const triangulationPoint = { id: 123 };
      jest.spyOn(triangulationPointFormService, 'getTriangulationPoint').mockReturnValue(triangulationPoint);
      jest.spyOn(triangulationPointService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ triangulationPoint });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: triangulationPoint }));
      saveSubject.complete();

      // THEN
      expect(triangulationPointFormService.getTriangulationPoint).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(triangulationPointService.update).toHaveBeenCalledWith(expect.objectContaining(triangulationPoint));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITriangulationPoint>>();
      const triangulationPoint = { id: 123 };
      jest.spyOn(triangulationPointFormService, 'getTriangulationPoint').mockReturnValue({ id: null });
      jest.spyOn(triangulationPointService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ triangulationPoint: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: triangulationPoint }));
      saveSubject.complete();

      // THEN
      expect(triangulationPointFormService.getTriangulationPoint).toHaveBeenCalled();
      expect(triangulationPointService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITriangulationPoint>>();
      const triangulationPoint = { id: 123 };
      jest.spyOn(triangulationPointService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ triangulationPoint });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(triangulationPointService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTriangulationReport', () => {
      it('Should forward to triangulationReportService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(triangulationReportService, 'compareTriangulationReport');
        comp.compareTriangulationReport(entity, entity2);
        expect(triangulationReportService.compareTriangulationReport).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
