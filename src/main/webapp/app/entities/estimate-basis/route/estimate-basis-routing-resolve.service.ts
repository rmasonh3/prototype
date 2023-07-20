import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEstimateBasis } from '../estimate-basis.model';
import { EstimateBasisService } from '../service/estimate-basis.service';

@Injectable({ providedIn: 'root' })
export class EstimateBasisRoutingResolveService implements Resolve<IEstimateBasis | null> {
  constructor(protected service: EstimateBasisService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEstimateBasis | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((estimateBasis: HttpResponse<IEstimateBasis>) => {
          if (estimateBasis.body) {
            return of(estimateBasis.body);
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
