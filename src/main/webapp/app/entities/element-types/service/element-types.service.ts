import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IElementTypes, NewElementTypes } from '../element-types.model';

export type PartialUpdateElementTypes = Partial<IElementTypes> & Pick<IElementTypes, 'id'>;

export type EntityResponseType = HttpResponse<IElementTypes>;
export type EntityArrayResponseType = HttpResponse<IElementTypes[]>;

@Injectable({ providedIn: 'root' })
export class ElementTypesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/element-types');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/element-types');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(elementTypes: NewElementTypes): Observable<EntityResponseType> {
    return this.http.post<IElementTypes>(this.resourceUrl, elementTypes, { observe: 'response' });
  }

  update(elementTypes: IElementTypes): Observable<EntityResponseType> {
    return this.http.put<IElementTypes>(`${this.resourceUrl}/${this.getElementTypesIdentifier(elementTypes)}`, elementTypes, {
      observe: 'response',
    });
  }

  partialUpdate(elementTypes: PartialUpdateElementTypes): Observable<EntityResponseType> {
    return this.http.patch<IElementTypes>(`${this.resourceUrl}/${this.getElementTypesIdentifier(elementTypes)}`, elementTypes, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IElementTypes>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IElementTypes[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IElementTypes[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getElementTypesIdentifier(elementTypes: Pick<IElementTypes, 'id'>): number {
    return elementTypes.id;
  }

  compareElementTypes(o1: Pick<IElementTypes, 'id'> | null, o2: Pick<IElementTypes, 'id'> | null): boolean {
    return o1 && o2 ? this.getElementTypesIdentifier(o1) === this.getElementTypesIdentifier(o2) : o1 === o2;
  }

  addElementTypesToCollectionIfMissing<Type extends Pick<IElementTypes, 'id'>>(
    elementTypesCollection: Type[],
    ...elementTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const elementTypes: Type[] = elementTypesToCheck.filter(isPresent);
    if (elementTypes.length > 0) {
      const elementTypesCollectionIdentifiers = elementTypesCollection.map(
        elementTypesItem => this.getElementTypesIdentifier(elementTypesItem)!
      );
      const elementTypesToAdd = elementTypes.filter(elementTypesItem => {
        const elementTypesIdentifier = this.getElementTypesIdentifier(elementTypesItem);
        if (elementTypesCollectionIdentifiers.includes(elementTypesIdentifier)) {
          return false;
        }
        elementTypesCollectionIdentifiers.push(elementTypesIdentifier);
        return true;
      });
      return [...elementTypesToAdd, ...elementTypesCollection];
    }
    return elementTypesCollection;
  }
}
