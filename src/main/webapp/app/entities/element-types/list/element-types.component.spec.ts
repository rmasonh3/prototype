import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ElementTypesService } from '../service/element-types.service';

import { ElementTypesComponent } from './element-types.component';

describe('ElementTypes Management Component', () => {
  let comp: ElementTypesComponent;
  let fixture: ComponentFixture<ElementTypesComponent>;
  let service: ElementTypesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'element-types', component: ElementTypesComponent }]), HttpClientTestingModule],
      declarations: [ElementTypesComponent],
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
      .overrideTemplate(ElementTypesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ElementTypesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ElementTypesService);

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
    expect(comp.elementTypes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to elementTypesService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getElementTypesIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getElementTypesIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
