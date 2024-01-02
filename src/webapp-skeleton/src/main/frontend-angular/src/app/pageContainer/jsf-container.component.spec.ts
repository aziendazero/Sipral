import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { JsfContainerComponent } from './jsf-container.component';
import { ApiService } from '../services/api.service';

describe('JsfContainer Component', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
        //   .withRoutes([
        //   { path: '', component: HomeComponent }
        // ])
      ],
      declarations: [JsfContainerComponent],
      providers: [
        ApiService
      ]
    });
  });

  it('should ...', () => {
    const fixture = TestBed.createComponent(JsfContainerComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.children[0].textContent).toContain('About Works!');
  });

});
