import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { TriangulationReportFormService, TriangulationReportFormGroup } from './triangulation-report-form.service';
import { ITriangulationReport } from '../triangulation-report.model';
import { TriangulationReportService } from '../service/triangulation-report.service';

@Component({
  selector: 'jhi-triangulation-report-update',
  templateUrl: './triangulation-report-update.component.html',
})
export class TriangulationReportUpdateComponent implements OnInit {
  isSaving = false;
  triangulationReport: ITriangulationReport | null = null;

  editForm: TriangulationReportFormGroup = this.triangulationReportFormService.createTriangulationReportFormGroup();

  constructor(
    protected triangulationReportService: TriangulationReportService,
    protected triangulationReportFormService: TriangulationReportFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ triangulationReport }) => {
      this.triangulationReport = triangulationReport;
      if (triangulationReport) {
        this.updateForm(triangulationReport);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const triangulationReport = this.triangulationReportFormService.getTriangulationReport(this.editForm);
    if (triangulationReport.id !== null) {
      this.subscribeToSaveResponse(this.triangulationReportService.update(triangulationReport));
    } else {
      this.subscribeToSaveResponse(this.triangulationReportService.create(triangulationReport));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITriangulationReport>>): void {
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

  protected updateForm(triangulationReport: ITriangulationReport): void {
    this.triangulationReport = triangulationReport;
    this.triangulationReportFormService.resetForm(this.editForm, triangulationReport);
  }
}
