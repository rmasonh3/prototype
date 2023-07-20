import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IElementTypes } from '../element-types.model';
import { ElementTypesService } from '../service/element-types.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './element-types-delete-dialog.component.html',
})
export class ElementTypesDeleteDialogComponent {
  elementTypes?: IElementTypes;

  constructor(protected elementTypesService: ElementTypesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.elementTypesService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
