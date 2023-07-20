import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { WorkRequestFormService } from './work-request-form.service';
import { WorkRequestService } from '../service/work-request.service';
import { IWorkRequest } from '../work-request.model';

import { WorkRequestUpdateComponent } from './work-request-update.component';

describe('WorkRequest Management Update Component', () => {
  let comp: WorkRequestUpdateComponent;
  let fixture: ComponentFixture<WorkRequestUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let workRequestFormService: WorkRequestFormService;
  let workRequestService: WorkRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [WorkRequestUpdateComponent],
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
      .overrideTemplate(WorkRequestUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WorkRequestUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    workRequestFormService = TestBed.inject(WorkRequestFormService);
    workRequestService = TestBed.inject(WorkRequestService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const workRequest: IWorkRequest = { id: 456 };

      activatedRoute.data = of({ workRequest });
      comp.ngOnInit();

      expect(comp.workRequest).toEqual(workRequest);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkRequest>>();
      const workRequest = { id: 123 };
      jest.spyOn(workRequestFormService, 'getWorkRequest').mockReturnValue(workRequest);
      jest.spyOn(workRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: workRequest }));
      saveSubject.complete();

      // THEN
      expect(workRequestFormService.getWorkRequest).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(workRequestService.update).toHaveBeenCalledWith(expect.objectContaining(workRequest));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkRequest>>();
      const workRequest = { id: 123 };
      jest.spyOn(workRequestFormService, 'getWorkRequest').mockReturnValue({ id: null });
      jest.spyOn(workRequestService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workRequest: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: workRequest }));
      saveSubject.complete();

      // THEN
      expect(workRequestFormService.getWorkRequest).toHaveBeenCalled();
      expect(workRequestService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkRequest>>();
      const workRequest = { id: 123 };
      jest.spyOn(workRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(workRequestService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
