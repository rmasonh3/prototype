import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ScopeDesignFormService } from './scope-design-form.service';
import { ScopeDesignService } from '../service/scope-design.service';
import { IScopeDesign } from '../scope-design.model';
import { IWorkRequest } from 'app/entities/work-request/work-request.model';
import { WorkRequestService } from 'app/entities/work-request/service/work-request.service';

import { ScopeDesignUpdateComponent } from './scope-design-update.component';

describe('ScopeDesign Management Update Component', () => {
  let comp: ScopeDesignUpdateComponent;
  let fixture: ComponentFixture<ScopeDesignUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let scopeDesignFormService: ScopeDesignFormService;
  let scopeDesignService: ScopeDesignService;
  let workRequestService: WorkRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ScopeDesignUpdateComponent],
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
      .overrideTemplate(ScopeDesignUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScopeDesignUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    scopeDesignFormService = TestBed.inject(ScopeDesignFormService);
    scopeDesignService = TestBed.inject(ScopeDesignService);
    workRequestService = TestBed.inject(WorkRequestService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call WorkRequest query and add missing value', () => {
      const scopeDesign: IScopeDesign = { id: 456 };
      const workrequest: IWorkRequest = { id: 12780 };
      scopeDesign.workrequest = workrequest;

      const workRequestCollection: IWorkRequest[] = [{ id: 42780 }];
      jest.spyOn(workRequestService, 'query').mockReturnValue(of(new HttpResponse({ body: workRequestCollection })));
      const additionalWorkRequests = [workrequest];
      const expectedCollection: IWorkRequest[] = [...additionalWorkRequests, ...workRequestCollection];
      jest.spyOn(workRequestService, 'addWorkRequestToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scopeDesign });
      comp.ngOnInit();

      expect(workRequestService.query).toHaveBeenCalled();
      expect(workRequestService.addWorkRequestToCollectionIfMissing).toHaveBeenCalledWith(
        workRequestCollection,
        ...additionalWorkRequests.map(expect.objectContaining)
      );
      expect(comp.workRequestsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const scopeDesign: IScopeDesign = { id: 456 };
      const workrequest: IWorkRequest = { id: 25511 };
      scopeDesign.workrequest = workrequest;

      activatedRoute.data = of({ scopeDesign });
      comp.ngOnInit();

      expect(comp.workRequestsSharedCollection).toContain(workrequest);
      expect(comp.scopeDesign).toEqual(scopeDesign);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IScopeDesign>>();
      const scopeDesign = { id: 123 };
      jest.spyOn(scopeDesignFormService, 'getScopeDesign').mockReturnValue(scopeDesign);
      jest.spyOn(scopeDesignService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scopeDesign });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: scopeDesign }));
      saveSubject.complete();

      // THEN
      expect(scopeDesignFormService.getScopeDesign).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(scopeDesignService.update).toHaveBeenCalledWith(expect.objectContaining(scopeDesign));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IScopeDesign>>();
      const scopeDesign = { id: 123 };
      jest.spyOn(scopeDesignFormService, 'getScopeDesign').mockReturnValue({ id: null });
      jest.spyOn(scopeDesignService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scopeDesign: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: scopeDesign }));
      saveSubject.complete();

      // THEN
      expect(scopeDesignFormService.getScopeDesign).toHaveBeenCalled();
      expect(scopeDesignService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IScopeDesign>>();
      const scopeDesign = { id: 123 };
      jest.spyOn(scopeDesignService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scopeDesign });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(scopeDesignService.update).toHaveBeenCalled();
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
  });
});
