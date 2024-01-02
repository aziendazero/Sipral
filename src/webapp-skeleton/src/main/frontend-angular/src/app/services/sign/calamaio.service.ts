import { Inject, Injectable } from '@angular/core';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { ViewManager } from '../view-manager';
import { HttpService } from '../http.service';

/**
 * Sign with PkNet
 * Uses /resource/rest/javawebstart/pknet to download jnlp wich launches Java Pknet
 * ShowDocRepositoryServlet for downloading pdf
 * PkNetSigner servlet to upload signed document
 */
@Injectable()
export class CalamaioService {

  @select(['process', 'cid']) cid$;
  cid: any;

  @select(['config', 'param']) param$;
  Param: Map<string, any>;

  apiUrl;
  apiUrlBase;
  signUrlBase;

  host;
  port;
  protocol;
  baseUrl;
  serviceHost;
  statusUrl;
  serverDownload;
  signUrl;
  jnlpUrl;
  timerStopped;
  docUrl;
  postUrl;

  documentSigned : Boolean;


  cancelled = false;

  private checkTimer = null;

  constructor(@Inject('apiUrl') url,
    private vm: ViewManager,
    private translateService: TranslateService,
    private httpService: HttpService,
  ) {
    this.cid$.subscribe((res) => this.cid = res);
    this.param$.subscribe(res => this.Param = res);

    this.apiUrl = url;

    // host calamaio
    if (this.Param['p.general.sign.calamaio.host'] && this.Param['p.general.sign.calamaio.host'].value) {
      this.host = this.Param['p.general.sign.calamaio.host'].value;
    } else {
      this.host = '172.16.22.135';
    }
    // port calamaio
    if (this.Param['p.general.sign.calamaio.port'] && this.Param['p.general.sign.calamaio.port'].value) {
      this.port = this.Param['p.general.sign.calamaio.port'].value;
    } else {
      this.port = '8980';
    }
    // protocol calamaio
    if (this.Param['p.general.sign.calamaio.protocol'] && this.Param['p.general.sign.calamaio.protocol'].value) {
      this.protocol = this.Param['p.general.sign.calamaio.protocol'].value;
    } else {
      this.protocol = 'http';
    }
    this.baseUrl = this.protocol + '://' + this.host + ':' + this.port;
    this.serviceHost = 'http://localhost:10200'; // url server locale calamaio
    this.jnlpUrl = this.baseUrl + '/calamaioresources/calamaio.jnlp';
    this.statusUrl = this.serviceHost + '/status';
    this.signUrl = this.serviceHost + '/sign';
  }

  
  public calamaioSign(docRepoId, inTemporary, sessionId, appOwner, reportType, userName, alreadySigned = false, appId) {

    this.documentSigned = alreadySigned;

    this.docUrl = this.apiUrl + 'ShowDocRepositoryServlet;JSESSIONID=' + sessionId + '?cid=' + this.cid;// + '&filename=' + encodeURIComponent(docRepoFileName);

    this.postUrl = this.apiUrl + 'PkNetSigner;JSESSIONID=' + sessionId + '?cid=' + this.cid + '&docid=' + docRepoId;

    if (inTemporary) {
      this.docUrl = this.docUrl + '&inTemporary=true';
    } else {
      this.docUrl = this.docUrl + '&docid=' + docRepoId;
    }

    this.docUrl = this.docUrl + '&asBase64=true';

    if (this.Param['p.ie.repository.use'] && this.Param['p.ie.repository.use'].value) {
      this.docUrl = this.docUrl + '&useIeRepos=true';
      this.postUrl = this.postUrl + '&useIeRepos=true';
    }

    if (appOwner) {
      this.postUrl = this.postUrl + '&appOwner=' + appOwner;
    }

    if (reportType) {
      this.postUrl = this.postUrl + '&reportType=' + reportType;
    }

    if (userName) {
      this.postUrl = this.postUrl + '&userName=' + userName;
    }

    if (appId) {
      this.postUrl = this.postUrl + '&appId=' + appId;
    }

    this.postUrl = this.postUrl + '&isBase64=true';

    this.serverDownload = true;
    this.timerStopped = false;
    this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('calamaio-server-check'), true, false,
    null, 'ANNULLA', true, null, this.stopTimer);

    console.log('Inizio verifica server di firma');
    this.serverCheck();    
    this.checkTimer = setInterval(this.serverCheck, 5000);

  }

  serverCheck = () => {
    this.httpService.fetch(this.statusUrl,
      {
        method: 'GET',
        //headers: {'Content-Type': 'application/json'},
        credentials: 'include'
      })
      .then(response => {
        return this.checkResponse(response);
      })
      .catch(() => {
        return this.performDownload();
      });
    }

  checkResponse = (response) => {
    if (response.ok) {
      console.log('Server di firma avviato');
      //this.vm.openAlertMessage(this.translateService.instant('sign'), 
      //this.translateService.instant('calamaio-server-ok'), true, true, 'OK', 'ANNULLA', true, this.performSign);
      this.stopTimer();
      this.performSign();
    } else {
      this.vm.openAlertMessage(this.translateService.instant('sign'),
        this.translateService.instant('calamaio-server-ko'), false, true);
        this.stopTimer();
    }
    return;
  }

  performDownload = () => {
    if (!this.timerStopped && this.serverDownload) {
      console.log('Download file richiesto');
      this.serverDownload = false;
      window.open(this.jnlpUrl, '_self');
      this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('calamaio-server-download'), true, false,
        null, 'ANNULLA', true, null, this.stopTimer);
    }
    //this.removeLoader();
    return;
  }

  /*
  removeLoader = () => {
    let loader = document.getElementById('ng-loader');
    if (loader) {
      console.log('loader rimosso');
      loader.style.display = 'none';
    }
  }
  */

  stopTimer = () => {
    this.timerStopped = true;
    clearInterval(this.checkTimer);
    console.log('Timer fermato');
  }

  performSign = () => {

    this.httpService.fetchNoLoading(this.docUrl,
      {
        credentials: 'include',
        method: 'GET'
      }).then(result => result.text())
      .then(base64In => {
        this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('calamaio-server-wait'), false, false);
        const xml = '<?xml version="1.0" encoding="UTF-8"?>\
        <appletRequest xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="xmlSignRequest.xsd">\
          <userAuthentication>\
          <userCode>test</userCode>\
          </userAuthentication>\
          <configurationIdentifier>' + (this.documentSigned ? 'pknet.pades.nooverlay' : 'pknet.pades') + '</configurationIdentifier>\
          <documents>\
          <document>\
            <documentIdentifier>1</documentIdentifier>\
            <documentData>' + base64In + '</documentData>\
          </document>\
          </documents>\
        </appletRequest>';
        //'<?xml version="1.0" encoding="UTF-8"?><appletRequest xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="xmlSignRequest.xsd">  <userAuthentication>	<userCode>test</userCode>  </userAuthentication>  <configurationIdentifier>pknet.pades</configurationIdentifier>  <documents>	<document>	  <documentIdentifier>1</documentIdentifier>	  <documentData>' + b64 + '</documentData> </document> </documents> </appletRequest>';
        //this.removeLoader();
        //const xmlOut = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><appletResponse><signResult>Success</signResult><result>OK</result><documentData>'+base64In+'</documentData></appletResponse>';
        this.httpService.fetchNoLoading(this.signUrl,
          {
            headers: { 'Content-Type': 'text/plain' },
            credentials: 'include',
            method: 'POST',
            body: xml
          })
          .then(result => result.text())
          .then(xmlOut => {
            // SI TRASFORMA L'XML TESTO DI RISPOSTA IN UN DOCUMENTO XML
            return new DOMParser().parseFromString(xmlOut, 'text/xml');
          })
          .then(xmlDoc => {
            //this.removeLoader();
            if (xmlDoc.getElementsByTagName('signResult')[0].childNodes[0].nodeValue === 'Success' && xmlDoc.getElementsByTagName('result')[0].childNodes[0].nodeValue === 'OK') {
              let base64Out = xmlDoc.getElementsByTagName('documentData')[0].childNodes[0].nodeValue;
              
              // SALVATAGGIO DOC FIRMATO SU PHI/IEREPOS
              this.httpService.fetchNoLoading(this.postUrl,
                {
                  headers: { 'Content-Type': 'text/plain;charset=UTF-8' },
                  credentials: 'include',
                  method: 'POST',
                  body: base64Out
                }).then(() => {
                  this.vm.openAlertMessage(this.translateService.instant('sign'),
                  this.translateService.instant('calamaio-sign-ok'), false, true);
                }).catch(error => {throw error});
            } else {
              this.vm.openAlertMessage(this.translateService.instant('sign'),
              this.translateService.instant('calamaio-sign-error') + xmlDoc.getElementsByTagName('description')[0].childNodes[0].nodeValue, false, true);
            }
          })
          .catch(error => {
            alert('errore');
          });
        }
      ).catch(

      );

    }
}
