import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'estimate-design',
        data: { pageTitle: 'mybujiApp.estimateDesign.home.title' },
        loadChildren: () => import('./estimate-design/estimate-design.module').then(m => m.EstimateDesignModule),
      },
      {
        path: 'scope-design',
        data: { pageTitle: 'mybujiApp.scopeDesign.home.title' },
        loadChildren: () => import('./scope-design/scope-design.module').then(m => m.ScopeDesignModule),
      },
      {
        path: 'work-info',
        data: { pageTitle: 'mybujiApp.workInfo.home.title' },
        loadChildren: () => import('./work-info/work-info.module').then(m => m.WorkInfoModule),
      },
      {
        path: 'estimate-basis',
        data: { pageTitle: 'mybujiApp.estimateBasis.home.title' },
        loadChildren: () => import('./estimate-basis/estimate-basis.module').then(m => m.EstimateBasisModule),
      },
      {
        path: 'costing-percentages',
        data: { pageTitle: 'mybujiApp.costingPercentages.home.title' },
        loadChildren: () => import('./costing-percentages/costing-percentages.module').then(m => m.CostingPercentagesModule),
      },
      {
        path: 'element-types',
        data: { pageTitle: 'mybujiApp.elementTypes.home.title' },
        loadChildren: () => import('./element-types/element-types.module').then(m => m.ElementTypesModule),
      },
      {
        path: 'work-request',
        data: { pageTitle: 'mybujiApp.workRequest.home.title' },
        loadChildren: () => import('./work-request/work-request.module').then(m => m.WorkRequestModule),
      },
      {
        path: 'employee',
        data: { pageTitle: 'mybujiApp.employee.home.title' },
        loadChildren: () => import('./employee/employee.module').then(m => m.EmployeeModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
