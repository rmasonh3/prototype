import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IElementTypes } from '../element-types.model';

@Component({
  selector: 'jhi-element-types-detail',
  templateUrl: './element-types-detail.component.html',
})
export class ElementTypesDetailComponent implements OnInit {
  elementTypes: IElementTypes | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ elementTypes }) => {
      this.elementTypes = elementTypes;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
