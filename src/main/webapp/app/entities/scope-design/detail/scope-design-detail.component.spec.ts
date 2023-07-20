import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ScopeDesignDetailComponent } from './scope-design-detail.component';

describe('ScopeDesign Management Detail Component', () => {
  let comp: ScopeDesignDetailComponent;
  let fixture: ComponentFixture<ScopeDesignDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ScopeDesignDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ scopeDesign: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ScopeDesignDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ScopeDesignDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load scopeDesign on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.scopeDesign).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
