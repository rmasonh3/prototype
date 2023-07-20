import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEstimateBasis } from '../estimate-basis.model';
import { EstimateBasisService } from '../service/estimate-basis.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './estimate-basis-delete-dialog.component.html',
})
export class EstimateBasisDeleteDialogComponent {
  estimateBasis?: IEstimateBasis;

  constructor(protected estimateBasisService: EstimateBasisService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.estimateBasisService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
