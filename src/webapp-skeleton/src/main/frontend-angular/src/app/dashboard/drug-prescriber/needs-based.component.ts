import {Component, Injector, OnInit, ViewChild} from '@angular/core';
import { select } from '@angular-redux/store';
import { BaseForm } from '../../widgets/form/base-form';
import { Prescription } from '../../services/entities/base-entity';
import { ServiceDeliveryLocation } from '../../services/entities/role';
import { SelectItem } from '../../services/datamodel/select-item';
import { CodeValue } from '../../services/entities/data-types/code-value';
import { CodeValueProxy } from '../../services/proxyes/code-value-proxy';
import { WidgetEvent } from '../../widgets/event/widget-event';
import { ViewManager } from '../../services/view-manager';
import { DictionaryManager } from '../../services/dictionary-manager';
import { SliderComponent } from '../../widgets/slider.component';

@Component({
  templateUrl: './needs-based.html',
  styles: ['.phi-combobox {min-width: 150px;}']
})
export class NeedsBasedComponent extends BaseForm implements OnInit {

  @select(['conversation', 'ServiceDeliveryLocation']) ServiceDeliveryLocation$;
  ServiceDeliveryLocationSub;
  ServiceDeliveryLocation: ServiceDeliveryLocation;

  @select(['conversation', 'Prescription']) Prescription$;
  PrescriptionSub;
  Prescription: Prescription;

  dateGap: Date;

  defaultPainType;

  @ViewChild('SliderNdsBsdPain')
  sliderNdsBsdPain: SliderComponent;

  painRange: Array<SelectItem> =  [{label: '[0,10]', value: 'NRS'}, {label: '[1,5]', value: 'VRS'}];

  constructor(injector: Injector, private viewManager: ViewManager, private dictionaryManager: DictionaryManager) {
    super(injector);

    this.ServiceDeliveryLocationSub = this.ServiceDeliveryLocation$.subscribe(res => {
      this.ServiceDeliveryLocation = res;
    });
    this.PrescriptionSub = this.Prescription$.subscribe(res => {
      this.Prescription = res;
      if (this.Prescription && this.Prescription.hoursGap && this.Prescription.minsGap) {
        this.dateGap = new Date(0);
        this.dateGap.setHours(+this.Prescription.hoursGap);
        this.dateGap.setMinutes(+this.Prescription.minsGap);
      }
    });
  }


  ngOnInit() {

    //FIXME add BPS only if ServiceDeliveryLocation.vitalSignForm is INTENSIVECARE and patient has an active BPS scale
    //FIXME Read scale
    if (this.ServiceDeliveryLocation && this.ServiceDeliveryLocation.vitalSignForm) {
      let vsf: CodeValue | CodeValueProxy = this.ServiceDeliveryLocation.vitalSignForm;

      if (!vsf.code) {
        this.dictionaryManager.getById(vsf.id).then((cv: CodeValue) => {
          if (vsf.code === 'INTENSIVECARE') {
            this.painRange.push({label: '[3,12]', value: 'BPS'});
          }
        });
      } else {
        if (vsf.code === 'INTENSIVECARE') {
          this.painRange.push({label: '[3,12]', value: 'BPS'});
        }
      }
    }
    if (this.ServiceDeliveryLocation && this.ServiceDeliveryLocation.defaultPainType) {
      let painType: CodeValue | CodeValueProxy = this.ServiceDeliveryLocation.defaultPainType;

      if (!painType.code) {
        this.dictionaryManager.getById(painType.id).then((cv: CodeValue) => {
          this.defaultPainType = cv.code;
          this.setPainTreshold(this.defaultPainType);
        });
      } else {
        this.defaultPainType = painType.code;
        this.setPainTreshold(this.defaultPainType);
      }
    }

    //FIXME WHEN WIDGETS WILL CREATE PATH REMOVE THIS
    if (!this.Prescription.maximumQuantity24h) {
      this.Prescription.maximumQuantity24h = {};
    }
    if (!this.Prescription.systolicPressure) {
      this.Prescription.systolicPressure = {};
    }
    if (!this.Prescription.diastolicPressure) {
      this.Prescription.diastolicPressure = {};
    }
    if (!this.Prescription.temperature) {
      this.Prescription.temperature = {};
    }
    if (!this.Prescription.glycemia) {
      this.Prescription.glycemia = {};
    }
    if (!this.Prescription.diuresis) {
      this.Prescription.diuresis = {};
    }
    if (!this.Prescription.pain) {
      this.Prescription.pain = {};
    }
    if (!this.Prescription.heartRate) {
      this.Prescription.heartRate = {};
    }
    if (!this.Prescription.spo2) {
      this.Prescription.spo2 = {};
    }
  }

  ngOnDestroy() {
    this.ServiceDeliveryLocationSub.unsubscribe();
    this.PrescriptionSub.unsubscribe();
  }

  onGlySecondaryProtocolChange(event: WidgetEvent){
    if (event.value) {
      delete this.Prescription.glycemiaThreshold;
      this.Prescription.glycemia = {};
    }
  }

  setPainTreshold(painType) {
    if (painType === 'NRS') {
      this.sliderNdsBsdPain.minimum = '0';
      this.sliderNdsBsdPain.maximum = '10';
    } else if (painType === 'VRS') {
      this.sliderNdsBsdPain.minimum = '1';
      this.sliderNdsBsdPain.maximum = '5';
    } else if (painType === 'BPS') {
      this.sliderNdsBsdPain.minimum = '3';
      this.sliderNdsBsdPain.maximum = '12';
    }
  }

  save() {

    this.returnToPrescription().then(() => {

      if (this.Prescription) {
        if (this.dateGap) {
          this.Prescription.hoursGap = this.dateGap.getHours().toString();
          this.Prescription.minsGap = this.dateGap.getMinutes().toString();
        } else {
          delete this.Prescription.hoursGap;
          delete this.Prescription.minsGap;
        }
      }

      //FIXME WHEN WIDGETS WILL CREATE PATH REMOVE THIS
      if (!this.Prescription.maximumQuantity24h.value) {
        delete this.Prescription.maximumQuantity24h;
      }
      if (this.Prescription.systolicPressure.value) {
        this.Prescription.systolicPressure.unit = 'mm[Hg]';
      } else {
        delete this.Prescription.systolicPressure;
      }
      if (this.Prescription.diastolicPressure.value) {
        this.Prescription.diastolicPressure.unit = 'mm[Hg]';
      } else {
        delete this.Prescription.diastolicPressure;
      }
      if (this.Prescription.temperature.value) {
        this.Prescription.temperature.unit = '°C';
      } else {
        delete this.Prescription.temperature;
      }
      if (this.Prescription.glycemia.value) {
        this.Prescription.glycemia.unit = 'mg/dl';
      } else {
        delete this.Prescription.glycemia;
      }
      if (this.Prescription.diuresis.value) {
        this.Prescription.diuresis.unit = 'ml/24h';
      } else {
        delete this.Prescription.diuresis;
      }
      if (this.Prescription.pain.value) {
        this.Prescription.pain.unit = '-';
      } else {
        delete this.Prescription.pain;
      }
      if (this.Prescription.heartRate.value) {
        this.Prescription.heartRate.unit = 'bpm';
      } else {
        delete this.Prescription.heartRate;
      }
      if (this.Prescription.spo2.value) {
        this.Prescription.spo2.unit = 'SPO2';
      } else {
        delete this.Prescription.spo2;
      }

    });
  }

  returnToPrescription(): Promise<any> {
    return this.viewManager.setPopupViewId('prescription');
  }

  cancelNdsBsdSist(){
    if(this.Prescription){
      delete this.Prescription.systolicPressureThreshold;
      this.Prescription.systolicPressure = {};
    }
  }

  cancelNdsBsdDia(){
    if(this.Prescription){
      delete this.Prescription.diastolicPressureThreshold;
      this.Prescription.diastolicPressure = {};
    }
  }

  cancelNdsBsdTmp(){
    if(this.Prescription){
      delete this.Prescription.temperatureThreshold;
      this.Prescription.temperature = {};
    }
  }

  cancelNdsBsdGly(){
    if(this.Prescription){
      delete this.Prescription.glycemiaThreshold;
      this.Prescription.glycemia = {};
    }
  }

  cancelNdsBsdDrs(){
    if(this.Prescription){
      delete this.Prescription.diuresisThreshold;
      this.Prescription.diuresis = {};
    }
  }

  cancelNdsBsdPain(){
    if(this.Prescription){
      delete this.Prescription.painThreshold;
      this.Prescription.pain = {};
    }
  }

  cancelNdsBsdHrtRt(){
    if(this.Prescription){
      delete this.Prescription.heartRateThreshold;
      this.Prescription.heartRate = {};
    }
  }

  cancelNdsBsdSpo2(){
    if(this.Prescription){
      delete this.Prescription.spo2Threshold;
      this.Prescription.spo2 = {};
    }
  }

}
