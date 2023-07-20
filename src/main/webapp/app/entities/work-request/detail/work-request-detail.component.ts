import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IWorkRequest } from '../work-request.model';

@Component({
  selector: 'jhi-work-request-detail',
  templateUrl: './work-request-detail.component.html',
})
export class WorkRequestDetailComponent implements OnInit {
  workRequest: IWorkRequest | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ workRequest }) => {
      this.workRequest = workRequest;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
