import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EstimateBasisComponent } from './list/estimate-basis.component';
import { EstimateBasisDetailComponent } from './detail/estimate-basis-detail.component';
import { EstimateBasisUpdateComponent } from './update/estimate-basis-update.component';
import { EstimateBasisDeleteDialogComponent } from './delete/estimate-basis-delete-dialog.component';
import { EstimateBasisRoutingModule } from './route/estimate-basis-routing.module';

@NgModule({
  imports: [SharedModule, EstimateBasisRoutingModule],
  declarations: [EstimateBasisComponent, EstimateBasisDetailComponent, EstimateBasisUpdateComponent, EstimateBasisDeleteDialogComponent],
})
export class EstimateBasisModule {}
