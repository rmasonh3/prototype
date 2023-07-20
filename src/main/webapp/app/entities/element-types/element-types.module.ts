import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ElementTypesComponent } from './list/element-types.component';
import { ElementTypesDetailComponent } from './detail/element-types-detail.component';
import { ElementTypesUpdateComponent } from './update/element-types-update.component';
import { ElementTypesDeleteDialogComponent } from './delete/element-types-delete-dialog.component';
import { ElementTypesRoutingModule } from './route/element-types-routing.module';

@NgModule({
  imports: [SharedModule, ElementTypesRoutingModule],
  declarations: [ElementTypesComponent, ElementTypesDetailComponent, ElementTypesUpdateComponent, ElementTypesDeleteDialogComponent],
})
export class ElementTypesModule {}
