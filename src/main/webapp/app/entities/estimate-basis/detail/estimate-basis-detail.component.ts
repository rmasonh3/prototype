import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEstimateBasis } from '../estimate-basis.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-estimate-basis-detail',
  templateUrl: './estimate-basis-detail.component.html',
})
export class EstimateBasisDetailComponent implements OnInit {
  estimateBasis: IEstimateBasis | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ estimateBasis }) => {
      this.estimateBasis = estimateBasis;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
