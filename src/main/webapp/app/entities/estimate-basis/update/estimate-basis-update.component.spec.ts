import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EstimateBasisFormService } from './estimate-basis-form.service';
import { EstimateBasisService } from '../service/estimate-basis.service';
import { IEstimateBasis } from '../estimate-basis.model';
import { IWorkRequest } from 'app/entities/work-request/work-request.model';
import { WorkRequestService } from 'app/entities/work-request/service/work-request.service';

import { EstimateBasisUpdateComponent } from './estimate-basis-update.component';

describe('EstimateBasis Management Update Component', () => {
  let comp: EstimateBasisUpdateComponent;
  let fixture: ComponentFixture<EstimateBasisUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let estimateBasisFormService: EstimateBasisFormService;
  let estimateBasisService: EstimateBasisService;
  let workRequestService: WorkRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EstimateBasisUpdateComponent],
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
      .overrideTemplate(EstimateBasisUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EstimateBasisUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    estimateBasisFormService = TestBed.inject(EstimateBasisFormService);
    estimateBasisService = TestBed.inject(EstimateBasisService);
    workRequestService = TestBed.inject(WorkRequestService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call WorkRequest query and add missing value', () => {
      const estimateBasis: IEstimateBasis = { id: 456 };
      const workrequest: IWorkRequest = { id: 7532 };
      estimateBasis.workrequest = workrequest;

      const workRequestCollection: IWorkRequest[] = [{ id: 90338 }];
      jest.spyOn(workRequestService, 'query').mockReturnValue(of(new HttpResponse({ body: workRequestCollection })));
      const additionalWorkRequests = [workrequest];
      const expectedCollection: IWorkRequest[] = [...additionalWorkRequests, ...workRequestCollection];
      jest.spyOn(workRequestService, 'addWorkRequestToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ estimateBasis });
      comp.ngOnInit();

      expect(workRequestService.query).toHaveBeenCalled();
      expect(workRequestService.addWorkRequestToCollectionIfMissing).toHaveBeenCalledWith(
        workRequestCollection,
        ...additionalWorkRequests.map(expect.objectContaining)
      );
      expect(comp.workRequestsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const estimateBasis: IEstimateBasis = { id: 456 };
      const workrequest: IWorkRequest = { id: 67703 };
      estimateBasis.workrequest = workrequest;

      activatedRoute.data = of({ estimateBasis });
      comp.ngOnInit();

      expect(comp.workRequestsSharedCollection).toContain(workrequest);
      expect(comp.estimateBasis).toEqual(estimateBasis);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEstimateBasis>>();
      const estimateBasis = { id: 123 };
      jest.spyOn(estimateBasisFormService, 'getEstimateBasis').mockReturnValue(estimateBasis);
      jest.spyOn(estimateBasisService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estimateBasis });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: estimateBasis }));
      saveSubject.complete();

      // THEN
      expect(estimateBasisFormService.getEstimateBasis).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(estimateBasisService.update).toHaveBeenCalledWith(expect.objectContaining(estimateBasis));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEstimateBasis>>();
      const estimateBasis = { id: 123 };
      jest.spyOn(estimateBasisFormService, 'getEstimateBasis').mockReturnValue({ id: null });
      jest.spyOn(estimateBasisService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estimateBasis: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: estimateBasis }));
      saveSubject.complete();

      // THEN
      expect(estimateBasisFormService.getEstimateBasis).toHaveBeenCalled();
      expect(estimateBasisService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEstimateBasis>>();
      const estimateBasis = { id: 123 };
      jest.spyOn(estimateBasisService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estimateBasis });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(estimateBasisService.update).toHaveBeenCalled();
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
