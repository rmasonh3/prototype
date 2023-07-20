import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { WorkInfoFormService } from './work-info-form.service';
import { WorkInfoService } from '../service/work-info.service';
import { IWorkInfo } from '../work-info.model';
import { IWorkRequest } from 'app/entities/work-request/work-request.model';
import { WorkRequestService } from 'app/entities/work-request/service/work-request.service';

import { WorkInfoUpdateComponent } from './work-info-update.component';

describe('WorkInfo Management Update Component', () => {
  let comp: WorkInfoUpdateComponent;
  let fixture: ComponentFixture<WorkInfoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let workInfoFormService: WorkInfoFormService;
  let workInfoService: WorkInfoService;
  let workRequestService: WorkRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [WorkInfoUpdateComponent],
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
      .overrideTemplate(WorkInfoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WorkInfoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    workInfoFormService = TestBed.inject(WorkInfoFormService);
    workInfoService = TestBed.inject(WorkInfoService);
    workRequestService = TestBed.inject(WorkRequestService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call WorkRequest query and add missing value', () => {
      const workInfo: IWorkInfo = { id: 456 };
      const workrequest: IWorkRequest = { id: 227 };
      workInfo.workrequest = workrequest;

      const workRequestCollection: IWorkRequest[] = [{ id: 95055 }];
      jest.spyOn(workRequestService, 'query').mockReturnValue(of(new HttpResponse({ body: workRequestCollection })));
      const additionalWorkRequests = [workrequest];
      const expectedCollection: IWorkRequest[] = [...additionalWorkRequests, ...workRequestCollection];
      jest.spyOn(workRequestService, 'addWorkRequestToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ workInfo });
      comp.ngOnInit();

      expect(workRequestService.query).toHaveBeenCalled();
      expect(workRequestService.addWorkRequestToCollectionIfMissing).toHaveBeenCalledWith(
        workRequestCollection,
        ...additionalWorkRequests.map(expect.objectContaining)
      );
      expect(comp.workRequestsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const workInfo: IWorkInfo = { id: 456 };
      const workrequest: IWorkRequest = { id: 96118 };
      workInfo.workrequest = workrequest;

      activatedRoute.data = of({ workInfo });
      comp.ngOnInit();

      expect(comp.workRequestsSharedCollection).toContain(workrequest);
      expect(comp.workInfo).toEqual(workInfo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkInfo>>();
      const workInfo = { id: 123 };
      jest.spyOn(workInfoFormService, 'getWorkInfo').mockReturnValue(workInfo);
      jest.spyOn(workInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: workInfo }));
      saveSubject.complete();

      // THEN
      expect(workInfoFormService.getWorkInfo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(workInfoService.update).toHaveBeenCalledWith(expect.objectContaining(workInfo));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkInfo>>();
      const workInfo = { id: 123 };
      jest.spyOn(workInfoFormService, 'getWorkInfo').mockReturnValue({ id: null });
      jest.spyOn(workInfoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workInfo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: workInfo }));
      saveSubject.complete();

      // THEN
      expect(workInfoFormService.getWorkInfo).toHaveBeenCalled();
      expect(workInfoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkInfo>>();
      const workInfo = { id: 123 };
      jest.spyOn(workInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(workInfoService.update).toHaveBeenCalled();
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
