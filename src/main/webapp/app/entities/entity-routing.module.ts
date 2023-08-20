import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'report',
        data: { pageTitle: 'Reports' },
        loadChildren: () => import('./report/report.module').then(m => m.ReportModule),
      },
      {
        path: 'message',
        data: { pageTitle: 'Messages' },
        loadChildren: () => import('./message/message.module').then(m => m.MessageModule),
      },
      {
        path: 'triangulation-point',
        data: { pageTitle: 'TriangulationPoints' },
        loadChildren: () => import('./triangulation-point/triangulation-point.module').then(m => m.TriangulationPointModule),
      },
      {
        path: 'triangulation-report',
        data: { pageTitle: 'TriangulationReports' },
        loadChildren: () => import('./triangulation-report/triangulation-report.module').then(m => m.TriangulationReportModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
