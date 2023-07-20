import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ScopeDesignComponent } from '../list/scope-design.component';
import { ScopeDesignDetailComponent } from '../detail/scope-design-detail.component';
import { ScopeDesignUpdateComponent } from '../update/scope-design-update.component';
import { ScopeDesignRoutingResolveService } from './scope-design-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const scopeDesignRoute: Routes = [
  {
    path: '',
    component: ScopeDesignComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ScopeDesignDetailComponent,
    resolve: {
      scopeDesign: ScopeDesignRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ScopeDesignUpdateComponent,
    resolve: {
      scopeDesign: ScopeDesignRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ScopeDesignUpdateComponent,
    resolve: {
      scopeDesign: ScopeDesignRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(scopeDesignRoute)],
  exports: [RouterModule],
})
export class ScopeDesignRoutingModule {}
