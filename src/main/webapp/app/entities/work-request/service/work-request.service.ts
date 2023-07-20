import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IWorkRequest, NewWorkRequest } from '../work-request.model';

export type PartialUpdateWorkRequest = Partial<IWorkRequest> & Pick<IWorkRequest, 'id'>;

type RestOf<T extends IWorkRequest | NewWorkRequest> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestWorkRequest = RestOf<IWorkRequest>;

export type NewRestWorkRequest = RestOf<NewWorkRequest>;

export type PartialUpdateRestWorkRequest = RestOf<PartialUpdateWorkRequest>;

export type EntityResponseType = HttpResponse<IWorkRequest>;
export type EntityArrayResponseType = HttpResponse<IWorkRequest[]>;

@Injectable({ providedIn: 'root' })
export class WorkRequestService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/work-requests');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/work-requests');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(workRequest: NewWorkRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(workRequest);
    return this.http
      .post<RestWorkRequest>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(workRequest: IWorkRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(workRequest);
    return this.http
      .put<RestWorkRequest>(`${this.resourceUrl}/${this.getWorkRequestIdentifier(workRequest)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(workRequest: PartialUpdateWorkRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(workRequest);
    return this.http
      .patch<RestWorkRequest>(`${this.resourceUrl}/${this.getWorkRequestIdentifier(workRequest)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestWorkRequest>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestWorkRequest[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestWorkRequest[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getWorkRequestIdentifier(workRequest: Pick<IWorkRequest, 'id'>): number {
    return workRequest.id;
  }

  compareWorkRequest(o1: Pick<IWorkRequest, 'id'> | null, o2: Pick<IWorkRequest, 'id'> | null): boolean {
    return o1 && o2 ? this.getWorkRequestIdentifier(o1) === this.getWorkRequestIdentifier(o2) : o1 === o2;
  }

  addWorkRequestToCollectionIfMissing<Type extends Pick<IWorkRequest, 'id'>>(
    workRequestCollection: Type[],
    ...workRequestsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const workRequests: Type[] = workRequestsToCheck.filter(isPresent);
    if (workRequests.length > 0) {
      const workRequestCollectionIdentifiers = workRequestCollection.map(
        workRequestItem => this.getWorkRequestIdentifier(workRequestItem)!
      );
      const workRequestsToAdd = workRequests.filter(workRequestItem => {
        const workRequestIdentifier = this.getWorkRequestIdentifier(workRequestItem);
        if (workRequestCollectionIdentifiers.includes(workRequestIdentifier)) {
          return false;
        }
        workRequestCollectionIdentifiers.push(workRequestIdentifier);
        return true;
      });
      return [...workRequestsToAdd, ...workRequestCollection];
    }
    return workRequestCollection;
  }

  protected convertDateFromClient<T extends IWorkRequest | NewWorkRequest | PartialUpdateWorkRequest>(workRequest: T): RestOf<T> {
    return {
      ...workRequest,
      startDate: workRequest.startDate?.toJSON() ?? null,
      endDate: workRequest.endDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restWorkRequest: RestWorkRequest): IWorkRequest {
    return {
      ...restWorkRequest,
      startDate: restWorkRequest.startDate ? dayjs(restWorkRequest.startDate) : undefined,
      endDate: restWorkRequest.endDate ? dayjs(restWorkRequest.endDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestWorkRequest>): HttpResponse<IWorkRequest> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestWorkRequest[]>): HttpResponse<IWorkRequest[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
