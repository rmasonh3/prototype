import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IScopeDesign } from '../scope-design.model';
import { ScopeDesignService } from '../service/scope-design.service';

@Injectable({ providedIn: 'root' })
export class ScopeDesignRoutingResolveService implements Resolve<IScopeDesign | null> {
  constructor(protected service: ScopeDesignService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IScopeDesign | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((scopeDesign: HttpResponse<IScopeDesign>) => {
          if (scopeDesign.body) {
            return of(scopeDesign.body);
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
