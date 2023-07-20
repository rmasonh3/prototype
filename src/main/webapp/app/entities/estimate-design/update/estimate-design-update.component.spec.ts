import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EstimateDesignFormService } from './estimate-design-form.service';
import { EstimateDesignService } from '../service/estimate-design.service';
import { IEstimateDesign } from '../estimate-design.model';
import { IWorkRequest } from 'app/entities/work-request/work-request.model';
import { WorkRequestService } from 'app/entities/work-request/service/work-request.service';
import { IElementTypes } from 'app/entities/element-types/element-types.model';
import { ElementTypesService } from 'app/entities/element-types/service/element-types.service';

import { EstimateDesignUpdateComponent } from './estimate-design-update.component';

describe('EstimateDesign Management Update Component', () => {
  let comp: EstimateDesignUpdateComponent;
  let fixture: ComponentFixture<EstimateDesignUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let estimateDesignFormService: EstimateDesignFormService;
  let estimateDesignService: EstimateDesignService;
  let workRequestService: WorkRequestService;
  let elementTypesService: ElementTypesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EstimateDesignUpdateComponent],
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
      .overrideTemplate(EstimateDesignUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EstimateDesignUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    estimateDesignFormService = TestBed.inject(EstimateDesignFormService);
    estimateDesignService = TestBed.inject(EstimateDesignService);
    workRequestService = TestBed.inject(WorkRequestService);
    elementTypesService = TestBed.inject(ElementTypesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call WorkRequest query and add missing value', () => {
      const estimateDesign: IEstimateDesign = { id: 456 };
      const workrequest: IWorkRequest = { id: 3699 };
      estimateDesign.workrequest = workrequest;

      const workRequestCollection: IWorkRequest[] = [{ id: 86247 }];
      jest.spyOn(workRequestService, 'query').mockReturnValue(of(new HttpResponse({ body: workRequestCollection })));
      const additionalWorkRequests = [workrequest];
      const expectedCollection: IWorkRequest[] = [...additionalWorkRequests, ...workRequestCollection];
      jest.spyOn(workRequestService, 'addWorkRequestToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ estimateDesign });
      comp.ngOnInit();

      expect(workRequestService.query).toHaveBeenCalled();
      expect(workRequestService.addWorkRequestToCollectionIfMissing).toHaveBeenCalledWith(
        workRequestCollection,
        ...additionalWorkRequests.map(expect.objectContaining)
      );
      expect(comp.workRequestsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ElementTypes query and add missing value', () => {
      const estimateDesign: IEstimateDesign = { id: 456 };
      const elementtypes: IElementTypes = { id: 99518 };
      estimateDesign.elementtypes = elementtypes;

      const elementTypesCollection: IElementTypes[] = [{ id: 16920 }];
      jest.spyOn(elementTypesService, 'query').mockReturnValue(of(new HttpResponse({ body: elementTypesCollection })));
      const additionalElementTypes = [elementtypes];
      const expectedCollection: IElementTypes[] = [...additionalElementTypes, ...elementTypesCollection];
      jest.spyOn(elementTypesService, 'addElementTypesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ estimateDesign });
      comp.ngOnInit();

      expect(elementTypesService.query).toHaveBeenCalled();
      expect(elementTypesService.addElementTypesToCollectionIfMissing).toHaveBeenCalledWith(
        elementTypesCollection,
        ...additionalElementTypes.map(expect.objectContaining)
      );
      expect(comp.elementTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const estimateDesign: IEstimateDesign = { id: 456 };
      const workrequest: IWorkRequest = { id: 70123 };
      estimateDesign.workrequest = workrequest;
      const elementtypes: IElementTypes = { id: 22190 };
      estimateDesign.elementtypes = elementtypes;

      activatedRoute.data = of({ estimateDesign });
      comp.ngOnInit();

      expect(comp.workRequestsSharedCollection).toContain(workrequest);
      expect(comp.elementTypesSharedCollection).toContain(elementtypes);
      expect(comp.estimateDesign).toEqual(estimateDesign);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEstimateDesign>>();
      const estimateDesign = { id: 123 };
      jest.spyOn(estimateDesignFormService, 'getEstimateDesign').mockReturnValue(estimateDesign);
      jest.spyOn(estimateDesignService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estimateDesign });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: estimateDesign }));
      saveSubject.complete();

      // THEN
      expect(estimateDesignFormService.getEstimateDesign).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(estimateDesignService.update).toHaveBeenCalledWith(expect.objectContaining(estimateDesign));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEstimateDesign>>();
      const estimateDesign = { id: 123 };
      jest.spyOn(estimateDesignFormService, 'getEstimateDesign').mockReturnValue({ id: null });
      jest.spyOn(estimateDesignService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estimateDesign: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: estimateDesign }));
      saveSubject.complete();

      // THEN
      expect(estimateDesignFormService.getEstimateDesign).toHaveBeenCalled();
      expect(estimateDesignService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEstimateDesign>>();
      const estimateDesign = { id: 123 };
      jest.spyOn(estimateDesignService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estimateDesign });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(estimateDesignService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareWorkRequest', () => {
      it('Should forward to workRequestService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(workRequestService, 'compareWorkRequest');
        comp.compareWorkRequest(entity, entity2);
        expect(workRequestService.compareWorkRequest).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareElementTypes', () => {
      it('Should forward to elementTypesService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(elementTypesService, 'compareElementTypes');
        comp.compareElementTypes(entity, entity2);
        expect(elementTypesService.compareElementTypes).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
