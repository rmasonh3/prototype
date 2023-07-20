import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { EstimateDesignFormService, EstimateDesignFormGroup } from './estimate-design-form.service';
import { IEstimateDesign } from '../estimate-design.model';
import { EstimateDesignService } from '../service/estimate-design.service';
import { IWorkRequest } from 'app/entities/work-request/work-request.model';
import { WorkRequestService } from 'app/entities/work-request/service/work-request.service';
import { IElementTypes } from 'app/entities/element-types/element-types.model';
import { ElementTypesService } from 'app/entities/element-types/service/element-types.service';
import { Complexity } from 'app/entities/enumerations/complexity.model';

@Component({
  selector: 'jhi-estimate-design-update',
  templateUrl: './estimate-design-update.component.html',
})
export class EstimateDesignUpdateComponent implements OnInit {
  isSaving = false;
  estimateDesign: IEstimateDesign | null = null;
  complexityValues = Object.keys(Complexity);

  workRequestsSharedCollection: IWorkRequest[] = [];
  elementTypesSharedCollection: IElementTypes[] = [];

  editForm: EstimateDesignFormGroup = this.estimateDesignFormService.createEstimateDesignFormGroup();

  constructor(
    protected estimateDesignService: EstimateDesignService,
    protected estimateDesignFormService: EstimateDesignFormService,
    protected workRequestService: WorkRequestService,
    protected elementTypesService: ElementTypesService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareWorkRequest = (o1: IWorkRequest | null, o2: IWorkRequest | null): boolean => this.workRequestService.compareWorkRequest(o1, o2);

  compareElementTypes = (o1: IElementTypes | null, o2: IElementTypes | null): boolean =>
    this.elementTypesService.compareElementTypes(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ estimateDesign }) => {
      this.estimateDesign = estimateDesign;
      if (estimateDesign) {
        this.updateForm(estimateDesign);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const estimateDesign = this.estimateDesignFormService.getEstimateDesign(this.editForm);
    if (estimateDesign.id !== null) {
      this.subscribeToSaveResponse(this.estimateDesignService.update(estimateDesign));
    } else {
      this.subscribeToSaveResponse(this.estimateDesignService.create(estimateDesign));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEstimateDesign>>): void {
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

  protected updateForm(estimateDesign: IEstimateDesign): void {
    this.estimateDesign = estimateDesign;
    this.estimateDesignFormService.resetForm(this.editForm, estimateDesign);

    this.workRequestsSharedCollection = this.workRequestService.addWorkRequestToCollectionIfMissing<IWorkRequest>(
      this.workRequestsSharedCollection,
      estimateDesign.workrequest
    );
    this.elementTypesSharedCollection = this.elementTypesService.addElementTypesToCollectionIfMissing<IElementTypes>(
      this.elementTypesSharedCollection,
      estimateDesign.elementtypes
    );
  }

  protected loadRelationshipsOptions(): void {
    this.workRequestService
      .query()
      .pipe(map((res: HttpResponse<IWorkRequest[]>) => res.body ?? []))
      .pipe(
        map((workRequests: IWorkRequest[]) =>
          this.workRequestService.addWorkRequestToCollectionIfMissing<IWorkRequest>(workRequests, this.estimateDesign?.workrequest)
        )
      )
      .subscribe((workRequests: IWorkRequest[]) => (this.workRequestsSharedCollection = workRequests));

    this.elementTypesService
      .query()
      .pipe(map((res: HttpResponse<IElementTypes[]>) => res.body ?? []))
      .pipe(
        map((elementTypes: IElementTypes[]) =>
          this.elementTypesService.addElementTypesToCollectionIfMissing<IElementTypes>(elementTypes, this.estimateDesign?.elementtypes)
        )
      )
      .subscribe((elementTypes: IElementTypes[]) => (this.elementTypesSharedCollection = elementTypes));
  }
}
