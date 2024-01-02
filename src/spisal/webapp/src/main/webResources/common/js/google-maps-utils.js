/**
 * Google maps utils
 */

function GoogleMapsUtils(googleMapsApiKey) {
    "use strict";

    // Distance in meters to near points
    var snapDistance = 0;

    var map;
    var markers = [];

    var icon = {
        path: 'm29.926877,9.140494c0,5.049511 -8.042672,14.129751 -14.932189,20.779071c-5.871141,-6.857516 -14.932189,-15.730397 -14.932189,-20.779699c0,-5.049511 6.685294,-9.142866 14.932189,-9.142866c8.246553,0 14.932189,4.093355 14.932189,9.142866l0,0.000628z',
        fillOpacity: 0.8,
        scale: 1,
        strokeWeight: 1
    };

    var constructionSiteIcon = {
        path: 'm5.779963,29.94483l-1.493658,0c-0.018189,0 -0.035938,-0.001389 -0.053393,-0.004012l-3.060952,0c-0.131134,0 -0.252293,-0.073763 -0.317713,-0.193203s-0.065273,-0.266812 0.000441,-0.386098l3.064913,-5.561388l0,-17.807582l-3.552923,0c-0.15695,0 -0.296444,-0.105089 -0.347049,-0.261257s-0.001174,-0.329156 0.123065,-0.429924l3.776906,-3.065484l0,-1.850092c0,-0.21311 0.164136,-0.38579 0.366705,-0.38579l1.493658,0c0.202567,0 0.366705,0.17268 0.366705,0.38579l0,1.699635l8.052089,1.307672c0.003081,0.000463 0.006161,0.00108 0.009094,0.001543l11.266471,1.829876c0.043857,0.006327 0.085369,0.020833 0.122918,0.041974c0.036818,0.020678 0.069821,0.047838 0.097544,0.079781c0.000294,0.000464 0.000441,0.000464 0.000441,0.000618c0.044444,0.051233 0.075834,0.115119 0.088889,0.185796c0.004547,0.025 0.006894,0.050616 0.006747,0.076695l0,6.702862l3.948084,4.18381c0.10429,0.110644 0.135093,0.27638 0.078328,0.420202c-0.056913,0.143822 -0.19054,0.237492 -0.338542,0.237492l-8.138631,0c-0.148295,0 -0.282069,-0.093978 -0.338834,-0.23811c-0.056766,-0.144285 -0.025376,-0.310174 0.079501,-0.42051l3.976686,-4.183656l0,-6.318924l-8.49052,0c-0.003081,0 -0.005867,0 -0.008801,0l-10.411463,0l0,17.765145l4.654502,5.541789c0.09549,0.113576 0.118813,0.275608 0.059553,0.414029c-0.05926,0.138267 -0.1901,0.227307 -0.334288,0.227307l-4.692933,0c-0.017601,0.002623 -0.03535,0.004012 -0.053539,0.004012zm0.366705,-0.775745l3.566417,0l-3.566417,-4.246309l0,4.246309zm-1.493659,0l0.760251,0l0,-5.263713c0,-0.00108 0,-0.002007 0,-0.002932l0,-17.909894l-0.760251,0l0,17.904492c0,0.00463 0,0.009414 0,0.014043l0,5.258003zm-2.844451,0l2.11119,0l0,-3.830891l-2.11119,3.830891zm20.416766,-12.786918l6.372584,0l-3.174777,-3.364393l-3.197807,3.364393zm-5.544127,-11.161199l3.964365,0l-5.044969,-0.819417l1.080604,0.819417zm-3.739208,0l2.484934,0l-1.274076,-0.966017l-1.210857,0.966017zm-3.924027,0l2.28178,0l-1.103339,-1.474796l-1.178441,1.474796zm-2.871293,0l1.448188,0l-1.448188,-1.766608l0,1.766608zm-1.493659,0l0.760251,0l0,-2.419055l-0.760251,0l0,2.419055zm-3.210568,0l2.477307,0l0,-2.010736l-2.477307,2.010736zm9.62701,-1.55535l0.974847,1.303043c0.39736,-0.317016 0.79472,-0.634032 1.192081,-0.951048l-2.166928,-0.351995zm-4.361725,-0.708464l1.60059,1.952713l1.197362,-1.498252c-0.932651,-0.151487 -1.865301,-0.302974 -2.797952,-0.454461zm-2.054717,-0.92682l0.760251,0l0,-1.258754l-0.760251,0l0,1.258754z',
        fillOpacity: 0.8,
        scale: 1,
        strokeWeight: 1
    };

    var companyIcon = {
        path: 'm25.042166,28.334161l0,-25.219253l-4.561631,0l0,-2.448244l-7.494963,0l0,2.448244l0,1.232495l0,3.982164l-6.588024,0l0,2.444894l-1.436734,0l0,17.5597l-4.627481,0l0,1.232495l4.627481,0l8.024758,0l1.101496,0l2.355645,0l4.848978,0l3.753468,0l4.621495,0l0,-1.232495l-4.624488,0zm-10.955097,-26.438351l5.294964,0l0,1.215749l-5.294964,0l0,-1.215749zm-6.591018,7.659553l5.294964,0l0,1.215749l-5.294964,0l0,-1.215749zm-1.436734,18.778798l0,-16.330555l0.338231,0l6.588024,0l0,16.330555l-6.926256,0zm11.4819,0l0,-3.663992l2.648978,0l0,3.663992l-2.648978,0zm3.747481,0l0,-4.893138l-4.848978,0l0,4.893138l-2.355645,0l0,-17.56305l0,-6.427058l6.393467,0l3.460135,0l0,23.990108l-2.648978,0z',
        fillOpacity: 0.8,
        scale: 1,
        strokeWeight: 1
    };

    var yellowMarker = jQuery.extend({fillColor: 'orange', strokeColor: 'orange'}, companyIcon);

    var violetMarker = jQuery.extend({fillColor: 'violet', strokeColor: 'violet'}, constructionSiteIcon);

    var brownMarker = jQuery.extend({fillColor: 'brown', strokeColor: 'brown'}, icon);

    if (typeof google !== 'object' || typeof google.maps !== 'object') {
        var script = document.createElement('script');
        script.setAttribute('src', 'https://maps.googleapis.com/maps/api/js?key=' + googleMapsApiKey + '&libraries=geometry');
        // script.onload = function() {alert("Script loaded and ready");};
        document.getElementsByTagName('head')[0].appendChild(script);
    }

    /**
     * public openMap: opens a google map inside container div, if already opened clean markers
     * @param {*} container 
     */
    this.openMap = function(container) {

        if (container.clientWidth>0) { // if map visible
            if (!map || !container.firstChild) {
                map = new google.maps.Map(container, { zoom: 10, center: GoogleMapsUtils.defaultPosition });
                map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(buildLegend());
                GoogleMapsUtils.getCurrentLocation(function(position) {
                    map.panTo(position);
                });
            }
            for (var i = 0; i < markers.length; i++) {
                markers[i].setMap(null);
            }
            markers = [];
        } else {
            map = null;
        }
        return map;
    }

    /**
     * private build a legend
     */
    var buildLegend = function() {
        var ul = document.createElement('ul');
        ul.style.fontSize='12px';
        ul.innerHTML = 
        '<li><div style="width:15px;height:15px;background-color:orange"></div>Impresa</li>' +
        '<li><div style="width:15px;height:15px;background-color:violet"></div>Cantiere</li>' +
        '<li><div style="width:15px;height:15px;background-color:brown"></div>Altro</li>';
        document.body.appendChild(ul);
        return ul;
    }

    /**
     * Open google maps into container and load ProtocolloList from conversation
     * Used by: MOD_home/CORE/FORMS/segnalazioni.mmgp
     */
    this.initProtocolloMap = function(container, injectCallBack) {

        if (this.openMap(container)) {

            loadList(BaseAction.buildUrl('protocollos/list')).done(function(protocolloList) {
                if(protocolloList) {
                    for (var z = 0;z < protocolloList.length;z++) {
                        var protocollo = protocolloList[z];

                        var markerOpt = {
                            map: map,
                            entity: protocollo
                        };
                        
                        if (protocollo.riferimentoCantiere && protocollo.riferimentoCantiere.latitudine && protocollo.riferimentoCantiere.longitudine) {
                            markerOpt.position = { lat: +protocollo.riferimentoCantiere.latitudine, lng: +protocollo.riferimentoCantiere.longitudine };
                        } else if(protocollo.ubicazioneX && protocollo.ubicazioneY) {
                            markerOpt.position = { lat: +protocollo.ubicazioneX, lng: +protocollo.ubicazioneY };
                        }

                        if (markerOpt.position) {
                            
                            if (protocollo.ubicazione.code === 'Cantiere') {
                                markerOpt.icon = violetMarker;
                            } else if (protocollo.ubicazione.code === 'Ditta') {
                                markerOpt.icon = yellowMarker;
                            } else {
                                markerOpt.icon = brownMarker;
                            }

                            markerOpt.icon.anchor = new google.maps.Point(15, 15);
                            
                            marker = new google.maps.Marker(markerOpt);
                            markers.push(marker);
                            
                            marker.addListener('click', function(mrk) {
                                var closestMarkers = findClosestMarkers(mrk, this.entity.internalId);

                                if (closestMarkers && closestMarkers.length > 1) {
                                    markersInfoWindow(closestMarkers, 'protocollo', injectCallBack, this);       
                                } else {
                                    protocolloInfoWindow(this.entity.internalId, injectCallBack, this);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    /**
     * Open protocolloId details into an InfoWindow on top of marker
     * @param {*} protocolloId 
     * @param {*} injectCallBack 
     * @param {*} marker 
     */
    var protocolloInfoWindow = function(protocolloId, injectCallBack, marker) {
        var protocolloAction = new BaseAction('Protocollo');

        protocolloAction.inject(protocolloId, [
        'code.displayName', 
        'statusCode.code', 
        'ubicazione', 
        'ubicazioneDitta', 
        'ubicazioneEntita',
        'ubicazioneSede', 
        'infortunio.gravita', 
        'infortunio.sedeLesione', 
        'infortunio.naturaLesione',
        'infortunio.infortuniExt.entita',
        'riferimentoCantiere.committente.person',
        'riferimentoCantiere.personeCantiere.person',
        'riferimentoCantiere.personeCantiere.ruolo',
        'riferimentoCantiere.ditteCantiere.personeGiuridiche',
        'riferimentoUtente',
        'dettagliBonifiche.tipoSegnalazione',
        'dettagliBonifiche.tipoBonifica',
        'dettagliBonifiche.tipoMatrice'
        ]
        ).done(function(entity) {

            if (injectCallBack) {
                injectCallBack();
            }
            
            var content = '<section class="info"><a class="fa protocollo ' + entity.statusCode.code + '" href="#" onclick="manageTask(\'btnManage\',\'BtnManagement\')" title="Gestisci"></a>' + 
            '<span class="comm-type">' + entity.code.displayName + '</span>' + formatISODate(entity.data) +
            '<a class="fa fa-map-marker" href="https://www.google.com/maps/dir/?api=1&destination=' + marker.position.lat() + ',' + marker.position.lng() + '" target="_blank" title="Vai a" style="float:right;"></a>' +
            '<br>' +
            '<dt>Numero protocollo</dt>' + (entity.nprotocollo ? entity.nprotocollo : '') + '<br>' +
            '<dt>Data protocollo</dt>' + formatISODate(entity.dataProtocollo)  + '<br>' +
            '<dt>Id</dt>' + entity.internalId + '<br>' +
            
            '<dt>Riferito a</dt>' + (entity.printRiferito ? entity.printRiferito : '') + ' ' + (entity.riferimentoUtente ? formatISODate(entity.riferimentoUtente.birthTime) : '') + '<br>';
            
            content += '<dt>Ubicazione</dt>';
            content += entity.ubicazione.currentTranslation + ' ';
            if (entity.ubicazioneEntita && entity.ubicazioneEntita.currentTranslation) {
                content += entity.ubicazioneEntita.currentTranslation + ' ';
            }
            if (entity.ubicazioneDitta && entity.ubicazioneDitta.denominazione) {
                content += entity.ubicazioneDitta.denominazione + ' ';
            }
            if (entity.ubicazioneSede && entity.ubicazioneSede.denominazioneUnitaLocale) {
                content += entity.ubicazioneSede.denominazioneUnitaLocale + ' ';
            }
            content += printAddr(entity.ubicazioneAddr);
            content += '<br>';
            
            if (entity.code.code === '1' && entity.infortunio) { // Tipo 1
                content += getInfortuniDetails(entity.infortunio);
                content += '<dt>Oggetto</dt>' + (entity.oggetto ? entity.oggetto : '') + '<br>';
            }
            
            if (entity.code.code === '4' && entity.riferimentoCantiere) { // Tipo 4
                content += getCantiereDetails(entity.riferimentoCantiere);
            }
            
            if (entity.code.code === '5' && entity.dettagliBonifiche && entity.dettagliBonifiche.length > 0) { // Tipo 5
                content += getDettagliBonificheDetails(entity.dettagliBonifiche[0]);
            }

            var infowindow = new google.maps.InfoWindow({content: content});
            infowindow.open(map, marker);

        });
    }

    /**
     * Open google maps into container and load ProcpraticheList from conversation
     * Used by: MOD_home/CORE/FORMS/procpratiche.mmgp
     */
    this.initProcpraticheMap = function(container, injectCallBack) {

        if (this.openMap(container)) {
    
            loadList(BaseAction.buildUrl('procpratiches/list')).done(function(procpraticheList) {
                if(procpraticheList) {
                    for (var z = 0;z < procpraticheList.length;z++) {
                        var procpratiche = procpraticheList[z];

                        var markerOpts = [];

                        if (procpratiche.serviceDeliveryLocation.area.code === 'WORKACCIDENT' && procpratiche.infortuni) {
                            for (var i = 0; i < procpratiche.infortuni.length; i++) {
                                var infortuni = procpratiche.infortuni[i];
                                if (infortuni.place && infortuni.infortuniExt && infortuni.infortuniExt.latitudine && infortuni.infortuniExt.longitudine) {
                                    var infMarker;
                                    if (infortuni.place.code === 'OwnCompany' || infortuni.place.code === 'Company') {
                                        infMarker = yellowMarker;
                                    } else if (infortuni.place.code === 'Yard') {
                                        infMarker = violetMarker;
                                    } else {
                                        infMarker = brownMarker;
                                    }
                                    markerOpts.push({
                                        position: { lat: +infortuni.infortuniExt.latitudine, lng: +infortuni.infortuniExt.longitudine },
                                        icon: infMarker
                                    });
                                }
                            }
                        } else if (procpratiche.serviceDeliveryLocation.area.code === 'SUPERVISION' && procpratiche.vigilanza) {
                            if (procpratiche.vigilanza.type.code === 'Asbestos') {
                                if (procpratiche.vigilanza.sitoBonificaSede && procpratiche.vigilanza.sitoBonificaSede.latitudine && procpratiche.vigilanza.sitoBonificaSede.longitudine) {
                                markerOpts.push({
                                    position: { lat: +procpratiche.vigilanza.sitoBonificaSede.latitudine, lng: +procpratiche.vigilanza.sitoBonificaSede.longitudine },
                                    icon: yellowMarker
                                });
                            } else if (procpratiche.vigilanza.cantiere && procpratiche.vigilanza.cantiere.latitudine && procpratiche.vigilanza.cantiere.longitudine) {
                                markerOpts.push({
                                    position: { lat: +procpratiche.vigilanza.cantiere.latitudine, lng: +procpratiche.vigilanza.cantiere.longitudine },
                                    icon: violetMarker
                                });
                            }  else if (procpratiche.vigilanza.latitudine && procpratiche.vigilanza.longitudine) {
                                markerOpts.push({
                                    position: { lat: +procpratiche.vigilanza.latitudine, lng: +procpratiche.vigilanza.longitudine },
                                    icon: brownMarker
                                });
                            }
                            } else if (procpratiche.vigilanza.type.code === 'Yard' && procpratiche.vigilanza.cantiere.latitudine && procpratiche.vigilanza.cantiere.longitudine) {
                                markerOpts.push({
                                    position: { lat: +procpratiche.vigilanza.cantiere.latitudine, lng: +procpratiche.vigilanza.cantiere.longitudine },
                                    icon: violetMarker
                                });
                            } else if (procpratiche.vigilanza.type.code === 'Generic' && procpratiche.vigilanza.personaGiuridicaSede && procpratiche.vigilanza.personaGiuridicaSede.length > 0) {
                                for (var s = 0; s < procpratiche.vigilanza.personaGiuridicaSede.length; s++) {
                                    var pgs = procpratiche.vigilanza.personaGiuridicaSede[s];
                                    if (pgs.sede && pgs.sede.latitudine && pgs.sede.longitudine) {
                                        markerOpts.push({
                                            position: { lat: +pgs.sede.latitudine, lng: +pgs.sede.longitudine },
                                            icon: yellowMarker
                                        });
                                    }
                                }
                            }
                        } else if (procpratiche.serviceDeliveryLocation.area.code === 'WORKDISEASE' && procpratiche.malattiaProfessionale && procpratiche.malattiaProfessionale.ditteMalattie) {
                            for (var d = 0; d < procpratiche.malattiaProfessionale.ditteMalattie.length; d++) {
                                var dm = procpratiche.malattiaProfessionale.ditteMalattie[d];
                                if (dm.sedi && dm.sedi.latitudine && dm.sedi.longitudine) {
                                    markerOpts.push({
                                        position: { lat: +dm.sedi.latitudine, lng: +dm.sedi.longitudine },
                                        icon: yellowMarker
                                    });
                                }
                            }
                        }

                        for (var m = 0; m < markerOpts.length; m++) {
                            var markerOpt = markerOpts[m];

                            markerOpt.icon.anchor = new google.maps.Point(15, 15);
                            markerOpt.map = map;
                            markerOpt.entity = procpratiche;
                            
                            marker = new google.maps.Marker(markerOpt);
                            
                            markers.push(marker);
                            
                            marker.addListener('click', function(mrk) {

                                var closestMarkers = findClosestMarkers(mrk, this.entity.internalId);

                                if (closestMarkers && closestMarkers.length > 1) {
                                    markersInfoWindow(closestMarkers, 'procpratiche', injectCallBack, this);       
                                } else {
                                    procpraticheInfoWindow(this.entity.internalId, injectCallBack, this);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    /**
     * Open procpraticheId details into an InfoWindow on top of marker
     * @param {*} procpraticheId 
     * @param {*} injectCallBack 
     * @param {*} marker 
     */
    var procpraticheInfoWindow = function(procpraticheId, injectCallBack, marker) {
        
        var procpraticheAction = new BaseAction('Procpratiche');

        procpraticheAction.inject(procpraticheId, [
            'serviceDeliveryLocation.area', 
            'statusCode.code',
            'rfp',
            'infortuni.person', 
            'infortuni.gravita', 
            'infortuni.sedeLesione', 
            'infortuni.naturaLesione',
            'infortuni.comparto',
            'infortuni.infortuniExt.entita',
            'infortuni.infortuniExt.addr',
            'infortuni.personeGiuridicheExt.denominazione',
            'infortuni.personeGiuridiche.denominazione',
            'infortuni.sedi.addr', 
            'attivita.code',
            'attivita.provvedimenti',
            'attivita.miglioramenti',
            'esitoPratica',
            'vigilanza.type',
            'vigilanza.cantiere.committente.person', // FIXME IF PERSON IS THE SAME RETURNS PROXYSTRING:FAILLLL
            'vigilanza.cantiere.personeCantiere.person',
            'vigilanza.cantiere.personeCantiere.ruolo',
            'vigilanza.cantiere.ditteCantiere.personeGiuridiche',
            'vigilanza.tipoSegnalazione',
            'vigilanza.tipoIntervento',
            'vigilanza.comparto',
            //'vigilanza.personaGiuridicaSede'
            'malattiaProfessionale.gravita',
            'malattiaProfessionale.comparto'
        ]
        ).done(function(Procpratiche) {

            if (injectCallBack) {
                injectCallBack();
            }

            var content = '<section class="info"><a class="fa procpratiche ' + Procpratiche.statusCode.code + '" href="#" onclick="manageTask(\'btnManage\',\'BtnManagement\')" title="Gestisci"></a>' + 
            '<span class="comm-type">' + Procpratiche.serviceDeliveryLocation.area.currentTranslation + '</span>' + formatISODate(entity.data) +
            '<a class="fa fa-map-marker" href="https://www.google.com/maps/dir/?api=1&destination=' + marker.position.lat() + ',' + marker.position.lng() + '" target="_blank" title="Vai a" style="float:right;"></a>' +
            '<br>' +
            '<dt>Numero</dt>' + (Procpratiche.numero ? Procpratiche.numero : '') + '<br>' +
            '<dt>Data</dt>' + formatISODate(Procpratiche.data)  + '<br>' + 
            '<dt>Rfp</dt>' + (Procpratiche.rfp ? Procpratiche.rfp.name.giv + ' ' + Procpratiche.rfp.name.fam : '')  + '<br>' +
            '<dt>Oggetto</dt>' + (Procpratiche.oggetto ? Procpratiche.oggetto : '') + '<br>';
            if (Procpratiche.serviceDeliveryLocation.area.code === 'WORKACCIDENT') {
                for (var i = 0; i < Procpratiche.infortuni.length; i++) {
                    content += getInfortuniDetails(Procpratiche.infortuni[i]);
                    content += '<dt>Luogo</dt>' + (Procpratiche.infortuni[i].place ? Procpratiche.infortuni[i].place.currentTranslation : '') + ' ' + (Procpratiche.infortuni[i].infortuniExt && Procpratiche.infortuni[i].infortuniExt.entita ? Procpratiche.infortuni[i].infortuniExt.entita.currentTranslation : '') + '<br>';
                    content += '<dt>Denominazione</dt>' + (Procpratiche.infortuni[i].personeGiuridicheExt ? Procpratiche.infortuni[i].personeGiuridicheExt.denominazione ? Procpratiche.infortuni[i].personeGiuridicheExt.denominazione : '' : '') + '<br>';
                    content += '<dt>Indirizzo</dt>' + printAddr(Procpratiche.infortuni[i].infortuniExt.addr) + '<br>';
                    content += '<dt>Ditta Infortunato </dt>' + (Procpratiche.infortuni[i].personeGiuridiche ? Procpratiche.infortuni[i].personeGiuridiche.denominazione : '') + ' ' + (Procpratiche.infortuni[i].sedi ? printAddr(Procpratiche.infortuni[i].sedi.addr) : '') + '<br>';
                    content += '<dt>Comparto ditta Infortunato </dt>' + (Procpratiche.infortuni[i].comparto ? Procpratiche.infortuni[i].comparto.currentTranslation : '') + '<br>';
                }
            } else if (Procpratiche.serviceDeliveryLocation.area.code === 'SUPERVISION' && Procpratiche.vigilanza && Procpratiche.vigilanza.type) {
                if (Procpratiche.vigilanza.type.code === 'Yard' && Procpratiche.vigilanza.cantiere) {
                    content += getCantiereDetails(Procpratiche.vigilanza.cantiere);
                } else if (Procpratiche.vigilanza.type.code === 'Asbestos') {
                    content += getDettagliBonificheDetails(Procpratiche.vigilanza);
                } else if (Procpratiche.vigilanza.type.code === 'Generic') { // Vigilanza ditte
                    content += '<dt>Comparto</dt>' + (Procpratiche.comparto ? Procpratiche.comparto.currentTranslation : '') + '<br>';
                }
            } else if (Procpratiche.serviceDeliveryLocation.area.code === 'WORKDISEASE' && Procpratiche.malattiaProfessionale) {
                content += '<dt>Diagnosi</dt>' + (Procpratiche.malattiaProfessionale.diagText ? Procpratiche.malattiaProfessionale.diagText : '') + '<br>';
                content += '<dt>Gravità</dt>' + (Procpratiche.malattiaProfessionale.gravita ? Procpratiche.malattiaProfessionale.gravita.currentTranslation : '') + '<br>';
                content += '<dt>Comparto</dt>' + (Procpratiche.malattiaProfessionale.comparto ? Procpratiche.malattiaProfessionale.comparto.keywords : '') + '<br>';
            }
            content += '<dt>Attività elementari</dt>';
            var activitySummary = {};
            var provvedimenti = false;
            var miglioramenti = false;
            for (var i = 0; i < Procpratiche.attivita.length; i++) {
                var Attivita = Procpratiche.attivita[i];
                // content += '<dt>Tipo</dt>' + (Attivita.code ? Attivita.code.currentTranslation : '')  + '<br>';
                if (Attivita.code && Attivita.isActive) {
                    if (!activitySummary[Attivita.code.currentTranslation]) {
                        activitySummary[Attivita.code.currentTranslation] = 1;
                    } else {
                        activitySummary[Attivita.code.currentTranslation]++;
                    }
                }
                if (Attivita.provvedimenti && Attivita.provvedimenti.length !== 0) {
                    provvedimenti = true;
                }
                if (Attivita.miglioramenti && Attivita.miglioramenti.length !== 0) {
                    miglioramenti = true;
                }
            }
            for (var s in activitySummary) {
                content += s + ': ' + activitySummary[s] + ' ';
            }
            content += '<br>';
            content += '<dt>Provvedimenti</dt>' + (provvedimenti ? 'Si': 'No') + '<br>';
            content += '<dt>Miglioramenti</dt>' + (miglioramenti ? 'Si': 'No') + '<br>';
            content += '<dt>Atto conclusivo</dt>' + (Procpratiche.esitoPratica ? Procpratiche.esitoPratica.currentTranslation : '') + '<br>';

            var infowindow = new google.maps.InfoWindow({content: content});
            infowindow.open(map, marker);
        });
    }

    /**
     * private Get details of Infortuni
     * @param {*} Infortuni 
     */
    var getInfortuniDetails = function(Infortuni) {
        var content = '';
        if (Infortuni.person && Infortuni.person.name) {
            content += '<dt>Infortunato</dt>' + Infortuni.person.name.giv + ' ' + Infortuni.person.name.fam  + ' ' + formatISODate(Infortuni.person.birthTime) + '<br>';
        }
        content += '<dt>Giorni prognosi iniziali</dt>' + (Infortuni.ggPrognosi1 ? Infortuni.ggPrognosi1 : '')  + '<br>' +
        '<dt>Gravità iniziale</dt>' + (Infortuni.gravita ? Infortuni.gravita.currentTranslation : '') + '<br>' +
        '<dt>Sede lesione</dt>' + (Infortuni.sedeLesione ? Infortuni.sedeLesione.currentTranslation : '') + '<br>' +
        '<dt>Natura lesione</dt>' + (Infortuni.naturaLesione ? Infortuni.naturaLesione.currentTranslation : '') + '<br>';
        return content;
    }

    /**
     * private Get details of Cantiere
     * @param {*} Cantiere 
     */
    var getCantiereDetails = function(Cantiere) {
        var content = '<dt>Natura dell\' opera</dt>' + Cantiere.naturaOpera + '<br>' +
        '<dt>Tipologia cantiere</dt>' + (Cantiere.tagCantiere ? Cantiere.tagCantiere : '') + '<br>'; // +
        content += '<dt>Data comunicazione</dt>' + formatISODate(Cantiere.dataComunicazione) + '<br>' +
        '<dt>Data presunta inizio lavori</dt>' + formatISODate(Cantiere.inizioLavori) + '<br>' +
        '<dt>Data presunta fine</dt>' + formatISODate(Cantiere.fineLavori) + '<br>' +
        '<dt>Nr. massimo lavoratori</dt>' + (Cantiere.maxWorkers ? Cantiere.maxWorkers : '') + '<br>' +
        '<dt>Nr. imprese in cantiere</dt>' + (Cantiere.numeroImprese ? Cantiere.numeroImprese : '') + '<br>' +
        '<dt>Nr. lavoratori autonomi</dt>' + (Cantiere.numeroAutonomi ? Cantiere.numeroAutonomi : '') + '<br>' +
        '<dt>Ammontare complessivo</dt>' + (Cantiere.cost ? Cantiere.cost : '') + '<br>';
        if (Cantiere.committente) {
            content += '<dt>Committenti</dt>';
            for (var c = 0; c < Cantiere.committente.length; c++) {
                var committente = Cantiere.committente[c];
                if (committente.person && committente.person.name) {
                    content += committente.person.name.giv + ' ' + committente.person.name.fam + ', ';
                }
            }
            if (content.endsWith(', ')) {
                content = content.substring(0, content.length - 2);
            }
            content += '<br>';
        }
        var responsabili = '', coordPrg = '', coordReal = '';
        if (Cantiere.personeCantiere) {
            for (var p = 0; p < Cantiere.personeCantiere.length; p++) {
                var personeCantiere = Cantiere.personeCantiere[p];
                if (personeCantiere.ruolo && personeCantiere.person && personeCantiere.person.name) {
                    if (personeCantiere.ruolo.code === 'RUOLOCANT01') {
                        responsabili += personeCantiere.person.name.giv + ' ' + personeCantiere.person.name.fam + ', ';
                    } else if (personeCantiere.ruolo.code === 'RUOLOCANT02') {
                        coordPrg += personeCantiere.person.name.giv + ' ' + personeCantiere.person.name.fam + ', ';
                    } else if (personeCantiere.ruolo.code === 'RUOLOCANT03') {
                        coordReal += personeCantiere.person.name.giv + ' ' + personeCantiere.person.name.fam + ', ';
                    }
                }
            }
        }
        if (responsabili.endsWith(', ')) {
            responsabili = responsabili.substring(0, responsabili.length - 2);
        }
        if (coordPrg.endsWith(', ')) {
            coordPrg = coordPrg.substring(0, coordPrg.length - 2);
        }
        if (coordReal.endsWith(', ')) {
            coordReal = coordReal.substring(0, coordReal.length - 2);
        }
        content += '<dt>Responsabili</dt>' + responsabili + '<br>' +
        '<dt>Coordinatori progettazione</dt>' + coordPrg + '<br>' +
        '<dt>Coordinatori realizzazione</dt>' + coordReal + '<br>';
        if (Cantiere.ditteCantiere) {
            content += '<dt>Imprese</dt>';
            for (var d = 0; d < Cantiere.ditteCantiere.length; d++) {
                var ditteCantiere = Cantiere.ditteCantiere[d];
                content += ditteCantiere.personeGiuridiche.denominazione + ', ';
            }
            if (content.endsWith(', ')) {
                content = content.substring(0, content.length - 2);
            }
            content += '<br>';
        }
        return content;
    }

    /**
     * private Get details of DettagliBonifiche or Vigilanza
     * @param {*} dettagliBonifiche can be an com.phi.entities.baseEntity.DettagliBonifiche or  com.phi.entities.baseEntity.Vigilanza
     */
    var getDettagliBonificheDetails = function(dettagliBonifiche) {
        var content = '<dt>Tipo notifica</dt>' + (dettagliBonifiche.tipoSegnalazione ? dettagliBonifiche.tipoSegnalazione.currentTranslation : '') + '<br>';
        content += '<dt>Tipo bonifica</dt>' + (dettagliBonifiche.tipoBonifica ? dettagliBonifiche.tipoBonifica.currentTranslation : '') + (dettagliBonifiche.tipoIntervento ? dettagliBonifiche.tipoIntervento.currentTranslation : '') + '<br>';
        content += '<dt>Tipo matrice</dt>' + (dettagliBonifiche.tipoMatrice ? dettagliBonifiche.tipoMatrice.currentTranslation : '') + (dettagliBonifiche.friabile ? 'Friabile' : '') + (dettagliBonifiche.compatto ? 'Compatto' : '') + '<br>';
        content += '<dt>Quantità bonificata Kg</dt>' + (dettagliBonifiche.kg | dettagliBonifiche.bonificatiKgEffettivi) + '<br>';
        content += '<dt>Inizio lavori dichiarato </dt>' + formatISODate((dettagliBonifiche.inizioLavori | dettagliBonifiche.presuntoInizioLavori)) + '<br>';
        content += '<dt>Durata</dt>' + (dettagliBonifiche.durata | dettagliBonifiche.durataLavori) + '<br>';
        content += '<dt>Numero lavoratori</dt>' + (dettagliBonifiche.nlav | dettagliBonifiche.numLavoratori) + '<br>';
        content += '<dt>Natura dell’opera</dt>' + (dettagliBonifiche.naturaOpera ? dettagliBonifiche.naturaOpera : '') + '<br>';
        content += '<dt>Committente</dt>' + (dettagliBonifiche.committente ? dettagliBonifiche.committente : '') + '<br>';
        return content;
    }

    /**
     * Find markers with distance < snapDistance
     * @param {*} mrk 
     * @param {*} id 
     */
    var findClosestMarkers = function(mrk, id) {
        var closestMarkers = [];
        for(var m=0; m<markers.length; m++) {
            var distance = google.maps.geometry.spherical.computeDistanceBetween(mrk.latLng, markers[m].position)
            if (distance <= snapDistance /*&& id !== markers[m].entity.internalId*/) {
                closestMarkers.push(markers[m]);
            }
        }
        return closestMarkers;
    };

    /**
     * private create InfoWindow containing list of closest markers
     * @param {*} closestMarkers 
     * @param {*} type 'protocollo' | 'procpratiche'
     * @param {*} injectCallBack 
     * @param {*} marker 
     */
    var markersInfoWindow = function(closestMarkers, type, injectCallBack, marker) {
        var div = document.createElement('div');
        div.classList.add('dt')
        var table = document.createElement('table');
        var tbody = document.createElement('tbody');
        tbody.classList.add('selectable')

        table.appendChild(tbody);
        div.appendChild(table);

        if (closestMarkers.length > 0) {
            for (var c = 0; c < closestMarkers.length; c++) {
                var e = closestMarkers[c].entity;
                var tr = document.createElement('tr');
                tr.onclick = (function(id, marker) { // closure to save current loop vars 
                    return function() { 
                        infowindow.close();
                        if (type === 'protocollo') {
                            protocolloInfoWindow(id, injectCallBack, marker);
                        } else {
                            procpraticheInfoWindow(id, injectCallBack, marker);
                        }
                    }
                })(e.internalId, closestMarkers[c]);

                var td = document.createElement('td');
                td.innerHTML = '<svg width="30" height="30"><g><path fill="' + closestMarkers[c].icon.fillColor + '" d="' + closestMarkers[c].icon.path + '"/></g></svg>'
                tr.appendChild(td);

                var td = document.createElement('td');
                td.classList.add('fa', type, e.statusCode.code);
                td.onclick =  (function(id) { // closure to save current loop vars 
                    return function() {
                        var action = new BaseAction(type);
                        action.inject(id).done(function(entity) {
                            manageTask('btnManage', 'BtnManagement');
                        });
                    }
                })(e.internalId);
                tr.appendChild(td);

                var td = document.createElement('td');
                td.innerHTML = (e.nprotocollo ? e.nprotocollo : ' - ') + ' ' + formatISODate(e.data) + ' ' + e.internalId;
                tr.appendChild(td);

                tbody.appendChild(tr);
            }
        }
        var infowindow = new google.maps.InfoWindow({content: div});
        infowindow.open(map, marker);
    };

    var loadList = function(url) {
        return jQuery.ajax({
            type: 'GET', 
            url: url,
            success: function(response, textStatus, jqXHR) {
                self.entity = response;
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert('Error injecting ' + id + ' ' + textStatus + ' ' + errorThrown);
            }
        }).then(function(response, textStatus, jqXHR) {
            return response;
        });
    }
    
    var formatISODate = function(isoDateString) {
        if (isoDateString) {
            var date = new Date(Date.parse(isoDateString));
            return jQuery.datepicker.formatDate('dd/mm/yy', date);
        }
        return '';
    }

    /**
     * private print Addr
     */
    var printAddr = function(addr) {
        var content = '';
        if (addr) {
            if (addr.cty) {
                content += addr.cty + ' ';
            }
            if (addr.str) {
                content += addr.str + ' ';
            }
            if (addr.bnr) {
                content += addr.bnr;
            }
        }
        return content;
    }

}

/**
* static defaultPosition
*/
GoogleMapsUtils.defaultPosition = {lat: 45.4350168, lng: 12.329868};
/**
 * static getCurrentLocation method
 * Get current position with HTML5 geolocation, if unavailable return defaultPosition 
 */
GoogleMapsUtils.getCurrentLocation = function(callback) {
    var position = GoogleMapsUtils.defaultPosition;
    if(navigator.geolocation) {
       navigator.geolocation.getCurrentPosition(function(currentPos) {
            position = {lat: currentPos.coords.latitude, lng: currentPos.coords.longitude}
            callback(position);
        }, function(error) {
            console.error('Error getting location ' + error.code);
        },{timeout:5000});
     } else {
        console.error("This browser doesen't support geolocation.");    
     }
 }
