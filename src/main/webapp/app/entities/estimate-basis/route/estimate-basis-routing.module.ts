import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EstimateBasisComponent } from '../list/estimate-basis.component';
import { EstimateBasisDetailComponent } from '../detail/estimate-basis-detail.component';
import { EstimateBasisUpdateComponent } from '../update/estimate-basis-update.component';
import { EstimateBasisRoutingResolveService } from './estimate-basis-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const estimateBasisRoute: Routes = [
  {
    path: '',
    component: EstimateBasisComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EstimateBasisDetailComponent,
    resolve: {
      estimateBasis: EstimateBasisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EstimateBasisUpdateComponent,
    resolve: {
      estimateBasis: EstimateBasisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EstimateBasisUpdateComponent,
    resolve: {
      estimateBasis: EstimateBasisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(estimateBasisRoute)],
  exports: [RouterModule],
})
export class EstimateBasisRoutingModule {}
