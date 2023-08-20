import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TriangulationPointComponent } from '../list/triangulation-point.component';
import { TriangulationPointDetailComponent } from '../detail/triangulation-point-detail.component';
import { TriangulationPointUpdateComponent } from '../update/triangulation-point-update.component';
import { TriangulationPointRoutingResolveService } from './triangulation-point-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const triangulationPointRoute: Routes = [
  {
    path: '',
    component: TriangulationPointComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TriangulationPointDetailComponent,
    resolve: {
      triangulationPoint: TriangulationPointRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TriangulationPointUpdateComponent,
    resolve: {
      triangulationPoint: TriangulationPointRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TriangulationPointUpdateComponent,
    resolve: {
      triangulationPoint: TriangulationPointRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(triangulationPointRoute)],
  exports: [RouterModule],
})
export class TriangulationPointRoutingModule {}
