import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TriangulationReportFormService } from './triangulation-report-form.service';
import { TriangulationReportService } from '../service/triangulation-report.service';
import { ITriangulationReport } from '../triangulation-report.model';

import { TriangulationReportUpdateComponent } from './triangulation-report-update.component';

describe('TriangulationReport Management Update Component', () => {
  let comp: TriangulationReportUpdateComponent;
  let fixture: ComponentFixture<TriangulationReportUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let triangulationReportFormService: TriangulationReportFormService;
  let triangulationReportService: TriangulationReportService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TriangulationReportUpdateComponent],
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
      .overrideTemplate(TriangulationReportUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TriangulationReportUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    triangulationReportFormService = TestBed.inject(TriangulationReportFormService);
    triangulationReportService = TestBed.inject(TriangulationReportService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const triangulationReport: ITriangulationReport = { id: 456 };

      activatedRoute.data = of({ triangulationReport });
      comp.ngOnInit();

      expect(comp.triangulationReport).toEqual(triangulationReport);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITriangulationReport>>();
      const triangulationReport = { id: 123 };
      jest.spyOn(triangulationReportFormService, 'getTriangulationReport').mockReturnValue(triangulationReport);
      jest.spyOn(triangulationReportService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ triangulationReport });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: triangulationReport }));
      saveSubject.complete();

      // THEN
      expect(triangulationReportFormService.getTriangulationReport).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(triangulationReportService.update).toHaveBeenCalledWith(expect.objectContaining(triangulationReport));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITriangulationReport>>();
      const triangulationReport = { id: 123 };
      jest.spyOn(triangulationReportFormService, 'getTriangulationReport').mockReturnValue({ id: null });
      jest.spyOn(triangulationReportService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ triangulationReport: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: triangulationReport }));
      saveSubject.complete();

      // THEN
      expect(triangulationReportFormService.getTriangulationReport).toHaveBeenCalled();
      expect(triangulationReportService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITriangulationReport>>();
      const triangulationReport = { id: 123 };
      jest.spyOn(triangulationReportService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ triangulationReport });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(triangulationReportService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
