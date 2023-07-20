import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IWorkInfo } from '../work-info.model';
import { WorkInfoService } from '../service/work-info.service';

@Injectable({ providedIn: 'root' })
export class WorkInfoRoutingResolveService implements Resolve<IWorkInfo | null> {
  constructor(protected service: WorkInfoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IWorkInfo | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((workInfo: HttpResponse<IWorkInfo>) => {
          if (workInfo.body) {
            return of(workInfo.body);
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
