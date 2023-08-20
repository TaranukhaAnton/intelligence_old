import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TriangulationReportComponent } from './list/triangulation-report.component';
import { TriangulationReportDetailComponent } from './detail/triangulation-report-detail.component';
import { TriangulationReportUpdateComponent } from './update/triangulation-report-update.component';
import { TriangulationReportDeleteDialogComponent } from './delete/triangulation-report-delete-dialog.component';
import { TriangulationReportRoutingModule } from './route/triangulation-report-routing.module';

@NgModule({
  imports: [SharedModule, TriangulationReportRoutingModule],
  declarations: [
    TriangulationReportComponent,
    TriangulationReportDetailComponent,
    TriangulationReportUpdateComponent,
    TriangulationReportDeleteDialogComponent,
  ],
})
export class TriangulationReportModule {}
