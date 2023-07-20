import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EstimateDesignDetailComponent } from './estimate-design-detail.component';

describe('EstimateDesign Management Detail Component', () => {
  let comp: EstimateDesignDetailComponent;
  let fixture: ComponentFixture<EstimateDesignDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EstimateDesignDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ estimateDesign: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EstimateDesignDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EstimateDesignDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load estimateDesign on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.estimateDesign).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
