import { TestBed, ComponentFixture  } from '@angular/core/testing';
import { APP_BASE_HREF } from '@angular/common';
import { provideRoutes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

// import { ApiService } from './services';
import { AppComponent } from './app.component';


import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
// import { AppModule } from './app.module';
// import { routing } from './app-routing';
import { LoginModule } from './views/login/login.module';
import { HomeModule } from './views/home/home.module';
import { AppRoutingModule } from './app-routing.module';
// import { HomeComponent } from './views/home/home.component';

describe('App', () => { // describe ragruppa i test

  let fixture: ComponentFixture<AppComponent>;
  let component: any;

  // provide our implementations or mocks to the dependency injector
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
        //   .withRoutes([
        //   { path: '', component: HomeComponent }
        // ])
        ,
        BrowserModule,
        FormsModule,
        // add Mock backed, see: C:\Lavoro\IdeaWorkspace\corsAngularIntermediate\angular-unit-test-examples\src\app\services\user.service.spec.ts

        LoginModule,
        HomeModule,
        AppRoutingModule,
      ],
      declarations: [AppComponent],
      providers: [
        {provide: APP_BASE_HREF, useValue : '/' },
        provideRoutes([])
      ]
    });

    fixture = TestBed.createComponent(AppComponent); // fixture: componente che mi da accesso al componente
    component = fixture.debugElement.componentInstance;

  });

  // it('should create the app', () => { // contiene una o piu expectations
  //
  //   // fixture.detectChanges();
  //   // expect(component.url).toEqual('https://github.com/preboot/angular2-webpack');
  //   expect(component.url).toBeTruthy();
  // });

  it('should have as title "Phi"', () => {
    fixture.detectChanges(); // triggero change detection
    expect(component.title).toEqual('Phi');
  });

  // it('should have as title "Phi"', () => {
  //   let fixture = TestBed.createComponent(AppComponent); // fixture: componente che mi da accesso al componente
  //   const component = fixture.debugElement.componentInstance;
  //   fixture.detectChanges(); // esegue un giro
  //   expect(component.title).toEqual('Phi');
  // });

});
