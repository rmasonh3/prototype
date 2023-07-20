import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { WorkInfoFormService, WorkInfoFormGroup } from './work-info-form.service';
import { IWorkInfo } from '../work-info.model';
import { WorkInfoService } from '../service/work-info.service';
import { IWorkRequest } from 'app/entities/work-request/work-request.model';
import { WorkRequestService } from 'app/entities/work-request/service/work-request.service';

@Component({
  selector: 'jhi-work-info-update',
  templateUrl: './work-info-update.component.html',
})
export class WorkInfoUpdateComponent implements OnInit {
  isSaving = false;
  workInfo: IWorkInfo | null = null;

  workRequestsSharedCollection: IWorkRequest[] = [];

  editForm: WorkInfoFormGroup = this.workInfoFormService.createWorkInfoFormGroup();

  constructor(
    protected workInfoService: WorkInfoService,
    protected workInfoFormService: WorkInfoFormService,
    protected workRequestService: WorkRequestService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareWorkRequest = (o1: IWorkRequest | null, o2: IWorkRequest | null): boolean => this.workRequestService.compareWorkRequest(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ workInfo }) => {
      this.workInfo = workInfo;
      if (workInfo) {
        this.updateForm(workInfo);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const workInfo = this.workInfoFormService.getWorkInfo(this.editForm);
    if (workInfo.id !== null) {
      this.subscribeToSaveResponse(this.workInfoService.update(workInfo));
    } else {
      this.subscribeToSaveResponse(this.workInfoService.create(workInfo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWorkInfo>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(workInfo: IWorkInfo): void {
    this.workInfo = workInfo;
    this.workInfoFormService.resetForm(this.editForm, workInfo);

    this.workRequestsSharedCollection = this.workRequestService.addWorkRequestToCollectionIfMissing<IWorkRequest>(
      this.workRequestsSharedCollection,
      workInfo.workrequest
    );
  }

  protected loadRelationshipsOptions(): void {
    this.workRequestService
      .query()
      .pipe(map((res: HttpResponse<IWorkRequest[]>) => res.body ?? []))
      .pipe(
        map((workRequests: IWorkRequest[]) =>
          this.workRequestService.addWorkRequestToCollectionIfMissing<IWorkRequest>(workRequests, this.workInfo?.workrequest)
        )
      )
      .subscribe((workRequests: IWorkRequest[]) => (this.workRequestsSharedCollection = workRequests));
  }
}
