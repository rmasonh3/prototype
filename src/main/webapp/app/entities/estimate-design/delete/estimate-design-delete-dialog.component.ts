import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEstimateDesign } from '../estimate-design.model';
import { EstimateDesignService } from '../service/estimate-design.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './estimate-design-delete-dialog.component.html',
})
export class EstimateDesignDeleteDialogComponent {
  estimateDesign?: IEstimateDesign;

  constructor(protected estimateDesignService: EstimateDesignService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.estimateDesignService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
