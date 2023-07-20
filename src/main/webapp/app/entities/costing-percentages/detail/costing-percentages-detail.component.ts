import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICostingPercentages } from '../costing-percentages.model';

@Component({
  selector: 'jhi-costing-percentages-detail',
  templateUrl: './costing-percentages-detail.component.html',
})
export class CostingPercentagesDetailComponent implements OnInit {
  costingPercentages: ICostingPercentages | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ costingPercentages }) => {
      this.costingPercentages = costingPercentages;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
