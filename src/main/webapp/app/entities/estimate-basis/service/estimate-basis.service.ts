import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IEstimateBasis, NewEstimateBasis } from '../estimate-basis.model';

export type PartialUpdateEstimateBasis = Partial<IEstimateBasis> & Pick<IEstimateBasis, 'id'>;

type RestOf<T extends IEstimateBasis | NewEstimateBasis> = Omit<T, 'lastUpdate'> & {
  lastUpdate?: string | null;
};

export type RestEstimateBasis = RestOf<IEstimateBasis>;

export type NewRestEstimateBasis = RestOf<NewEstimateBasis>;

export type PartialUpdateRestEstimateBasis = RestOf<PartialUpdateEstimateBasis>;

export type EntityResponseType = HttpResponse<IEstimateBasis>;
export type EntityArrayResponseType = HttpResponse<IEstimateBasis[]>;

@Injectable({ providedIn: 'root' })
export class EstimateBasisService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/estimate-bases');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/estimate-bases');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(estimateBasis: NewEstimateBasis): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(estimateBasis);
    return this.http
      .post<RestEstimateBasis>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(estimateBasis: IEstimateBasis): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(estimateBasis);
    return this.http
      .put<RestEstimateBasis>(`${this.resourceUrl}/${this.getEstimateBasisIdentifier(estimateBasis)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(estimateBasis: PartialUpdateEstimateBasis): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(estimateBasis);
    return this.http
      .patch<RestEstimateBasis>(`${this.resourceUrl}/${this.getEstimateBasisIdentifier(estimateBasis)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestEstimateBasis>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEstimateBasis[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEstimateBasis[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getEstimateBasisIdentifier(estimateBasis: Pick<IEstimateBasis, 'id'>): number {
    return estimateBasis.id;
  }

  compareEstimateBasis(o1: Pick<IEstimateBasis, 'id'> | null, o2: Pick<IEstimateBasis, 'id'> | null): boolean {
    return o1 && o2 ? this.getEstimateBasisIdentifier(o1) === this.getEstimateBasisIdentifier(o2) : o1 === o2;
  }

  addEstimateBasisToCollectionIfMissing<Type extends Pick<IEstimateBasis, 'id'>>(
    estimateBasisCollection: Type[],
    ...estimateBasesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const estimateBases: Type[] = estimateBasesToCheck.filter(isPresent);
    if (estimateBases.length > 0) {
      const estimateBasisCollectionIdentifiers = estimateBasisCollection.map(
        estimateBasisItem => this.getEstimateBasisIdentifier(estimateBasisItem)!
      );
      const estimateBasesToAdd = estimateBases.filter(estimateBasisItem => {
        const estimateBasisIdentifier = this.getEstimateBasisIdentifier(estimateBasisItem);
        if (estimateBasisCollectionIdentifiers.includes(estimateBasisIdentifier)) {
          return false;
        }
        estimateBasisCollectionIdentifiers.push(estimateBasisIdentifier);
        return true;
      });
      return [...estimateBasesToAdd, ...estimateBasisCollection];
    }
    return estimateBasisCollection;
  }

  protected convertDateFromClient<T extends IEstimateBasis | NewEstimateBasis | PartialUpdateEstimateBasis>(estimateBasis: T): RestOf<T> {
    return {
      ...estimateBasis,
      lastUpdate: estimateBasis.lastUpdate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restEstimateBasis: RestEstimateBasis): IEstimateBasis {
    return {
      ...restEstimateBasis,
      lastUpdate: restEstimateBasis.lastUpdate ? dayjs(restEstimateBasis.lastUpdate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestEstimateBasis>): HttpResponse<IEstimateBasis> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestEstimateBasis[]>): HttpResponse<IEstimateBasis[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
