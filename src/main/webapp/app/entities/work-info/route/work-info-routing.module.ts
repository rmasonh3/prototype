import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { WorkInfoComponent } from '../list/work-info.component';
import { WorkInfoDetailComponent } from '../detail/work-info-detail.component';
import { WorkInfoUpdateComponent } from '../update/work-info-update.component';
import { WorkInfoRoutingResolveService } from './work-info-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const workInfoRoute: Routes = [
  {
    path: '',
    component: WorkInfoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: WorkInfoDetailComponent,
    resolve: {
      workInfo: WorkInfoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: WorkInfoUpdateComponent,
    resolve: {
      workInfo: WorkInfoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: WorkInfoUpdateComponent,
    resolve: {
      workInfo: WorkInfoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(workInfoRoute)],
  exports: [RouterModule],
})
export class WorkInfoRoutingModule {}
