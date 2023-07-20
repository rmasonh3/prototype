import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { EstimateBasisFormService, EstimateBasisFormGroup } from './estimate-basis-form.service';
import { IEstimateBasis } from '../estimate-basis.model';
import { EstimateBasisService } from '../service/estimate-basis.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IWorkRequest } from 'app/entities/work-request/work-request.model';
import { WorkRequestService } from 'app/entities/work-request/service/work-request.service';

@Component({
  selector: 'jhi-estimate-basis-update',
  templateUrl: './estimate-basis-update.component.html',
})
export class EstimateBasisUpdateComponent implements OnInit {
  isSaving = false;
  estimateBasis: IEstimateBasis | null = null;

  workRequestsSharedCollection: IWorkRequest[] = [];

  editForm: EstimateBasisFormGroup = this.estimateBasisFormService.createEstimateBasisFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected estimateBasisService: EstimateBasisService,
    protected estimateBasisFormService: EstimateBasisFormService,
    protected workRequestService: WorkRequestService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareWorkRequest = (o1: IWorkRequest | null, o2: IWorkRequest | null): boolean => this.workRequestService.compareWorkRequest(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ estimateBasis }) => {
      this.estimateBasis = estimateBasis;
      if (estimateBasis) {
        this.updateForm(estimateBasis);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('mybujiApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const estimateBasis = this.estimateBasisFormService.getEstimateBasis(this.editForm);
    if (estimateBasis.id !== null) {
      this.subscribeToSaveResponse(this.estimateBasisService.update(estimateBasis));
    } else {
      this.subscribeToSaveResponse(this.estimateBasisService.create(estimateBasis));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEstimateBasis>>): void {
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

  protected updateForm(estimateBasis: IEstimateBasis): void {
    this.estimateBasis = estimateBasis;
    this.estimateBasisFormService.resetForm(this.editForm, estimateBasis);

    this.workRequestsSharedCollection = this.workRequestService.addWorkRequestToCollectionIfMissing<IWorkRequest>(
      this.workRequestsSharedCollection,
      estimateBasis.workrequest
    );
  }

  protected loadRelationshipsOptions(): void {
    this.workRequestService
      .query()
      .pipe(map((res: HttpResponse<IWorkRequest[]>) => res.body ?? []))
      .pipe(
        map((workRequests: IWorkRequest[]) =>
          this.workRequestService.addWorkRequestToCollectionIfMissing<IWorkRequest>(workRequests, this.estimateBasis?.workrequest)
        )
      )
      .subscribe((workRequests: IWorkRequest[]) => (this.workRequestsSharedCollection = workRequests));
  }
}
