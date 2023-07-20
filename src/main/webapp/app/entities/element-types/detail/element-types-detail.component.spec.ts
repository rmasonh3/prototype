import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ElementTypesDetailComponent } from './element-types-detail.component';

describe('ElementTypes Management Detail Component', () => {
  let comp: ElementTypesDetailComponent;
  let fixture: ComponentFixture<ElementTypesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ElementTypesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ elementTypes: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ElementTypesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ElementTypesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load elementTypes on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.elementTypes).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
