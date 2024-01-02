import { environment } from 'environments/environment';
import { Inject, Injectable } from '@angular/core';
import { select } from '@angular-redux/store';
import { TranslateService } from '@ngx-translate/core';
import { ViewManager } from '../view-manager';
import { logError } from '../error/global-error-handler';
import { ConversationResolve } from 'app/views/home/conversation.resolve';
import { HttpService } from '../http.service';

/**
 * Sign with PkNet
 * Uses /resource/rest/javawebstart/pknet to download jnlp wich launches Java Pknet
 * ShowDocRepositoryServlet for downloading pdf
 * PkNetSigner servlet to upload signed document
 */
@Injectable()
export class PkNetService {

  @select(['process', 'cid']) cid$;
  cid: any;

  @select(['config', 'param']) param$;
  Param: Map<string, any>;

  apiUrl;
  apiUrlBase;
  signUrlBase;

  pkNet;

  cancelled = false;

  constructor( @Inject('apiUrl') url,
    private vm: ViewManager,
    private translateService: TranslateService,
    private httpService: HttpService,
  ) {
    this.cid$.subscribe((res) => this.cid = res);
    this.param$.subscribe(res => this.Param = res);

    this.apiUrl = url;
    this.apiUrlBase = location.protocol + '//' + location.hostname+ ':' +
      (location.port && location.port !== '' ? location.port : (
        location.protocol === 'https:' ? '443' : '80'));

    if (this.apiUrl.startsWith('/')) {
      this.apiUrl = this.apiUrlBase + this.apiUrl;
    }

    if (environment.sign && environment.sign.basePath) {
      this.signUrlBase = this.apiUrlBase + environment.sign.basePath;

      if (this.Param['p.general.sign.balancers'] && this.Param['p.general.sign.balancers'].value) {
        let isBalancer = false;
        let balancers = this.Param['p.general.sign.balancers'].value;
        if (String.prototype.includes) {
          isBalancer = balancers.includes(this.apiUrlBase);
        } else {
          isBalancer = balancers.indexOf(this.apiUrlBase) !== -1;
        }
        if (!isBalancer) {
          this.signUrlBase = this.apiUrl + 'common/signature/';
        }
        return;
      } else {
        try {
          this.signUrlBase = this.checkUrl(this.signUrlBase);
        } catch(error) {
          this.signUrlBase = this.apiUrlBase + environment.sign.basePath;
        }
        return;
      }
    } else {
      this.signUrlBase = this.apiUrl + 'common/signature/';
    }

  }

  /**
   * Sign a document
   * pkNetSign('#{DocRepository.fileName}', #{DocRepository.internalId}, '#{session.id}', 'P7M')
   * @param docRepoFileName DocRepository.fileName
   * @param docRepoId DocRepository.internalId
   // * @param docUrl doc to sign url
   // * @param postUrl signed doc post
   * @param sessionId session.id
   * @param signType P7M or PDF
   * @param successCallback function called after succesful sign
   */
  public pkNetSign(docRepoFileName, docRepoId, sessionId, signType, successCallback, inTemporary, appOwner, reportType, alreadySigned, userName, appId) {

    this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('sign-download'), true, false);

    let docUrl = this.apiUrl + 'ShowDocRepositoryServlet?cid=' + this.cid + '&filename=' + encodeURIComponent(docRepoFileName);

    if (inTemporary) {
      docUrl = docUrl + '&inTemporary=true';
    } else {
      docUrl = docUrl + '&docid=' + docRepoId;
    }
    let postUrl = this.apiUrl + 'PkNetSigner?cid=' + this.cid + '&docid=' + docRepoId;
    let checkUrl = this.apiUrl + 'resource/rest/pknetutils/checkDoc/' + docRepoId;

    if (this.Param['p.ie.repository.use'] && this.Param['p.ie.repository.use'].value) {
      docUrl = docUrl + '&useIeRepos=true';
      postUrl = postUrl + '&useIeRepos=true';
    }

    if (appOwner) {
      postUrl = postUrl + '&appOwner=' + appOwner;
    }

    if (reportType) {
      postUrl = postUrl + '&reportType=' + reportType;
    }

    if (userName) {
      postUrl = postUrl + '&userName=' + userName;
    }

    if (appId) {
      postUrl = postUrl + '&appId=' + appId;
    }

    let sign = () => {

      this.pkNet = window['pkNet'];

      let PIN = '';
      let pkNetInitialized = false;

      this.cancelled = false;

      /**
       * Polling task
       */
      let setPageToOK = () => {
        this.pkNet.stopPolling();
        if (pkNetInitialized === false) {
          this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('sign-started'), true, false);
          pkNetInitialized = true;
          this.pkNet.jq.getScript(this.pkNet.getJsStubLocation(), () => {

            let com_intesi_pknet_PKNet = window['com_intesi_pknet_PKNet'];

            let credential = null;
            let signedBy = '';

            let ajaxKO = (res) => {
              console.log(res);
              this.vm.openAlertMessage(this.translateService.instant('sign'),
                this.translateService.instant('sign-error') + res.error.code + ':' + res.error.message, true, false);
              logError('Sign error: '  + res.error.code + ':' + res.error.message, sessionStorage['cid']);
              com_intesi_pknet_PKNet.Finalize(function () {
                },
                function () {
                  console.log('An error occurred during PKNet `finalize');
                });
              this.pkNet.stopPolling();
              this.pkNet.quiteShutDown();
              // unblockUI();
            };

            let onInitOK = function () {
                // 1: certificate Key Usage extension is equal to non-repudiation
                com_intesi_pknet_PKNet.setProperty(onCredTypeOK, ajaxKO, 'CredTypeFilter', '1');
              },
              onCredTypeOK = function () {
                // 1: credentials with valid certificate validity
                com_intesi_pknet_PKNet.setProperty(onValidCredOK, ajaxKO, 'ValidCredFilter', '1');
              },
              onValidCredOK = function () {
                // 1: if a single credential is selectable, PkNet automatically select it, without displaying the selection dialog
                com_intesi_pknet_PKNet.setProperty(onAutoSelCredFilterOK, ajaxKO, 'AutoSelCredFilter', '1');
              },
              onAutoSelCredFilterOK = function () {
                com_intesi_pknet_PKNet.GetCredentials(onGetCredentials, ajaxKO);
              },
              onGetCredentials = (res) => {
                this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('sign-pin'), true, false);
                let ICredentials = new window['com_intesi_pknet_ICredentials']();
                ICredentials.pknetJWSinstanceID = getResultField(res, 'instanceId');
                if (ICredentials.pknetJWSinstanceID !== undefined) {
                  ICredentials.getList(onGetList, ajaxKO, PIN);
                }
              },
              onGetList = (res) => {
                this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('sign-pin-ok'), true, false);
                credential = new window['com_intesi_pknet_Credential']();
                credential.pknetJWSinstanceID = [getResultField(res, 'instanceId'), ':0'].join('');
                let credValue = getResultField(res, 'value');
                if (credValue && credValue[0]) {
                  signedBy = credValue[0].c || credValue[0].m_CommonName;
                }
                credential.verifyExpiration(onVerify, ajaxKO, 1800);
              },
              onVerify = (res) => {
                let warningMessage = getResultField(res, 'value'),
                  Envelope = new window['com_intesi_pknet_Envelope']();
                if (warningMessage !== undefined && warningMessage !== '') {
                  this.vm.openAlertMessage(this.translateService.instant('sign'),
                  this.translateService.instant('sign-pin-ok') + ' ' + warningMessage.replace(/scadr.*fra/, 'scadrà fra'), true, false);
                }

                let signed = false;

                postUrl = postUrl + '&s=' + encodeURIComponent(signedBy);

                if (signType === 'PDF') {

                  if (alreadySigned) {
                    signed = Envelope.pdfaddsign(onSignOK, ajaxKO,
                      docUrl, // input PDF document
                      0,    // accessPermissions: No restriction
                      null, // fieldName
                      environment.sign.format, // sigLayout
                      environment.sign.reason,   // the reason PDF attribute
                      environment.sign.location, // the location PDF attribute
                      null,  // the contact PDF attribute
                      credential, // credential object
                      PIN, // [PIN], // smart card PIN
                      null, // signature date attribute - format: YYYY-MM-GG@hh:mm:ss
                      // (use null value to specify the current date)
                      null, //getImageByteArray(), // signature image // PER LA SECONDA FIRMA POTREBBE NON SERVIRE
                      -1,    // page number
                      8,    // position (1=top left)
                      0,    // x coordinate
                      0,    // y coordinate
                      postUrl); // signed PDF document
                  } else {
                    signed = Envelope.pdfsign(onSignOK, ajaxKO,
                      docUrl, // input PDF document
                      0,    // accessPermissions: No restriction
                      null, // fieldName
                      environment.sign.format, // sigLayout //'<FontSize>6</FontSize><DateFormat>dd MMMM yyyy</DateFormat><DateLang>it</DateLang><Text>%reason%\n%location% %date%</Text>',
                      environment.sign.reason,   // the reason PDF attribute
                      environment.sign.location, // the location PDF attribute
                      null,  // the contact PDF attribute
                      credential, // credential object
                      PIN, // [PIN], // smart card PIN
                      null, // signature date attribute - format: YYYY-MM-GG@hh:mm:ss
                      // (use null value to specify the current date)
                      getImageByteArray(/*signedBy, new Date()*/), // signature image
                      -1,    // page number
                      8,    // position (1=top left)
                      0,    // x coordinate
                      0,    // y coordinate
                      postUrl); // signed PDF document
                  }

                } else if (signType === 'P7M') {

                  signed = Envelope.sign(onSignOK, ajaxKO,
                    docUrl, // input document
                    credential,  // credential object
                    PIN, // smart card PIN
                    null,  // OTP
                    null, // signature date attribute
                    // format: YYYY-MM-GG@hh:mm:ss
                    // (use null value to specify
                    // the current date)
                    postUrl); // signed document
                } else {
                  logError('Sign error: Sign type unknow: ' + signType, sessionStorage['cid']);
                  throw new Error('Sign type unknow: ' + signType);
                }
                if (!signed) {
                  this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('sign-error'), true, true);
                  logError('Sign error: params: docUrl: ' + docUrl + ' postUrl: ' + postUrl, sessionStorage['cid']);
                  this.pkNet.quiteShutDown();
                }
              },
              onSignOK = () => {
                if (environment.sign && environment.sign.synchro) {
                  return this.httpService.fetch(checkUrl,
                  {
                    method: 'GET',
                    credentials: 'include'
                  })
                  .then(() => {
                    this.vm.openAlertMessage(this.translateService.instant('sign'),
                      this.translateService.instant('sign-ok') + ' ' + signedBy, false, true);
                    com_intesi_pknet_PKNet.Finalize(function () {
                      },
                      function (finalizeKO) {
                        console.log(finalizeKO);
                      });
                    if (successCallback !== undefined) {
                      successCallback();
                    }
                  });
                } else {
                  this.vm.openAlertMessage(this.translateService.instant('sign'),
                    this.translateService.instant('sign-ok') + ' ' + signedBy, false, true);
                  com_intesi_pknet_PKNet.Finalize(function () {
                    },
                    function (finalizeKO) {
                      console.log(finalizeKO);
                    });
                  if (successCallback !== undefined) {
                    successCallback();
                  }
                }
              };

            function getImageByteArray(/*signer, signDate*/) {
              let myCanvas = document.createElement('canvas');
              myCanvas.height = 60; // 60; under line
              myCanvas.width = 300;

              let ctx = myCanvas.getContext('2d');
              ctx.fillStyle = '#FEFEFE';
              //ctx.globalAlpha = 0.1;
              ctx.fillRect(0, 0, 300, 60);
              //ctx.globalAlpha = 1;
              // Create an image otherwise sign will be invisible
              /* let ctx = myCanvas.getContext('2d');
                          ctx.font = '12px serif';
                          ctx.fillText('Rappresentazione di un documento firmato elettronicamente, secondo la normativa vigente.', 10, 20);
                          ctx.fillText('Firmato da: ' + signer + ' in data ' + formatDate(signDate), 10, 40);
                          ctx.fillText('Il documento è conservato secondo la normativa vigente.', 10, 60);
                          ctx.rect(0,0,500,70);
                          ctx.stroke();*/
              let myDataURL = myCanvas.toDataURL('image/jpeg', 1.0); // or `image/png'
              let base64 = myDataURL.split(',')[1];

              let decoded = atob(base64);
              let len = decoded.length;
              let array = new Array(len);
              for (let i = 0; i < len; i++) {
                array[i] = decoded.charCodeAt(i);
              }

              return array;
            }

            // function formatDate(date) {
            //   return pad(date.getDate())
            //     + '-' + pad(date.getMonth() + 1)
            //     + '-' + date.getFullYear()
            //     + ' ' + pad(date.getHours())
            //     + ':' + pad(date.getMinutes())
            //     + ':' + pad(date.getSeconds());
            // }
            //
            // function pad(num) {
            //   return (num < 10 ? '0' : '') + num;
            // }

            // utility function for JWS server response parsing
            function getResultField(response, field) {
              for (let element in response.result) {
                // N.B. for...in - not compatible with IE < 10
                if (response.result[element].key === field) {
                  return response.result[element].value;
                }
              }
              return undefined;
            }

            // PKNet initialization
            com_intesi_pknet_PKNet.init(onInitOK, ajaxKO,
              null, // location.origin + '/PHI_KLINIK/common/signature/pknet.properties', // config file
              'JSESSIONID=' + sessionId, // cookies
              this.signUrlBase + 'pknet_1.9.41.gif.p7m', // license file
              true, // Log mode flag, assign true value to specify verbose mode
              null); // path to log file
          });
        }
      };

      let setPageToKO = () => {
        if (!this.cancelled) {
          this.vm.openAlertMessage(this.translateService.instant('sign'),
            this.translateService.instant('sign-execute'),
            true,
            false,
            'OK',
            'Cancel',
            true,
            null,
            () => {
              this.cancelled = true;
              this.pkNet.stopPolling(); // Doesen't stop polling...
            }
          );
        }
      };

      let startAndWait = () => {
        this.pkNet.start();
        // Polling PkNet Server
        this.pkNet.startPolling(setPageToOK, setPageToKO);
      };

      this.pkNet.checkServerStatus(setPageToOK, startAndWait);

    };

    if (typeof window['pkNet'] === 'undefined') {
      // jQuery.getScript('common/signature/js/pknet.js', sign);
      let script = document.createElement('script');

      script.setAttribute('src', this.signUrlBase + 'js/pknet.js');
      script.onload = sign;
      document.getElementsByTagName('head')[0].appendChild(script);
    } else {
      sign();
    }
  }

    /**
   * Sign an XML file
   * pkNetSignXml('#{DocRepository.fileName}', #{DocRepository.internalId}, '#{session.id}', 'P7M')
   * @param docRepoFileName DocRepository.fileName
   * @param docRepoId DocRepository.internalId
   // * @param docUrl doc to sign url
   // * @param postUrl signed doc post
   * @param sessionId session.id
   * @param signType P7M or PDF
   * @param successCallback function called after succesful sign
   */
  public pkNetSignXml(docRepoFileName, docRepoId, sessionId, signType, successCallback, inTemporary, appOwner, reportType, alreadySigned) {

    this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('sign-download'), true, false);

    let docIds = JSON.parse(docRepoId);

    let docUrl = this.apiUrl + 'GetXml?cid=' + this.cid + '&filename=' + encodeURIComponent(docRepoFileName);;

    if (inTemporary) {
      docUrl = docUrl + '&inTemporary=true';
    }/* else {
      docUrl = docUrl + '&docid=' + encodeURIComponent(docRepoId);
    }*/

    let postUrl = this.apiUrl + 'PostXml?cid=' + this.cid;;

    if (this.Param['p.ie.repository.use'] && this.Param['p.ie.repository.use'].value) {
      docUrl = docUrl + '&useIeRepos=true';
      postUrl = postUrl + '&useIeRepos=true';
    }

    if (appOwner) {
      postUrl = postUrl + '&appOwner=' + appOwner;
    }

    if (reportType) {
      postUrl = postUrl + '&reportType=' + reportType;
    }

    let sign = () => {

      this.pkNet = window['pkNet'];

      let PIN = '';
      let pkNetInitialized = false;

      this.cancelled = false;

      /**
       * Polling task
       */
      let setPageToOK = () => {
        this.pkNet.stopPolling();
        if (pkNetInitialized === false) {
          this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('sign-started'), true, false);
          pkNetInitialized = true;
          this.pkNet.jq.getScript(this.pkNet.getJsStubLocation(), () => {

            let com_intesi_pknet_PKNet = window['com_intesi_pknet_PKNet'];
            let XMLReference;
            let com_intesi_pknet_XMLReference_init;
            let XMLEnvelope;
            let com_intesi_pknet_XMLEnvelope_init;

            let extCounter = 0;
            let credential = null;
            let signedBy = '';

            let ajaxKO = (res) => {
              console.log(res);
              this.vm.openAlertMessage(this.translateService.instant('sign'),
                this.translateService.instant('sign-error') + res.error.code + ':' + res.error.message, true, false);
              logError('Sign error: '  + res.error.code + ':' + res.error.message, sessionStorage['cid']);
              com_intesi_pknet_PKNet.Finalize(function () {
                },
                function () {
                  console.log('An error occurred during PKNet `finalize');
                });
              this.pkNet.stopPolling();
              this.pkNet.quiteShutDown();
              // unblockUI();
            };

            let onInitOK = function () {
                // 1: certificate Key Usage extension is equal to non-repudiation
                com_intesi_pknet_PKNet.setProperty(onCredTypeOK, ajaxKO, 'CredTypeFilter', '1');
              },
              onCredTypeOK = function () {
                // 1: credentials with valid certificate validity
                com_intesi_pknet_PKNet.setProperty(onValidCredOK, ajaxKO, 'ValidCredFilter', '1');
              },
              onValidCredOK = function () {
                // 1: if a single credential is selectable, PkNet automatically select it, without displaying the selection dialog
                com_intesi_pknet_PKNet.setProperty(onAutoSelCredFilterOK, ajaxKO, 'AutoSelCredFilter', '1');
              },
              onAutoSelCredFilterOK = function () {
                com_intesi_pknet_PKNet.GetCredentials(onGetCredentials, ajaxKO);
              },
              onGetCredentials = (res) => {
                this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('sign-pin'), true, false);
                let ICredentials = new window['com_intesi_pknet_ICredentials']();
                ICredentials.pknetJWSinstanceID = getResultField(res, 'instanceId');
                if (ICredentials.pknetJWSinstanceID !== undefined) {
                  ICredentials.getList(onGetList, ajaxKO, PIN);
                }
              },
              onGetList = (res) => {
                this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('sign-pin-ok'), true, false);
                credential =new window['com_intesi_pknet_Credential']();
                credential.pknetJWSinstanceID = [getResultField(res, 'instanceId'), ':0'].join('');
                let credValue = getResultField(res, 'value');
                if (credValue && credValue[0]) {
                  signedBy = credValue[0].c || credValue[0].m_CommonName;
                }
                credential.verifyExpiration(onVerify, ajaxKO, 1800);
              },
              onVerify = (res) => {
                let warningMessage = getResultField(res, 'value');
                if (warningMessage !== undefined && warningMessage !== '') {
                  this.vm.openAlertMessage(this.translateService.instant('sign'),
                  this.translateService.instant('sign-pin-ok') + ' ' + warningMessage.replace(/scadr.*fra/, 'scadrà fra'), true, false);
                };
                onVerifyMessage();
              },
              onVerifyMessage = function(){
                XMLReference = new window['com_intesi_pknet_XMLReference']();
                com_intesi_pknet_XMLReference_init = window['com_intesi_pknet_XMLReference_init'](onRefOK, ajaxKO,
                  XMLReference, // object to be initialized
                  null, // the XML reference data, used to retrieve reference
                  // data value
                  false, // specify if the data object have to be internally inserted
                  // in the XML envelope
                  null, // specify the DataObjectFormat XAdES element (may be null)
                  null, // the reference Id (optional field)
                  "", // XML reference URI the (mandatory field)
                  // Use an empty value to sign the complete XML document
                  // (excluding comments)
                  null, // reference type
                  false, // cacheURI - not used
                  false);// exclude tags from signature - if true it will be
                  // automatically excluded the ds:Object element tags from
                  // signature computation
  
              },
              onRefOK = function(){
                XMLEnvelope = new window['com_intesi_pknet_XMLEnvelope']();
                com_intesi_pknet_XMLEnvelope_init = window['com_intesi_pknet_XMLEnvelope_init'](onStartTranOK, ajaxKO,
                  XMLEnvelope); // object to be initialized)
              },
              onStartTranOK = function(){
                XMLEnvelope.startTransaction(onFirstXMLSign, ajaxKO);
              },
              onFirstXMLSign = function(){
                let signed = false;

                let singleDocUrl = docUrl + '&docid=' + docIds[0];
                let singlePostUrl = postUrl + '&docid=' + docIds[0] + '&s=' + encodeURIComponent(signedBy);

                  if (signType === 'XML'){
                    signed = XMLEnvelope.xmlsign(onFirstXMLSignOK, ajaxKO,
                      singleDocUrl, // input XML document
                      null, // The canonicalization method - if no value is
                            // specified, it will be automatically used
                            // "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
                      [ XMLReference ], // The array of references of the new signature.
                            // Each reference can address an internal element
                            // of the XML envelope or an external document 
                      null, // Specify the XAdES SignatureProductionPlace qualified
                            // property (optional parameter)                         
                      credential,  // credential object
                      PIN, // smart card PIN
                      1, // The envelope creation mode: 1=Enveloped
                      // 2=Enveloping
                      // 3=Detached
                      null, // The SigningTime XAdES attribute
                      // format: YYYY-MM-GG@hh:mm:ss
                      // (use null value to specify the current date).
                      // The signing time attribute is not the time stamp object.
                      singlePostUrl); // signed document
                  } else {
                    logError('Sign error: Sign type unknow: ' + signType, sessionStorage['cid']);
                    throw new Error('Sign type unknow: ' + signType);
                  }
                  // if (!signed) {
                  //   this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('sign-error'), true, true);
                  //   logError('Sign error: params: docUrl: ' + docUrl + ' postUrl: ' + postUrl, sessionStorage['cid']);
                  //   this.pkNet.quiteShutDown();
                  // }

                
              },
              onFirstXMLSignOK = () =>{
                if(docIds.length==1){
                  onXMLSingleSignOK();
                }else{
                  extCounter += 1;                  
                  for(let i = 1; i < docIds.length; i++){
                    let signed = false;

                    let singleDocUrl = docUrl + '&docid=' + docIds[i];
                    let singlePostUrl = postUrl + '&docid=' + docIds[i] + '&s=' + encodeURIComponent(signedBy);

                    if (signType === 'XML'){
                      signed = XMLEnvelope.xmlsign(onXMLSingleSignOK, ajaxKO,
                        singleDocUrl, // input XML document
                        null, // The canonicalization method - if no value is
                              // specified, it will be automatically used
                              // "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
                        [ XMLReference ], // The array of references of the new signature.
                              // Each reference can address an internal element
                              // of the XML envelope or an external document 
                        null, // Specify the XAdES SignatureProductionPlace qualified
                              // property (optional parameter)                         
                        credential,  // credential object
                        null, // smart card PIN
                        1, // The envelope creation mode: 1=Enveloped
                        // 2=Enveloping
                        // 3=Detached
                        null, // The SigningTime XAdES attribute
                        // format: YYYY-MM-GG@hh:mm:ss
                        // (use null value to specify the current date).
                        // The signing time attribute is not the time stamp object.
                        singlePostUrl); // signed document
                  } else {
                    logError('Sign error: Sign type unknow: ' + signType, sessionStorage['cid']);
                    throw new Error('Sign type unknow: ' + signType);
                  }
                  // if (!signed) {
                  //   this.vm.openAlertMessage(this.translateService.instant('sign'), this.translateService.instant('sign-error'), true, true);
                  //   logError('Sign error: params: docUrl: ' + docUrl + ' postUrl: ' + postUrl, sessionStorage['cid']);
                  //   this.pkNet.quiteShutDown();
                  // }

                  }
                }

              },
              onXMLSingleSignOK = () =>{
                extCounter += 1;
                console.log(extCounter);
                if(extCounter===docIds.length){
                  XMLEnvelope.endTransaction(onXMLSignOK, ajaxKO);
                }
              },
              onXMLSignOK = () => {
                this.vm.openAlertMessage(this.translateService.instant('sign'),
                  this.translateService.instant('sign-ok') + ' ' + signedBy, false, true);
                com_intesi_pknet_PKNet.Finalize(function () {
                  },
                  function (finalizeKO) {
                    console.log(finalizeKO);
                  });
                if (successCallback !== undefined) {
                  successCallback();
                }
              };

            function getImageByteArray(/*signer, signDate*/) {
              let myCanvas = document.createElement('canvas');
              myCanvas.height = 60; // 60; under line
              myCanvas.width = 300;

              let ctx = myCanvas.getContext('2d');
              ctx.fillStyle = '#FEFEFE';
              ctx.fillRect(0, 0, 300, 60);
              // Create an image otherwise sign will be invisible
              /* let ctx = myCanvas.getContext('2d');
                          ctx.font = '12px serif';
                          ctx.fillText('Rappresentazione di un documento firmato elettronicamente, secondo la normativa vigente.', 10, 20);
                          ctx.fillText('Firmato da: ' + signer + ' in data ' + formatDate(signDate), 10, 40);
                          ctx.fillText('Il documento è conservato secondo la normativa vigente.', 10, 60);
                          ctx.rect(0,0,500,70);
                          ctx.stroke();*/
              let myDataURL = myCanvas.toDataURL('image/jpeg', 1.0); // or `image/png'
              let base64 = myDataURL.split(',')[1];

              let decoded = atob(base64);
              let len = decoded.length;
              let array = new Array(len);
              for (let i = 0; i < len; i++) {
                array[i] = decoded.charCodeAt(i);
              }

              return array;
            }

            // function formatDate(date) {
            //   return pad(date.getDate())
            //     + '-' + pad(date.getMonth() + 1)
            //     + '-' + date.getFullYear()
            //     + ' ' + pad(date.getHours())
            //     + ':' + pad(date.getMinutes())
            //     + ':' + pad(date.getSeconds());
            // }
            //
            // function pad(num) {
            //   return (num < 10 ? '0' : '') + num;
            // }

            // utility function for JWS server response parsing
            function getResultField(response, field) {
              for (let element in response.result) {
                // N.B. for...in - not compatible with IE < 10
                if (response.result[element].key === field) {
                  return response.result[element].value;
                }
              }
              return undefined;
            }

            // PKNet initialization
            com_intesi_pknet_PKNet.init(onInitOK, ajaxKO,
              null, // location.origin + '/PHI_KLINIK/common/signature/pknet.properties', // config file
              'JSESSIONID=' + sessionId, // cookies
              this.signUrlBase + 'pknet_1.9.41.gif.p7m', // license file
              true, // Log mode flag, assign true value to specify verbose mode
              null); // path to log file
          });
        }
      };

      let setPageToKO = () => {
        if (!this.cancelled) {
          this.vm.openAlertMessage(this.translateService.instant('sign'),
            this.translateService.instant('sign-execute'),
            true,
            false,
            'OK',
            'Cancel',
            true,
            null,
            () => {
              this.cancelled = true;
              this.pkNet.stopPolling(); // Doesen't stop polling...
            }
          );
        }
      };

      let startAndWait = () => {
        this.pkNet.start();
        // Polling PkNet Server
        this.pkNet.startPolling(setPageToOK, setPageToKO);
      };

      this.pkNet.checkServerStatus(setPageToOK, startAndWait);

    };

    if (typeof window['pkNet'] === 'undefined') {
      // jQuery.getScript('common/signature/js/pknet.js', sign);
      let script = document.createElement('script');

      script.setAttribute('src', this.signUrlBase + 'js/pknet.js');
      script.onload = sign;
      document.getElementsByTagName('head')[0].appendChild(script);
    } else {
      sign();
    }
  }

  checkUrl(urlToCheck): any {
    let urlChecker = new XMLHttpRequest();
    let newUrl = urlToCheck;
    // Opens the file and specifies the method (GET), Asynchronous (true)
    urlChecker.open('GET', urlToCheck, false);

    // check each time the ready state changes to see if the object is ready
    urlChecker.onreadystatechange = (() => {
      if (urlChecker.readyState === 4) {
        if ((urlChecker.status === 200) || (urlChecker.status === 0)) {
          return;
        } else {
          newUrl = this.apiUrl + 'common/signature/';
          return;
        }
      }
    });
    urlChecker.send();
    return newUrl;
  }
}
