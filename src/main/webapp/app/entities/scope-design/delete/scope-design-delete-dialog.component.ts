import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IScopeDesign } from '../scope-design.model';
import { ScopeDesignService } from '../service/scope-design.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './scope-design-delete-dialog.component.html',
})
export class ScopeDesignDeleteDialogComponent {
  scopeDesign?: IScopeDesign;

  constructor(protected scopeDesignService: ScopeDesignService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.scopeDesignService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
