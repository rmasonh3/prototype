import { Type } from 'app/entities/enumerations/type.model';

import { IElementTypes, NewElementTypes } from './element-types.model';

export const sampleWithRequiredData: IElementTypes = {
  id: 14795,
  element: '49b6a634-614a-4c1a-8c69-2dd56795d7ce',
};

export const sampleWithPartialData: IElementTypes = {
  id: 41604,
  element: 'cb720639-0bde-4532-bfe4-7205dcb301c5',
  type: Type['New'],
};

export const sampleWithFullData: IElementTypes = {
  id: 25530,
  element: 'ac2ec894-27e0-4d87-84a3-be1f7f5a153b',
  type: Type['Modify'],
};

export const sampleWithNewData: NewElementTypes = {
  element: '13fb4ee7-2eaf-475e-a040-341a2413a13a',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
