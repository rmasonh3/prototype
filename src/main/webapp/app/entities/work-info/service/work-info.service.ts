import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IWorkInfo, NewWorkInfo } from '../work-info.model';

export type PartialUpdateWorkInfo = Partial<IWorkInfo> & Pick<IWorkInfo, 'id'>;

export type EntityResponseType = HttpResponse<IWorkInfo>;
export type EntityArrayResponseType = HttpResponse<IWorkInfo[]>;

@Injectable({ providedIn: 'root' })
export class WorkInfoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/work-infos');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/work-infos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(workInfo: NewWorkInfo): Observable<EntityResponseType> {
    return this.http.post<IWorkInfo>(this.resourceUrl, workInfo, { observe: 'response' });
  }

  update(workInfo: IWorkInfo): Observable<EntityResponseType> {
    return this.http.put<IWorkInfo>(`${this.resourceUrl}/${this.getWorkInfoIdentifier(workInfo)}`, workInfo, { observe: 'response' });
  }

  partialUpdate(workInfo: PartialUpdateWorkInfo): Observable<EntityResponseType> {
    return this.http.patch<IWorkInfo>(`${this.resourceUrl}/${this.getWorkInfoIdentifier(workInfo)}`, workInfo, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IWorkInfo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IWorkInfo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IWorkInfo[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getWorkInfoIdentifier(workInfo: Pick<IWorkInfo, 'id'>): number {
    return workInfo.id;
  }

  compareWorkInfo(o1: Pick<IWorkInfo, 'id'> | null, o2: Pick<IWorkInfo, 'id'> | null): boolean {
    return o1 && o2 ? this.getWorkInfoIdentifier(o1) === this.getWorkInfoIdentifier(o2) : o1 === o2;
  }

  addWorkInfoToCollectionIfMissing<Type extends Pick<IWorkInfo, 'id'>>(
    workInfoCollection: Type[],
    ...workInfosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const workInfos: Type[] = workInfosToCheck.filter(isPresent);
    if (workInfos.length > 0) {
      const workInfoCollectionIdentifiers = workInfoCollection.map(workInfoItem => this.getWorkInfoIdentifier(workInfoItem)!);
      const workInfosToAdd = workInfos.filter(workInfoItem => {
        const workInfoIdentifier = this.getWorkInfoIdentifier(workInfoItem);
        if (workInfoCollectionIdentifiers.includes(workInfoIdentifier)) {
          return false;
        }
        workInfoCollectionIdentifiers.push(workInfoIdentifier);
        return true;
      });
      return [...workInfosToAdd, ...workInfoCollection];
    }
    return workInfoCollection;
  }
}
