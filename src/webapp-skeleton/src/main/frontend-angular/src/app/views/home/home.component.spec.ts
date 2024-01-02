// import { Component } from '@angular/core';

import { TestBed, ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { HomeComponent } from './home.component';

describe('Home Component', () => {
  // const html = '<my-home></my-home>';

  let fixture: ComponentFixture<HomeComponent>;
  let component: any;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
      ],
      declarations: [
        HomeComponent,
        //TestComponent
      ],
      providers: [
      ]
    });

    fixture = TestBed.createComponent(HomeComponent); // fixture: componente che mi da accesso al componente
    component = fixture.debugElement.componentInstance;

    // TestBed.overrideComponent(TestComponent, { set: { template: html }});
  });

  // it('should contain Phi', () => {
  //   // const fixture = TestBed.createComponent(TestComponent);
  //   fixture.detectChanges();
  //   expect(fixture.nativeElement.children[0].textContent).toContain('Phi');
  // });

  it('should open the menu', () => {
    // const fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();

    const menuLink = fixture.nativeElement.querySelector("header > div > a");
    // Trigger click
    menuLink.dispatchEvent(new Event('click'));
    // Check if emitter has been call
    // expect(context.component.toggle.emit).toHaveBeenCalled();

    fixture.whenStable().then(() => {
      //expect(location.path()).toEqual('/');
      console.log('after expect');
    });

    expect(fixture.nativeElement.children[0].textContent).toContain('Phi');
  });




});

// @Component({selector: 'my-test', template: ''})
// class TestComponent { }
