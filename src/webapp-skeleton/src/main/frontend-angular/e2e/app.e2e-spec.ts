import { Phi } from './app.po';
import { PatientPageObject } from './patient.po';

describe('Phi', () => {
  let page: Phi;
  let patient: PatientPageObject;

  let users = [
    {"username": "maxtest", "password": "12345678"},
    // {"username": "alex", "password": "alexalex"}
  ];

  beforeEach(() => {
    page = new Phi();
    patient = new PatientPageObject();
  });

  users.map((user) => {

    console.log('Using ' + user.username);

    it('should contain login btn', () => {
      page.navigateTo();
      expect(page.getLogin()).toEqual('LOGIN');
    });

    it('should login', function() {
      page.login(user.username, user.password);
      expect(page.getOptions()).toEqual('LOGIN');
    });

    it('should set options', function() {
      page.setOptions();
      expect(page.getOptions()).toEqual('LOGIN');
    });

    it('should be adt dashboard', function () {
      expect(page.isAdtDashboard()).toEqual(true);
    });

    it('should inject first patient encounter', function() {
      page.selectPatientEncounter();
      expect(page.getBanner()).toEqual(true);
    });

    it('should open menu', function() {
      page.openMenu();
      expect(page.isMenuVisible()).toEqual(true);
    });

    it('should open patient menu folder', function() {
      patient.openPatientMenu();
    });

    it('should start create patient process', function() {
      patient.navigateToCreatePatient();
      expect(patient.getCreatePatientBtn().getText()).toEqual('');
    });

    it('should fill patient data', function() {
      patient.fillPatientForm();
      expect(patient.getCreatePatientBtn().getText()).toEqual('');
    });

    it('should logout', function() {
      page.logout();
      expect(page.getLogin()).toEqual('LOGIN');
    });

  });

});
