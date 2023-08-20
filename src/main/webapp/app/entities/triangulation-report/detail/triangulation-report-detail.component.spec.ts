import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TriangulationReportDetailComponent } from './triangulation-report-detail.component';

describe('TriangulationReport Management Detail Component', () => {
  let comp: TriangulationReportDetailComponent;
  let fixture: ComponentFixture<TriangulationReportDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TriangulationReportDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ triangulationReport: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TriangulationReportDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TriangulationReportDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load triangulationReport on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.triangulationReport).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
