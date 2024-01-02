import { browser, by, element, ElementFinder, protractor } from 'protractor';

export class PatientPageObject {

  ec = protractor.ExpectedConditions;
  timeout = 500000;


  openPatientMenu() {
    this.getPatientFolderBtn().click();
    // this.getPatientFolderBtn()
  }

  navigateToCreatePatient() {

    this.getCreatePatientProcessBtn().click();

    return browser.wait(this.ec.elementToBeClickable( this.getCreatePatientBtn() ), this.timeout);
  }

  fillPatientForm() {
    element(by.id('i:TextBox_1380797755809_id')).sendKeys('surname');
    element(by.id('i:TextBox_1380350353505_id')).sendKeys('name');
    element(by.id('i:MonthCalendar_1383179809331_id')).sendKeys('04/01/2018');
    element(by.id('i:TextBox_1382087503512_id')).sendKeys('XYZXYZ80A01L424N');

    // browser.pause(12345);

    this.getCreatePatientBtn().click();

    browser.wait(this.ec.elementToBeClickable( element(by.css('phi-menu-item > a'))), this.timeout); // wait for menu, home
  }


  getPatientFolderBtn(): ElementFinder {
    // console.log(element(by.cssContainingText('a span', 'Pazienti')).getText() );
    return element(by.cssContainingText('a span', 'Pazienti'));
  }

  getCreatePatientProcessBtn(): ElementFinder {
    return element(by.css('a[href*="#/process/MOD_Patients%2FCORE%2FPROCESSES%2FCreate_Patient"]'));
  }

  getCreatePatientBtn(): ElementFinder {
    return element(by.id('i:Button_1383312181860_id'));
  }

}
