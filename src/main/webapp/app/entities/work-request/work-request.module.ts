import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { WorkRequestComponent } from './list/work-request.component';
import { WorkRequestDetailComponent } from './detail/work-request-detail.component';
import { WorkRequestUpdateComponent } from './update/work-request-update.component';
import { WorkRequestDeleteDialogComponent } from './delete/work-request-delete-dialog.component';
import { WorkRequestRoutingModule } from './route/work-request-routing.module';

@NgModule({
  imports: [SharedModule, WorkRequestRoutingModule],
  declarations: [WorkRequestComponent, WorkRequestDetailComponent, WorkRequestUpdateComponent, WorkRequestDeleteDialogComponent],
})
export class WorkRequestModule {}
