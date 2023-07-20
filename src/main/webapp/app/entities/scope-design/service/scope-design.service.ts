import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IScopeDesign, NewScopeDesign } from '../scope-design.model';

export type PartialUpdateScopeDesign = Partial<IScopeDesign> & Pick<IScopeDesign, 'id'>;

export type EntityResponseType = HttpResponse<IScopeDesign>;
export type EntityArrayResponseType = HttpResponse<IScopeDesign[]>;

@Injectable({ providedIn: 'root' })
export class ScopeDesignService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/scope-designs');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/scope-designs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(scopeDesign: NewScopeDesign): Observable<EntityResponseType> {
    return this.http.post<IScopeDesign>(this.resourceUrl, scopeDesign, { observe: 'response' });
  }

  update(scopeDesign: IScopeDesign): Observable<EntityResponseType> {
    return this.http.put<IScopeDesign>(`${this.resourceUrl}/${this.getScopeDesignIdentifier(scopeDesign)}`, scopeDesign, {
      observe: 'response',
    });
  }

  partialUpdate(scopeDesign: PartialUpdateScopeDesign): Observable<EntityResponseType> {
    return this.http.patch<IScopeDesign>(`${this.resourceUrl}/${this.getScopeDesignIdentifier(scopeDesign)}`, scopeDesign, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IScopeDesign>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IScopeDesign[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IScopeDesign[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getScopeDesignIdentifier(scopeDesign: Pick<IScopeDesign, 'id'>): number {
    return scopeDesign.id;
  }

  compareScopeDesign(o1: Pick<IScopeDesign, 'id'> | null, o2: Pick<IScopeDesign, 'id'> | null): boolean {
    return o1 && o2 ? this.getScopeDesignIdentifier(o1) === this.getScopeDesignIdentifier(o2) : o1 === o2;
  }

  addScopeDesignToCollectionIfMissing<Type extends Pick<IScopeDesign, 'id'>>(
    scopeDesignCollection: Type[],
    ...scopeDesignsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const scopeDesigns: Type[] = scopeDesignsToCheck.filter(isPresent);
    if (scopeDesigns.length > 0) {
      const scopeDesignCollectionIdentifiers = scopeDesignCollection.map(
        scopeDesignItem => this.getScopeDesignIdentifier(scopeDesignItem)!
      );
      const scopeDesignsToAdd = scopeDesigns.filter(scopeDesignItem => {
        const scopeDesignIdentifier = this.getScopeDesignIdentifier(scopeDesignItem);
        if (scopeDesignCollectionIdentifiers.includes(scopeDesignIdentifier)) {
          return false;
        }
        scopeDesignCollectionIdentifiers.push(scopeDesignIdentifier);
        return true;
      });
      return [...scopeDesignsToAdd, ...scopeDesignCollection];
    }
    return scopeDesignCollection;
  }
}
