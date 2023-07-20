import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CostingPercentagesComponent } from '../list/costing-percentages.component';
import { CostingPercentagesDetailComponent } from '../detail/costing-percentages-detail.component';
import { CostingPercentagesUpdateComponent } from '../update/costing-percentages-update.component';
import { CostingPercentagesRoutingResolveService } from './costing-percentages-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const costingPercentagesRoute: Routes = [
  {
    path: '',
    component: CostingPercentagesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CostingPercentagesDetailComponent,
    resolve: {
      costingPercentages: CostingPercentagesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CostingPercentagesUpdateComponent,
    resolve: {
      costingPercentages: CostingPercentagesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CostingPercentagesUpdateComponent,
    resolve: {
      costingPercentages: CostingPercentagesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(costingPercentagesRoute)],
  exports: [RouterModule],
})
export class CostingPercentagesRoutingModule {}
