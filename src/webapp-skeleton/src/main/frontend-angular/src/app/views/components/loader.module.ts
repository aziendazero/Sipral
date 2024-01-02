import { NgModule } from '@angular/core';
import { LoaderComponent } from './loader.component';
import { HttpService } from '../../services/http.service';
import { CommonModule }  from '@angular/common';

@NgModule({
  declarations: [ LoaderComponent ],
  exports: [ LoaderComponent ],
  imports: [
    CommonModule
  ],
  providers: [ HttpService ]
})
export class LoaderModule {}

