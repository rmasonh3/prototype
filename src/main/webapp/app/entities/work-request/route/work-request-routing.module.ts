import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { WorkRequestComponent } from '../list/work-request.component';
import { WorkRequestDetailComponent } from '../detail/work-request-detail.component';
import { WorkRequestUpdateComponent } from '../update/work-request-update.component';
import { WorkRequestRoutingResolveService } from './work-request-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const workRequestRoute: Routes = [
  {
    path: '',
    component: WorkRequestComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: WorkRequestDetailComponent,
    resolve: {
      workRequest: WorkRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: WorkRequestUpdateComponent,
    resolve: {
      workRequest: WorkRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: WorkRequestUpdateComponent,
    resolve: {
      workRequest: WorkRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(workRequestRoute)],
  exports: [RouterModule],
})
export class WorkRequestRoutingModule {}
