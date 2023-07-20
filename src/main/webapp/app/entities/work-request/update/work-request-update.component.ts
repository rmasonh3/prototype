import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { WorkRequestFormService, WorkRequestFormGroup } from './work-request-form.service';
import { IWorkRequest } from '../work-request.model';
import { WorkRequestService } from '../service/work-request.service';
import { ProjectStatus } from 'app/entities/enumerations/project-status.model';
import { DesignStatus } from 'app/entities/enumerations/design-status.model';

@Component({
  selector: 'jhi-work-request-update',
  templateUrl: './work-request-update.component.html',
})
export class WorkRequestUpdateComponent implements OnInit {
  isSaving = false;
  workRequest: IWorkRequest | null = null;
  projectStatusValues = Object.keys(ProjectStatus);
  designStatusValues = Object.keys(DesignStatus);

  editForm: WorkRequestFormGroup = this.workRequestFormService.createWorkRequestFormGroup();

  constructor(
    protected workRequestService: WorkRequestService,
    protected workRequestFormService: WorkRequestFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ workRequest }) => {
      this.workRequest = workRequest;
      if (workRequest) {
        this.updateForm(workRequest);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const workRequest = this.workRequestFormService.getWorkRequest(this.editForm);
    if (workRequest.id !== null) {
      this.subscribeToSaveResponse(this.workRequestService.update(workRequest));
    } else {
      this.subscribeToSaveResponse(this.workRequestService.create(workRequest));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWorkRequest>>): void {
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

  protected updateForm(workRequest: IWorkRequest): void {
    this.workRequest = workRequest;
    this.workRequestFormService.resetForm(this.editForm, workRequest);
  }
}
