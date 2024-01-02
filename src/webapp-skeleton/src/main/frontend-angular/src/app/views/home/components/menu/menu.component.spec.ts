import { TestBed, ComponentFixture } from '@angular/core/testing';

import { MenuComponent } from './menu.component';
import { MenuItemComponent } from './menuItem.component';
import { RouterTestingModule } from '@angular/router/testing';

let fixture: ComponentFixture<MenuComponent>;
let component: any;

const mockProcessListResponse = {
  "sid":"207468CA96D98B78F9987EFBBAAC78C4",
  "sdLocs":[
  {
    "internalId":37,
    "version":421,
    "creationDate":"2013-08-12T15:51:52.233+02:00",
    "isActive":true,
    "parent":{
      "entityName":"com.phi.entities.role.ServiceDeliveryLocation",
      "internalId":127618
    },
    "code":{
      "entityName":"com.phi.entities.dataTypes.CodeValuePhi",
      "id":"2.16.840.1.113883.3.20.1.1.111_V0",
      "code":"WARD",
      "displayName":"WARD",
      "langIt":"WARD",
      "currentTranslation":"WARD",
    },
    "name":{
      "giv":"Chirurgia Brunico - degenza",
    },
    "telecom":{
      "dir":"0474581500",
    },
    "vitalSignForm":{
      "entityName":"com.phi.entities.dataTypes.CodeValuePhi",
      "id":"2.16.840.1.113883.3.20.9.16.01_V0"
    }
  }
  ],
  "currentEmployee":{
    "entityName":"com.phi.entities.role.Employee",
    "internalId":129465
  },
  "param": {
    "p.general.modchecking.form.identification.layoutvertical": {
      "readOnly": false,
      "visible": false,
      "value": false,
      "required": false
    }
  },
  "processList":[
    {
      "title":"Accertamento",
      "children":[
        {
          "title":"Allergia",
          "children":[

          ],
          "path":"MOD_Checking/CORE/PROCESSES/AllergyManagement",
          "imagePath":null,
          "processPath":[
            "MOD_Checking",
            "MOD_Checking/CORE/PROCESSES/AllergyManagement"
          ],
          "sortOrder":null,
          "readOnly":false,
          "leaf":true,
          "always_executable":false
        },
        {
          "title":"Informazioni di base",
          "children":[

          ],
          "path":"MOD_Checking/CORE/PROCESSES/Identification",
          "imagePath":null,
          "processPath":[
            "MOD_Checking",
            "MOD_Checking/CORE/PROCESSES/Identification"
          ],
          "sortOrder":1,
          "readOnly":false,
          "leaf":true,
          "always_executable":false
        },
        {
          "title":"Accertamento infermieristico",
          "children":[

          ],
          "path":"MOD_Checking/customer_VCO/PROCESSES/Checking",
          "imagePath":null,
          "processPath":[
            "MOD_Checking",
            "MOD_Checking/customer_VCO/PROCESSES/Checking"
          ],
          "sortOrder":2,
          "readOnly":false,
          "leaf":true,
          "always_executable":false
        }
      ],
      "path":"MOD_Checking",
      "imagePath":null,
      "processPath":[
        "MOD_Checking"
      ],
      "sortOrder":null,
      "readOnly":false,
      "leaf":false,
      "always_executable":false
    }
  ],
  "roleCode":"1",
  "cid":"2511"
};

describe('Menu Component', () => {
  beforeEach(() => {

    const apiServiceStub = {
      getProcesses: () => Promise.resolve(mockProcessListResponse)
    };

    TestBed.configureTestingModule({
      imports: [ RouterTestingModule ],
      declarations: [ MenuComponent, MenuItemComponent ],
      providers: [
        { useValue: apiServiceStub },
      ]
    });

    fixture = TestBed.createComponent(MenuComponent); // fixture: componente che mi da accesso al componente
    component = fixture.debugElement.componentInstance;

  });

  it('should contain Home link', () => {
    fixture.detectChanges();
    expect(fixture.nativeElement.children[0].textContent).toContain('Home');
  });

  // A better way to test toggle feature
  it('should emit navigate to "/" when clickcked the first link ', () => {
    // Track toggle function
    // spyOn(component.toggle, 'emit');
    // Select title bar
    const homeLink = fixture.nativeElement.querySelector("ul > li > a");
    // Trigger click
    //homeLink.dispatchEvent(new Event('click'));
    homeLink.click();
    // Check if emitter has been call
    // expect(context.component.toggle.emit).toHaveBeenCalled();
    fixture.detectChanges();
    // fixture.whenStable().then(() => {
      //expect(location.path()).toEqual('/');
      console.log('after expect');
    // });
  });

});
