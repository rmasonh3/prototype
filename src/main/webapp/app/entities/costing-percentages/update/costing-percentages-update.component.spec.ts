import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CostingPercentagesFormService } from './costing-percentages-form.service';
import { CostingPercentagesService } from '../service/costing-percentages.service';
import { ICostingPercentages } from '../costing-percentages.model';

import { CostingPercentagesUpdateComponent } from './costing-percentages-update.component';

describe('CostingPercentages Management Update Component', () => {
  let comp: CostingPercentagesUpdateComponent;
  let fixture: ComponentFixture<CostingPercentagesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let costingPercentagesFormService: CostingPercentagesFormService;
  let costingPercentagesService: CostingPercentagesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CostingPercentagesUpdateComponent],
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
      .overrideTemplate(CostingPercentagesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CostingPercentagesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    costingPercentagesFormService = TestBed.inject(CostingPercentagesFormService);
    costingPercentagesService = TestBed.inject(CostingPercentagesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const costingPercentages: ICostingPercentages = { id: 456 };

      activatedRoute.data = of({ costingPercentages });
      comp.ngOnInit();

      expect(comp.costingPercentages).toEqual(costingPercentages);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICostingPercentages>>();
      const costingPercentages = { id: 123 };
      jest.spyOn(costingPercentagesFormService, 'getCostingPercentages').mockReturnValue(costingPercentages);
      jest.spyOn(costingPercentagesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ costingPercentages });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: costingPercentages }));
      saveSubject.complete();

      // THEN
      expect(costingPercentagesFormService.getCostingPercentages).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(costingPercentagesService.update).toHaveBeenCalledWith(expect.objectContaining(costingPercentages));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICostingPercentages>>();
      const costingPercentages = { id: 123 };
      jest.spyOn(costingPercentagesFormService, 'getCostingPercentages').mockReturnValue({ id: null });
      jest.spyOn(costingPercentagesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ costingPercentages: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: costingPercentages }));
      saveSubject.complete();

      // THEN
      expect(costingPercentagesFormService.getCostingPercentages).toHaveBeenCalled();
      expect(costingPercentagesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICostingPercentages>>();
      const costingPercentages = { id: 123 };
      jest.spyOn(costingPercentagesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ costingPercentages });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(costingPercentagesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
