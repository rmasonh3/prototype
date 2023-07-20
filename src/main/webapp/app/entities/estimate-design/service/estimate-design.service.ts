import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IEstimateDesign, NewEstimateDesign } from '../estimate-design.model';

export type PartialUpdateEstimateDesign = Partial<IEstimateDesign> & Pick<IEstimateDesign, 'id'>;

export type EntityResponseType = HttpResponse<IEstimateDesign>;
export type EntityArrayResponseType = HttpResponse<IEstimateDesign[]>;

@Injectable({ providedIn: 'root' })
export class EstimateDesignService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/estimate-designs');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/estimate-designs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(estimateDesign: NewEstimateDesign): Observable<EntityResponseType> {
    return this.http.post<IEstimateDesign>(this.resourceUrl, estimateDesign, { observe: 'response' });
  }

  update(estimateDesign: IEstimateDesign): Observable<EntityResponseType> {
    return this.http.put<IEstimateDesign>(`${this.resourceUrl}/${this.getEstimateDesignIdentifier(estimateDesign)}`, estimateDesign, {
      observe: 'response',
    });
  }

  partialUpdate(estimateDesign: PartialUpdateEstimateDesign): Observable<EntityResponseType> {
    return this.http.patch<IEstimateDesign>(`${this.resourceUrl}/${this.getEstimateDesignIdentifier(estimateDesign)}`, estimateDesign, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEstimateDesign>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEstimateDesign[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEstimateDesign[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getEstimateDesignIdentifier(estimateDesign: Pick<IEstimateDesign, 'id'>): number {
    return estimateDesign.id;
  }

  compareEstimateDesign(o1: Pick<IEstimateDesign, 'id'> | null, o2: Pick<IEstimateDesign, 'id'> | null): boolean {
    return o1 && o2 ? this.getEstimateDesignIdentifier(o1) === this.getEstimateDesignIdentifier(o2) : o1 === o2;
  }

  addEstimateDesignToCollectionIfMissing<Type extends Pick<IEstimateDesign, 'id'>>(
    estimateDesignCollection: Type[],
    ...estimateDesignsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const estimateDesigns: Type[] = estimateDesignsToCheck.filter(isPresent);
    if (estimateDesigns.length > 0) {
      const estimateDesignCollectionIdentifiers = estimateDesignCollection.map(
        estimateDesignItem => this.getEstimateDesignIdentifier(estimateDesignItem)!
      );
      const estimateDesignsToAdd = estimateDesigns.filter(estimateDesignItem => {
        const estimateDesignIdentifier = this.getEstimateDesignIdentifier(estimateDesignItem);
        if (estimateDesignCollectionIdentifiers.includes(estimateDesignIdentifier)) {
          return false;
        }
        estimateDesignCollectionIdentifiers.push(estimateDesignIdentifier);
        return true;
      });
      return [...estimateDesignsToAdd, ...estimateDesignCollection];
    }
    return estimateDesignCollection;
  }
}
