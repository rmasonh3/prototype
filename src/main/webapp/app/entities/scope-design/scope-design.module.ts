import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ScopeDesignComponent } from './list/scope-design.component';
import { ScopeDesignDetailComponent } from './detail/scope-design-detail.component';
import { ScopeDesignUpdateComponent } from './update/scope-design-update.component';
import { ScopeDesignDeleteDialogComponent } from './delete/scope-design-delete-dialog.component';
import { ScopeDesignRoutingModule } from './route/scope-design-routing.module';

@NgModule({
  imports: [SharedModule, ScopeDesignRoutingModule],
  declarations: [ScopeDesignComponent, ScopeDesignDetailComponent, ScopeDesignUpdateComponent, ScopeDesignDeleteDialogComponent],
})
export class ScopeDesignModule {}
