import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TriangulationPointComponent } from './list/triangulation-point.component';
import { TriangulationPointDetailComponent } from './detail/triangulation-point-detail.component';
import { TriangulationPointUpdateComponent } from './update/triangulation-point-update.component';
import { TriangulationPointDeleteDialogComponent } from './delete/triangulation-point-delete-dialog.component';
import { TriangulationPointRoutingModule } from './route/triangulation-point-routing.module';

@NgModule({
  imports: [SharedModule, TriangulationPointRoutingModule],
  declarations: [
    TriangulationPointComponent,
    TriangulationPointDetailComponent,
    TriangulationPointUpdateComponent,
    TriangulationPointDeleteDialogComponent,
  ],
})
export class TriangulationPointModule {}
