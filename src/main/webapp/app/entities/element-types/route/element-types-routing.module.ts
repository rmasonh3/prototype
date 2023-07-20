import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ElementTypesComponent } from '../list/element-types.component';
import { ElementTypesDetailComponent } from '../detail/element-types-detail.component';
import { ElementTypesUpdateComponent } from '../update/element-types-update.component';
import { ElementTypesRoutingResolveService } from './element-types-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const elementTypesRoute: Routes = [
  {
    path: '',
    component: ElementTypesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ElementTypesDetailComponent,
    resolve: {
      elementTypes: ElementTypesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ElementTypesUpdateComponent,
    resolve: {
      elementTypes: ElementTypesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ElementTypesUpdateComponent,
    resolve: {
      elementTypes: ElementTypesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(elementTypesRoute)],
  exports: [RouterModule],
})
export class ElementTypesRoutingModule {}
