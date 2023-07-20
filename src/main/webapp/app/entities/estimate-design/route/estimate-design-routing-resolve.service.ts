import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEstimateDesign } from '../estimate-design.model';
import { EstimateDesignService } from '../service/estimate-design.service';

@Injectable({ providedIn: 'root' })
export class EstimateDesignRoutingResolveService implements Resolve<IEstimateDesign | null> {
  constructor(protected service: EstimateDesignService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEstimateDesign | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((estimateDesign: HttpResponse<IEstimateDesign>) => {
          if (estimateDesign.body) {
            return of(estimateDesign.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
