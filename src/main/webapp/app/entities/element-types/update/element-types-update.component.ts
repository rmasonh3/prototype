import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ElementTypesFormService, ElementTypesFormGroup } from './element-types-form.service';
import { IElementTypes } from '../element-types.model';
import { ElementTypesService } from '../service/element-types.service';
import { Type } from 'app/entities/enumerations/type.model';

@Component({
  selector: 'jhi-element-types-update',
  templateUrl: './element-types-update.component.html',
})
export class ElementTypesUpdateComponent implements OnInit {
  isSaving = false;
  elementTypes: IElementTypes | null = null;
  typeValues = Object.keys(Type);

  editForm: ElementTypesFormGroup = this.elementTypesFormService.createElementTypesFormGroup();

  constructor(
    protected elementTypesService: ElementTypesService,
    protected elementTypesFormService: ElementTypesFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ elementTypes }) => {
      this.elementTypes = elementTypes;
      if (elementTypes) {
        this.updateForm(elementTypes);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const elementTypes = this.elementTypesFormService.getElementTypes(this.editForm);
    if (elementTypes.id !== null) {
      this.subscribeToSaveResponse(this.elementTypesService.update(elementTypes));
    } else {
      this.subscribeToSaveResponse(this.elementTypesService.create(elementTypes));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IElementTypes>>): void {
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

  protected updateForm(elementTypes: IElementTypes): void {
    this.elementTypes = elementTypes;
    this.elementTypesFormService.resetForm(this.editForm, elementTypes);
  }
}
