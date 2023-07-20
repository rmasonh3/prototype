import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EstimateDesignComponent } from '../list/estimate-design.component';
import { EstimateDesignDetailComponent } from '../detail/estimate-design-detail.component';
import { EstimateDesignUpdateComponent } from '../update/estimate-design-update.component';
import { EstimateDesignRoutingResolveService } from './estimate-design-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const estimateDesignRoute: Routes = [
  {
    path: '',
    component: EstimateDesignComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EstimateDesignDetailComponent,
    resolve: {
      estimateDesign: EstimateDesignRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EstimateDesignUpdateComponent,
    resolve: {
      estimateDesign: EstimateDesignRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EstimateDesignUpdateComponent,
    resolve: {
      estimateDesign: EstimateDesignRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(estimateDesignRoute)],
  exports: [RouterModule],
})
export class EstimateDesignRoutingModule {}
