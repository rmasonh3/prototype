import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WorkRequestDetailComponent } from './work-request-detail.component';

describe('WorkRequest Management Detail Component', () => {
  let comp: WorkRequestDetailComponent;
  let fixture: ComponentFixture<WorkRequestDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WorkRequestDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ workRequest: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(WorkRequestDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(WorkRequestDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load workRequest on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.workRequest).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
