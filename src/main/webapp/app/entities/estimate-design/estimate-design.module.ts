import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EstimateDesignComponent } from './list/estimate-design.component';
import { EstimateDesignDetailComponent } from './detail/estimate-design-detail.component';
import { EstimateDesignUpdateComponent } from './update/estimate-design-update.component';
import { EstimateDesignDeleteDialogComponent } from './delete/estimate-design-delete-dialog.component';
import { EstimateDesignRoutingModule } from './route/estimate-design-routing.module';

@NgModule({
  imports: [SharedModule, EstimateDesignRoutingModule],
  declarations: [
    EstimateDesignComponent,
    EstimateDesignDetailComponent,
    EstimateDesignUpdateComponent,
    EstimateDesignDeleteDialogComponent,
  ],
})
export class EstimateDesignModule {}
