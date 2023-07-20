import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CostingPercentagesDetailComponent } from './costing-percentages-detail.component';

describe('CostingPercentages Management Detail Component', () => {
  let comp: CostingPercentagesDetailComponent;
  let fixture: ComponentFixture<CostingPercentagesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CostingPercentagesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ costingPercentages: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CostingPercentagesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CostingPercentagesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load costingPercentages on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.costingPercentages).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
