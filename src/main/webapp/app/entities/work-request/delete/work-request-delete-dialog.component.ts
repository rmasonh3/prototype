import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IWorkRequest } from '../work-request.model';
import { WorkRequestService } from '../service/work-request.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './work-request-delete-dialog.component.html',
})
export class WorkRequestDeleteDialogComponent {
  workRequest?: IWorkRequest;

  constructor(protected workRequestService: WorkRequestService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.workRequestService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
