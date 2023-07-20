import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICostingPercentages } from '../costing-percentages.model';
import { CostingPercentagesService } from '../service/costing-percentages.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './costing-percentages-delete-dialog.component.html',
})
export class CostingPercentagesDeleteDialogComponent {
  costingPercentages?: ICostingPercentages;

  constructor(protected costingPercentagesService: CostingPercentagesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.costingPercentagesService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
