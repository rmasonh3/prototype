import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ScopeDesignFormService, ScopeDesignFormGroup } from './scope-design-form.service';
import { IScopeDesign } from '../scope-design.model';
import { ScopeDesignService } from '../service/scope-design.service';
import { IWorkRequest } from 'app/entities/work-request/work-request.model';
import { WorkRequestService } from 'app/entities/work-request/service/work-request.service';

@Component({
  selector: 'jhi-scope-design-update',
  templateUrl: './scope-design-update.component.html',
})
export class ScopeDesignUpdateComponent implements OnInit {
  isSaving = false;
  scopeDesign: IScopeDesign | null = null;

  workRequestsSharedCollection: IWorkRequest[] = [];

  editForm: ScopeDesignFormGroup = this.scopeDesignFormService.createScopeDesignFormGroup();

  constructor(
    protected scopeDesignService: ScopeDesignService,
    protected scopeDesignFormService: ScopeDesignFormService,
    protected workRequestService: WorkRequestService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareWorkRequest = (o1: IWorkRequest | null, o2: IWorkRequest | null): boolean => this.workRequestService.compareWorkRequest(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ scopeDesign }) => {
      this.scopeDesign = scopeDesign;
      if (scopeDesign) {
        this.updateForm(scopeDesign);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const scopeDesign = this.scopeDesignFormService.getScopeDesign(this.editForm);
    if (scopeDesign.id !== null) {
      this.subscribeToSaveResponse(this.scopeDesignService.update(scopeDesign));
    } else {
      this.subscribeToSaveResponse(this.scopeDesignService.create(scopeDesign));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IScopeDesign>>): void {
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

  protected updateForm(scopeDesign: IScopeDesign): void {
    this.scopeDesign = scopeDesign;
    this.scopeDesignFormService.resetForm(this.editForm, scopeDesign);

    this.workRequestsSharedCollection = this.workRequestService.addWorkRequestToCollectionIfMissing<IWorkRequest>(
      this.workRequestsSharedCollection,
      scopeDesign.workrequest
    );
  }

  protected loadRelationshipsOptions(): void {
    this.workRequestService
      .query()
      .pipe(map((res: HttpResponse<IWorkRequest[]>) => res.body ?? []))
      .pipe(
        map((workRequests: IWorkRequest[]) =>
          this.workRequestService.addWorkRequestToCollectionIfMissing<IWorkRequest>(workRequests, this.scopeDesign?.workrequest)
        )
      )
      .subscribe((workRequests: IWorkRequest[]) => (this.workRequestsSharedCollection = workRequests));
  }
}
