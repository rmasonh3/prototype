import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ElementTypesFormService } from './element-types-form.service';
import { ElementTypesService } from '../service/element-types.service';
import { IElementTypes } from '../element-types.model';

import { ElementTypesUpdateComponent } from './element-types-update.component';

describe('ElementTypes Management Update Component', () => {
  let comp: ElementTypesUpdateComponent;
  let fixture: ComponentFixture<ElementTypesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let elementTypesFormService: ElementTypesFormService;
  let elementTypesService: ElementTypesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ElementTypesUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ElementTypesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ElementTypesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    elementTypesFormService = TestBed.inject(ElementTypesFormService);
    elementTypesService = TestBed.inject(ElementTypesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const elementTypes: IElementTypes = { id: 456 };

      activatedRoute.data = of({ elementTypes });
      comp.ngOnInit();

      expect(comp.elementTypes).toEqual(elementTypes);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IElementTypes>>();
      const elementTypes = { id: 123 };
      jest.spyOn(elementTypesFormService, 'getElementTypes').mockReturnValue(elementTypes);
      jest.spyOn(elementTypesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ elementTypes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: elementTypes }));
      saveSubject.complete();

      // THEN
      expect(elementTypesFormService.getElementTypes).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(elementTypesService.update).toHaveBeenCalledWith(expect.objectContaining(elementTypes));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IElementTypes>>();
      const elementTypes = { id: 123 };
      jest.spyOn(elementTypesFormService, 'getElementTypes').mockReturnValue({ id: null });
      jest.spyOn(elementTypesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ elementTypes: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: elementTypes }));
      saveSubject.complete();

      // THEN
      expect(elementTypesFormService.getElementTypes).toHaveBeenCalled();
      expect(elementTypesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IElementTypes>>();
      const elementTypes = { id: 123 };
      jest.spyOn(elementTypesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ elementTypes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(elementTypesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
