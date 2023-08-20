import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITriangulationReport } from '../triangulation-report.model';

@Component({
  selector: 'jhi-triangulation-report-detail',
  templateUrl: './triangulation-report-detail.component.html',
})
export class TriangulationReportDetailComponent implements OnInit {
  triangulationReport: ITriangulationReport | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ triangulationReport }) => {
      this.triangulationReport = triangulationReport;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
