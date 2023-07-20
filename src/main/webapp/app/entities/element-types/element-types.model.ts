import { Type } from 'app/entities/enumerations/type.model';

export interface IElementTypes {
  id: number;
  element?: string | null;
  type?: Type | null;
}

export type NewElementTypes = Omit<IElementTypes, 'id'> & { id: null };
