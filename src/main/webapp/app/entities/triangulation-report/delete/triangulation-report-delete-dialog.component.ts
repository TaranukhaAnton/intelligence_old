import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITriangulationReport } from '../triangulation-report.model';
import { TriangulationReportService } from '../service/triangulation-report.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './triangulation-report-delete-dialog.component.html',
})
export class TriangulationReportDeleteDialogComponent {
  triangulationReport?: ITriangulationReport;

  constructor(protected triangulationReportService: TriangulationReportService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.triangulationReportService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
