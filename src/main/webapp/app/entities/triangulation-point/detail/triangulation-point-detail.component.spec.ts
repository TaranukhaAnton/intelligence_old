import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TriangulationPointDetailComponent } from './triangulation-point-detail.component';

describe('TriangulationPoint Management Detail Component', () => {
  let comp: TriangulationPointDetailComponent;
  let fixture: ComponentFixture<TriangulationPointDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TriangulationPointDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ triangulationPoint: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TriangulationPointDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TriangulationPointDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load triangulationPoint on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.triangulationPoint).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
