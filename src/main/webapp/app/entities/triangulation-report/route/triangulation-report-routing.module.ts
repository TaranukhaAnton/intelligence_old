import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TriangulationReportComponent } from '../list/triangulation-report.component';
import { TriangulationReportDetailComponent } from '../detail/triangulation-report-detail.component';
import { TriangulationReportUpdateComponent } from '../update/triangulation-report-update.component';
import { TriangulationReportRoutingResolveService } from './triangulation-report-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const triangulationReportRoute: Routes = [
  {
    path: '',
    component: TriangulationReportComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TriangulationReportDetailComponent,
    resolve: {
      triangulationReport: TriangulationReportRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TriangulationReportUpdateComponent,
    resolve: {
      triangulationReport: TriangulationReportRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TriangulationReportUpdateComponent,
    resolve: {
      triangulationReport: TriangulationReportRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(triangulationReportRoute)],
  exports: [RouterModule],
})
export class TriangulationReportRoutingModule {}
