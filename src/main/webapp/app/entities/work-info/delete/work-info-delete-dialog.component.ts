import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IWorkInfo } from '../work-info.model';
import { WorkInfoService } from '../service/work-info.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './work-info-delete-dialog.component.html',
})
export class WorkInfoDeleteDialogComponent {
  workInfo?: IWorkInfo;

  constructor(protected workInfoService: WorkInfoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.workInfoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
