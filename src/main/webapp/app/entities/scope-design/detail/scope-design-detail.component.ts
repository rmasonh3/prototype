import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IScopeDesign } from '../scope-design.model';

@Component({
  selector: 'jhi-scope-design-detail',
  templateUrl: './scope-design-detail.component.html',
})
export class ScopeDesignDetailComponent implements OnInit {
  scopeDesign: IScopeDesign | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ scopeDesign }) => {
      this.scopeDesign = scopeDesign;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
