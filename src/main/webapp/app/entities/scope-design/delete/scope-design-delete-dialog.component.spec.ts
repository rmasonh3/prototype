jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ScopeDesignService } from '../service/scope-design.service';

import { ScopeDesignDeleteDialogComponent } from './scope-design-delete-dialog.component';

describe('ScopeDesign Management Delete Component', () => {
  let comp: ScopeDesignDeleteDialogComponent;
  let fixture: ComponentFixture<ScopeDesignDeleteDialogComponent>;
  let service: ScopeDesignService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ScopeDesignDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(ScopeDesignDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ScopeDesignDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ScopeDesignService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
