import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICostingPercentages, NewCostingPercentages } from '../costing-percentages.model';

export type PartialUpdateCostingPercentages = Partial<ICostingPercentages> & Pick<ICostingPercentages, 'id'>;

type RestOf<T extends ICostingPercentages | NewCostingPercentages> = Omit<T, 'dateAdded'> & {
  dateAdded?: string | null;
};

export type RestCostingPercentages = RestOf<ICostingPercentages>;

export type NewRestCostingPercentages = RestOf<NewCostingPercentages>;

export type PartialUpdateRestCostingPercentages = RestOf<PartialUpdateCostingPercentages>;

export type EntityResponseType = HttpResponse<ICostingPercentages>;
export type EntityArrayResponseType = HttpResponse<ICostingPercentages[]>;

@Injectable({ providedIn: 'root' })
export class CostingPercentagesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/costing-percentages');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/costing-percentages');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(costingPercentages: NewCostingPercentages): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(costingPercentages);
    return this.http
      .post<RestCostingPercentages>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(costingPercentages: ICostingPercentages): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(costingPercentages);
    return this.http
      .put<RestCostingPercentages>(`${this.resourceUrl}/${this.getCostingPercentagesIdentifier(costingPercentages)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(costingPercentages: PartialUpdateCostingPercentages): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(costingPercentages);
    return this.http
      .patch<RestCostingPercentages>(`${this.resourceUrl}/${this.getCostingPercentagesIdentifier(costingPercentages)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCostingPercentages>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCostingPercentages[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCostingPercentages[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getCostingPercentagesIdentifier(costingPercentages: Pick<ICostingPercentages, 'id'>): number {
    return costingPercentages.id;
  }

  compareCostingPercentages(o1: Pick<ICostingPercentages, 'id'> | null, o2: Pick<ICostingPercentages, 'id'> | null): boolean {
    return o1 && o2 ? this.getCostingPercentagesIdentifier(o1) === this.getCostingPercentagesIdentifier(o2) : o1 === o2;
  }

  addCostingPercentagesToCollectionIfMissing<Type extends Pick<ICostingPercentages, 'id'>>(
    costingPercentagesCollection: Type[],
    ...costingPercentagesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const costingPercentages: Type[] = costingPercentagesToCheck.filter(isPresent);
    if (costingPercentages.length > 0) {
      const costingPercentagesCollectionIdentifiers = costingPercentagesCollection.map(
        costingPercentagesItem => this.getCostingPercentagesIdentifier(costingPercentagesItem)!
      );
      const costingPercentagesToAdd = costingPercentages.filter(costingPercentagesItem => {
        const costingPercentagesIdentifier = this.getCostingPercentagesIdentifier(costingPercentagesItem);
        if (costingPercentagesCollectionIdentifiers.includes(costingPercentagesIdentifier)) {
          return false;
        }
        costingPercentagesCollectionIdentifiers.push(costingPercentagesIdentifier);
        return true;
      });
      return [...costingPercentagesToAdd, ...costingPercentagesCollection];
    }
    return costingPercentagesCollection;
  }

  protected convertDateFromClient<T extends ICostingPercentages | NewCostingPercentages | PartialUpdateCostingPercentages>(
    costingPercentages: T
  ): RestOf<T> {
    return {
      ...costingPercentages,
      dateAdded: costingPercentages.dateAdded?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCostingPercentages: RestCostingPercentages): ICostingPercentages {
    return {
      ...restCostingPercentages,
      dateAdded: restCostingPercentages.dateAdded ? dayjs(restCostingPercentages.dateAdded) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCostingPercentages>): HttpResponse<ICostingPercentages> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCostingPercentages[]>): HttpResponse<ICostingPercentages[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
