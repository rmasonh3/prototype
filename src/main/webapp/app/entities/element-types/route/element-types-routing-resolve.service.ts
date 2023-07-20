import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IElementTypes } from '../element-types.model';
import { ElementTypesService } from '../service/element-types.service';

@Injectable({ providedIn: 'root' })
export class ElementTypesRoutingResolveService implements Resolve<IElementTypes | null> {
  constructor(protected service: ElementTypesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IElementTypes | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((elementTypes: HttpResponse<IElementTypes>) => {
          if (elementTypes.body) {
            return of(elementTypes.body);
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
