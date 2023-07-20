import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEstimateDesign } from '../estimate-design.model';

@Component({
  selector: 'jhi-estimate-design-detail',
  templateUrl: './estimate-design-detail.component.html',
})
export class EstimateDesignDetailComponent implements OnInit {
  estimateDesign: IEstimateDesign | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ estimateDesign }) => {
      this.estimateDesign = estimateDesign;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
