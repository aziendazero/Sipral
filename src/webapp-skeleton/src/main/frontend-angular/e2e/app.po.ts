import { browser, by, element, ElementFinder, protractor } from 'protractor';

export class Phi {

  ec = protractor.ExpectedConditions;
  timeout = 500000;

  navigateTo() {
    return browser.get('/PHI_CI');
  }

  getCurrentUrl() {
    browser.getCurrentUrl();
  }

  getLogin() {
    return element(by.id('LytLoginBtn')).getText();
  }


  getOptions() {
    return element(by.id('BtnOptions')).getText();
  }


  login(username, password) {
    // browser.waitForAngular();
    element(by.css('#TxtLoginUsername input')).sendKeys(username);
    element(by.css('#TxtLoginPassword input')).sendKeys(password);

    element(by.css('#LytLoginBtn button')).click();

    browser.wait(this.ec.elementToBeClickable( element(by.id('BtnOptions'))), this.timeout);
  }

  logout() {
    element(by.id('logout')).click();
    browser.wait(this.ec.elementToBeClickable( element(by.id('LytLoginBtn')) ), this.timeout);
  }


  setOptions() {
    element(by.css('#BtnOptions button')).click();

    browser.wait(this.ec.elementToBeClickable( element(by.id('app'))), this.timeout);
  }

  isAdtDashboard() { // #DgAdt
    browser.wait(this.ec.elementToBeClickable(element(by.css('#LayoutAdtResults'))), this.timeout);
    return element(by.css('#LayoutAdtResults')).isPresent();
  }

  selectPatientEncounter() {
    this.waitFor('#LayoutAdtResults td').then(() => {
      element.all(by.css('#LayoutAdtResults td')).get(1).click();
      browser.wait(this.ec.elementToBeClickable( element(by.css('#LytPtnt'))), this.timeout); // wait for Banner
    })
  }

  openMenu() {
    this.getMenuBtn().click();
    browser.wait(this.ec.elementToBeClickable( element(by.css('phi-menu-item > a'))), this.timeout);
  }

  getBanner() {
    return element(by.css('#LytPtnt')).isPresent()
  }

  getMenuBtn(): ElementFinder {
    return element(by.css('.hamburger a'));
  }

  isMenuVisible() {
    return element(by.css('.menu.visible')).isPresent();
  }

  waitFor(selector) {
    return browser.wait(function () {
      return browser.isElementPresent(element(by.css(selector)));
    }, this.timeout);
  }
}
