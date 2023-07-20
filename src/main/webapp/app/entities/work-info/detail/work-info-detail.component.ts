import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IWorkInfo } from '../work-info.model';

@Component({
  selector: 'jhi-work-info-detail',
  templateUrl: './work-info-detail.component.html',
})
export class WorkInfoDetailComponent implements OnInit {
  workInfo: IWorkInfo | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ workInfo }) => {
      this.workInfo = workInfo;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
