import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TriangulationPointFormService, TriangulationPointFormGroup } from './triangulation-point-form.service';
import { ITriangulationPoint } from '../triangulation-point.model';
import { TriangulationPointService } from '../service/triangulation-point.service';
import { ITriangulationReport } from 'app/entities/triangulation-report/triangulation-report.model';
import { TriangulationReportService } from 'app/entities/triangulation-report/service/triangulation-report.service';

@Component({
  selector: 'jhi-triangulation-point-update',
  templateUrl: './triangulation-point-update.component.html',
})
export class TriangulationPointUpdateComponent implements OnInit {
  isSaving = false;
  triangulationPoint: ITriangulationPoint | null = null;

  triangulationReportsSharedCollection: ITriangulationReport[] = [];

  editForm: TriangulationPointFormGroup = this.triangulationPointFormService.createTriangulationPointFormGroup();

  constructor(
    protected triangulationPointService: TriangulationPointService,
    protected triangulationPointFormService: TriangulationPointFormService,
    protected triangulationReportService: TriangulationReportService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTriangulationReport = (o1: ITriangulationReport | null, o2: ITriangulationReport | null): boolean =>
    this.triangulationReportService.compareTriangulationReport(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ triangulationPoint }) => {
      this.triangulationPoint = triangulationPoint;
      if (triangulationPoint) {
        this.updateForm(triangulationPoint);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const triangulationPoint = this.triangulationPointFormService.getTriangulationPoint(this.editForm);
    if (triangulationPoint.id !== null) {
      this.subscribeToSaveResponse(this.triangulationPointService.update(triangulationPoint));
    } else {
      this.subscribeToSaveResponse(this.triangulationPointService.create(triangulationPoint));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITriangulationPoint>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(triangulationPoint: ITriangulationPoint): void {
    this.triangulationPoint = triangulationPoint;
    this.triangulationPointFormService.resetForm(this.editForm, triangulationPoint);

    this.triangulationReportsSharedCollection =
      this.triangulationReportService.addTriangulationReportToCollectionIfMissing<ITriangulationReport>(
        this.triangulationReportsSharedCollection,
        triangulationPoint.triangulationReport
      );
  }

  protected loadRelationshipsOptions(): void {
    this.triangulationReportService
      .query()
      .pipe(map((res: HttpResponse<ITriangulationReport[]>) => res.body ?? []))
      .pipe(
        map((triangulationReports: ITriangulationReport[]) =>
          this.triangulationReportService.addTriangulationReportToCollectionIfMissing<ITriangulationReport>(
            triangulationReports,
            this.triangulationPoint?.triangulationReport
          )
        )
      )
      .subscribe((triangulationReports: ITriangulationReport[]) => (this.triangulationReportsSharedCollection = triangulationReports));
  }
}
