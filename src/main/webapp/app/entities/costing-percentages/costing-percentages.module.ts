import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CostingPercentagesComponent } from './list/costing-percentages.component';
import { CostingPercentagesDetailComponent } from './detail/costing-percentages-detail.component';
import { CostingPercentagesUpdateComponent } from './update/costing-percentages-update.component';
import { CostingPercentagesDeleteDialogComponent } from './delete/costing-percentages-delete-dialog.component';
import { CostingPercentagesRoutingModule } from './route/costing-percentages-routing.module';

@NgModule({
  imports: [SharedModule, CostingPercentagesRoutingModule],
  declarations: [
    CostingPercentagesComponent,
    CostingPercentagesDetailComponent,
    CostingPercentagesUpdateComponent,
    CostingPercentagesDeleteDialogComponent,
  ],
})
export class CostingPercentagesModule {}
