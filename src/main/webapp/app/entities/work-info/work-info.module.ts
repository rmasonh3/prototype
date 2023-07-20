import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { WorkInfoComponent } from './list/work-info.component';
import { WorkInfoDetailComponent } from './detail/work-info-detail.component';
import { WorkInfoUpdateComponent } from './update/work-info-update.component';
import { WorkInfoDeleteDialogComponent } from './delete/work-info-delete-dialog.component';
import { WorkInfoRoutingModule } from './route/work-info-routing.module';

@NgModule({
  imports: [SharedModule, WorkInfoRoutingModule],
  declarations: [WorkInfoComponent, WorkInfoDetailComponent, WorkInfoUpdateComponent, WorkInfoDeleteDialogComponent],
})
export class WorkInfoModule {}
