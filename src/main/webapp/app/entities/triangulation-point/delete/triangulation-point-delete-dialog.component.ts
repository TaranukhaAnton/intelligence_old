import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITriangulationPoint } from '../triangulation-point.model';
import { TriangulationPointService } from '../service/triangulation-point.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './triangulation-point-delete-dialog.component.html',
})
export class TriangulationPointDeleteDialogComponent {
  triangulationPoint?: ITriangulationPoint;

  constructor(protected triangulationPointService: TriangulationPointService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.triangulationPointService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
