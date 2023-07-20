import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IWorkRequest } from '../work-request.model';
import { WorkRequestService } from '../service/work-request.service';

@Injectable({ providedIn: 'root' })
export class WorkRequestRoutingResolveService implements Resolve<IWorkRequest | null> {
  constructor(protected service: WorkRequestService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IWorkRequest | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((workRequest: HttpResponse<IWorkRequest>) => {
          if (workRequest.body) {
            return of(workRequest.body);
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
