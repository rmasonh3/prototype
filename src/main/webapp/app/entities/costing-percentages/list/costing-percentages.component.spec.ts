import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CostingPercentagesService } from '../service/costing-percentages.service';

import { CostingPercentagesComponent } from './costing-percentages.component';

describe('CostingPercentages Management Component', () => {
  let comp: CostingPercentagesComponent;
  let fixture: ComponentFixture<CostingPercentagesComponent>;
  let service: CostingPercentagesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'costing-percentages', component: CostingPercentagesComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [CostingPercentagesComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(CostingPercentagesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CostingPercentagesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CostingPercentagesService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.costingPercentages?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to costingPercentagesService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCostingPercentagesIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCostingPercentagesIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
