import { browser, by, element, ElementFinder, protractor } from 'protractor';

export class EmployeePageObject {

  ec = protractor.ExpectedConditions;
  timeout = 500000;


  openEmployeeMenu() {
    this.getEmployeeFolderBtn().click();
  }

  navigateToEmployeeManagement() {

    this.getEmployeeManagementProcessBtn().click();

    return browser.wait(this.ec.elementToBeClickable( this.getAddEmployeeBtn() ), this.timeout);
  }

  fillEmployeeForm() {

    this.getAddEmployeeBtn().click();

    // console.log('AddEmployeeBtn');

    browser.wait(this.ec.elementToBeClickable( this.getCreateEmployeeBtn() ), this.timeout);

    element(by.id('i:TextBox_1346056349646_id')).sendKeys('username');
    element(by.id('i:TextBox_1346056354861_id')).sendKeys('password');
    element(by.id('i:MonthCalendar_1445512542741_id')).sendKeys('04/01/2018');
    element(by.id('i:TextBox_1338865218853_id')).sendKeys('surname');
    element(by.id('i:TextBox_1337977803537_id')).sendKeys('name');
    // element(by.id('i:ComboBox_1487776396728_id')).sendKeys('CodeValuePhi-2.16.840.1.113883.3.20.100.02.20'); // Italiano
    element(by.cssContainingText('#i\\:ComboBox_1487776396728_id option', 'italiano')).click();
    // element.all(by.css('#i\\:ComboBox_1487776396728_id option')).first().click();

    this.getAddRoleBtn().click();

    console.log('AddRoleBtn');

    browser.wait(this.ec.elementToBeClickable( this.getCreateRoleBtn() ), this.timeout);

    // element(by.id('i:ComboBox_1341298174695')).sendKeys('CodeValueRole-2.16.840.1.113883.3.20.11.1.1'); // admin
    element.all(by.css('#i\\:ComboBox_1341298174695 option')).first().click();
    element(by.id('i:Interval_1337085981567_A')).sendKeys('04/01/2018');

    this.getCreateRoleBtn().click();

    browser.wait(this.ec.elementToBeClickable( this.getCreateEmployeeBtn() ), this.timeout);

    this.getCreateEmployeeBtn().click();

    this.getHomeBtn().click();

    browser.wait(this.ec.elementToBeClickable( element(by.css('phi-menu-item > a'))), this.timeout); // wait for menu, home
  }


  getEmployeeFolderBtn(): ElementFinder {
    return element(by.cssContainingText('a span', 'Personale'));
  }

  getEmployeeManagementProcessBtn(): ElementFinder {
    return element(by.css('a[href*="#/process/MOD_Employees%2FCORE%2FPROCESSES%2FManageMulti"]'));
  }

  getAddEmployeeBtn(): ElementFinder {
    return element(by.id('i:Button_1382431033812_id'));
  }

  getCreateEmployeeBtn(): ElementFinder {
    return element(by.id('i:Button_1337629976451_id'));
  }

  getAddRoleBtn(): ElementFinder {
    return element(by.id('i:Button_1382434576368_id'));
  }

  getCreateRoleBtn(): ElementFinder {
    return element(by.id('i:Button_1337086014209_id'));
  }

  getHomeBtn(): ElementFinder {
    return element(by.id('i:ButtonHome_1381487046983_id'));
  }


}
