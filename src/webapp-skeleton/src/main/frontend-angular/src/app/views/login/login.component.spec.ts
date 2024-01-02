import { TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterTestingModule } from '@angular/router/testing';

import { LoginComponent } from './login.component';


describe('Login Component', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule,
        FormsModule,
        RouterTestingModule
      ],
      declarations: [LoginComponent],
      providers: [

        {
          provide: 'apiUrl',  // <=== NOTE: OpaqueToken would be much butter
          useValue: 'http://localhost:3300'
        }
      ]
    });
  });

  it('should contain Username', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.children[0].textContent).toContain('Username');
  });

});
