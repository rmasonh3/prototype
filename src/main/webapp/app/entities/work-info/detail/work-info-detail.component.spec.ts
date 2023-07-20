import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WorkInfoDetailComponent } from './work-info-detail.component';

describe('WorkInfo Management Detail Component', () => {
  let comp: WorkInfoDetailComponent;
  let fixture: ComponentFixture<WorkInfoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WorkInfoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ workInfo: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(WorkInfoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(WorkInfoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load workInfo on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.workInfo).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
