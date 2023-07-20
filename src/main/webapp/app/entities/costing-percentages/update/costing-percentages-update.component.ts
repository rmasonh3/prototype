import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { CostingPercentagesFormService, CostingPercentagesFormGroup } from './costing-percentages-form.service';
import { ICostingPercentages } from '../costing-percentages.model';
import { CostingPercentagesService } from '../service/costing-percentages.service';

@Component({
  selector: 'jhi-costing-percentages-update',
  templateUrl: './costing-percentages-update.component.html',
})
export class CostingPercentagesUpdateComponent implements OnInit {
  isSaving = false;
  costingPercentages: ICostingPercentages | null = null;

  editForm: CostingPercentagesFormGroup = this.costingPercentagesFormService.createCostingPercentagesFormGroup();

  constructor(
    protected costingPercentagesService: CostingPercentagesService,
    protected costingPercentagesFormService: CostingPercentagesFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ costingPercentages }) => {
      this.costingPercentages = costingPercentages;
      if (costingPercentages) {
        this.updateForm(costingPercentages);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const costingPercentages = this.costingPercentagesFormService.getCostingPercentages(this.editForm);
    if (costingPercentages.id !== null) {
      this.subscribeToSaveResponse(this.costingPercentagesService.update(costingPercentages));
    } else {
      this.subscribeToSaveResponse(this.costingPercentagesService.create(costingPercentages));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICostingPercentages>>): void {
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

  protected updateForm(costingPercentages: ICostingPercentages): void {
    this.costingPercentages = costingPercentages;
    this.costingPercentagesFormService.resetForm(this.editForm, costingPercentages);
  }
}
