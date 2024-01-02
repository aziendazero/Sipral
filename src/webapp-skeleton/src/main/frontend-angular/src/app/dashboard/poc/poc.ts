import { Component, Injector, OnInit, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { select } from '@angular-redux/store';
import { BaseChartDirective } from 'ng2-charts';

import { BaseForm } from '../../widgets/form/base-form';
import { PatientEncounterActionService, VitalSignActionService, ClinicalDiaryActionService } from '../../services/actions';
import { PatientEncounter } from '../../services/entities/act';
import { Datamodel } from '../../services/datamodel/datamodel';
import { WidgetEvent } from '../../widgets/event/widget-event';
import { SelectItem } from '../../services/datamodel/select-item';
import { DateFormatPipe } from '../../services/converters/date-format.pipe';


@Component({
  selector: 'phi-poc',
  templateUrl: './poc.html',
  styleUrls: ['./poc.scss']
})
export class Poc extends BaseForm implements OnInit {
  @select(['conversation']) conversation$;

  PatientEncounter: PatientEncounter;
  ClinicalDiaryList: Datamodel;

  @ViewChild(BaseChartDirective)
  public chart: BaseChartDirective;

  startDate: Date;
  endDate: Date;

  from: Date;
  to: Date;

  measureType: Array<any> = [
    { label: 'PA [mmHG]', value: 'PA' },
    { label: 'SPO2 [%]', value: 'SPO2' },
    { label: 'FC [bpm]', value: 'FC' },
    { label: 'T [°C]', value: 'T' },
    { label: 'GLC [mg/dl]', value: 'DLC' },
    { label: 'Diuresi [ml/24h]', value: 'DIURESIS' },
    { label: 'Dolore', value: 'PAIN' },
    { label: 'Peso', value: 'WEIGHT' },
    { label: 'Altezza', value: 'HEIGHT' }
  ];

  selectedMultichannel = this.measureType.map((m: SelectItem) => m.value);
  selectedMeasures = this.measureType.map((m: SelectItem) => m.value);
  selectedSingleMeasure = 'PA';

  series = {
    PA: [{ label: 'Sistolica', data: [], hidden: false, fill: false },
    { label: 'Diastolica', data: [], hidden: false, fill: false }],
    SPO2: [{ label: 'SPO2 [%]', data: [], hidden: false, fill: false }],
    FC: [{ label: 'FC [bpm]', data: [], hidden: false, fill: false }],
    T: [{ label: 'T [°C]', data: [], hidden: false, fill: false }],
    DLC: [{ label: 'GLC [mg/dl]', data: [], hidden: false, fill: false }],
    DIURESIS: [{ label: 'Diuresi [ml/24h]', data: [], hidden: false, fill: false }],
    PAIN: [{ label: 'Dolore', data: [], hidden: false, fill: false }],
    WEIGHT: [{ label: 'Peso', data: [], hidden: false, fill: false }],
    HEIGHT: [{ label: 'Altezza', data: [], hidden: false, fill: false }]
  };

  // lineChart
  public multichannelChartData: Array<any>
    = [
      this.series.PA[0],
      this.series.PA[1],
      this.series.SPO2[0],
      this.series.FC[0],
      this.series.T[0],
      this.series.DLC[0],
      this.series.DIURESIS[0],
      this.series.PAIN[0],
      this.series.WEIGHT[0],
      this.series.HEIGHT[0],
    ];

  public paChartData: Array<any>
    = [
      this.series.PA[0],
      this.series.PA[1]
    ];

  public spo2ChartData: Array<any>
    = [
      this.series.SPO2[0]
    ];

  public fcChartData: Array<any>
    = [
      this.series.FC[0]
    ];

  public tChartData: Array<any>
    = [
      this.series.T[0]
    ];

  public dlcChartData: Array<any>
    = [
      this.series.DLC[0]
    ];

  public diuresisChartData: Array<any>
    = [
      this.series.DIURESIS[0]
    ];

  public painChartData: Array<any>
    = [
      this.series.PAIN[0]
    ];

  public weightChartData: Array<any>
    = [
      this.series.WEIGHT[0]
    ];

  public heightChartData: Array<any>
    = [
      this.series.HEIGHT[0]
    ];


  public singleLineChartData: Array<any>; // = Object.assign([], this.lineChartData);

  public lineChartLabels: Array<any>;

  public lineChartOptionsCustomRatio: any = {
    responsive: true,
    maintainAspectRatio: false
  };

  public lineChartOptions: any = {
    responsive: true
  };

  public lineChartColors: Array<any> = [
    // { // grey
    //   backgroundColor: 'rgba(148,159,177,0.2)',
    //   borderColor: 'rgba(148,159,177,1)',
    //   pointBackgroundColor: 'rgba(148,159,177,1)',
    //   pointBorderColor: '#fff',
    //   pointHoverBackgroundColor: '#fff',
    //   pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    // },
    // { // dark grey
    //   backgroundColor: 'rgba(77,83,96,0.2)',
    //   borderColor: 'rgba(77,83,96,1)',
    //   pointBackgroundColor: 'rgba(77,83,96,1)',
    //   pointBorderColor: '#fff',
    //   pointHoverBackgroundColor: '#fff',
    //   pointHoverBorderColor: 'rgba(77,83,96,1)'
    // },
    // { // grey
    //   backgroundColor: 'rgba(148,159,177,0.2)',
    //   borderColor: 'rgba(148,159,177,1)',
    //   pointBackgroundColor: 'rgba(148,159,177,1)',
    //   pointBorderColor: '#fff',
    //   pointHoverBackgroundColor: '#fff',
    //   pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    // }
  ];
  public lineChartLegend = true;
  public lineChartType = 'line';

  constructor(injector: Injector,
    private router: Router,
    private route: ActivatedRoute,
    private PatientEncounterAction: PatientEncounterActionService,
    public VitalSignAction: VitalSignActionService,
    private ClinicalDiaryAction: ClinicalDiaryActionService,
    private datePipe: DateFormatPipe) {
    super(injector);

    this.conversation$.subscribe(res => {
      this.PatientEncounter = res.PatientEncounter;
      this.ClinicalDiaryList = res.ClinicalDiaryList;
    });

    this.singleLineChartData = this.multichannelChartData.map((serie, i) => {
      const cloneSerie = Object.assign([], serie);
      if (i >= 2) {
        cloneSerie.hidden = true;
      }
      return cloneSerie;
    });

    let today: Date = new Date();
    let halfMonthAgo: Date = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 15);
    let oneMonthsAgo: Date = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());

    this.startDate = oneMonthsAgo;
    this.from = halfMonthAgo;
    this.to = today;
    this.endDate = today;
  }

  ngOnInit() {
    if (this.PatientEncounter) {
      this.initRestrictions();
      this.refresh();
    } else {
      this.router.navigate(['/dashboard', 'adt']);
    }
  }

  refresh() {
    this.readVitalSign();
    this.readClinicalDiary();
  }

  initRestrictions() {
    this.VitalSignAction.select.push('evaluationDate');

    // PA
    this.VitalSignAction.select.push('systolic.value');
    this.VitalSignAction.select.push('diastolic.value');
    // SPO2
    this.VitalSignAction.select.push('o2Saturation.value');
    // FC
    this.VitalSignAction.select.push('cardiacFrequency.value');
    // T
    this.VitalSignAction.select.push('bodyTemperature.value');
    // GLC
    this.VitalSignAction.select.push('glycemia.value');
    // Diuresi
    // this.VitalSignAction.select.push('glycemia.value');
    // Dolore
    this.VitalSignAction.select.push('pain.value');
    // Peso
    this.VitalSignAction.select.push('weight.value');
    // Altezza
    this.VitalSignAction.select.push('height.value');


    this.VitalSignAction.equal['patient.internalId'] = this.PatientEncounter.patient.internalId;
    this.VitalSignAction.equal['isActive'] = true;

    this.VitalSignAction.orderBy['evaluationDate'] = 'ascending';
  }

  readVitalSign(ev = null) {

    if (ev && ev.highValue) {
      this.to = ev.highValue;
    }

    // console.log(this.from + ' - ' + this.to);

    this.VitalSignAction.greaterEqual['evaluationDate'] = this.from;
    this.VitalSignAction.less['evaluationDate'] = new Date(this.to.getFullYear(), this.to.getMonth(), this.to.getDate() + 1);


    this.VitalSignAction.read().then((dm: Datamodel) => {

      this.lineChartLabels = [];
      this.resetData();

      const series: Array<any> = dm.entities.map((row) => {
        const evaluationDate: string = this.datePipe.transform(row.evaluationDate, 'short');
        this.lineChartLabels.push(evaluationDate);

        if (row.systolic.value) {
          this.series.PA[0].data.push(row.systolic.value);
        }
        if (row.systolic.value) {
          this.series.PA[1].data.push(row.diastolic.value);
        }
        if (row.o2Saturation.value) {
          this.series.SPO2[0].data.push(row.o2Saturation.value);
        }

        if (row.cardiacFrequency.value) {
          this.series.FC[0].data.push(row.cardiacFrequency.value);
        }
        if (row.bodyTemperature.value) {
          this.series.T[0].data.push(row.bodyTemperature.value);
        }
        if (row.glycemia.value) {
          this.series.DLC[0].data.push(row.glycemia.value);
        }
        // DIURESIS
        if (row.pain.value) {
          this.series.PAIN[0].data.push(row.pain.value);
        }
        if (row.weight.value) {
          this.series.WEIGHT[0].data.push(row.weight.value);
        }
        if (row.height.value) {
          this.series.HEIGHT[0].data.push(row.height.value);
        }

      });
      this.updateSingleLineChartData();
    });
  }

  multichannelMeasureChanged(event: WidgetEvent) {

    const series: Array<any> = this.series[event.element];

    series.map((serie) => {
      let serieIndex = this.multichannelChartData.indexOf(serie);
      let meta = this.chart.chart.getDatasetMeta(serieIndex);
      meta.hidden = !event.selected;
      this.multichannelChartData[serieIndex].hidden = !event.selected;
    });

    this.multichannelChartData = this.multichannelChartData.slice();
    this.selectedMeasures = this.selectedMultichannel;
  }

  measuresChanged(event: WidgetEvent) {
    const series: Array<any> = this.series[event.element];

    series.map((serie) => {
      let serieIndex = this.multichannelChartData.indexOf(serie);
      this.multichannelChartData[serieIndex].hidden = !event.selected;
    });

    this.multichannelChartData = this.multichannelChartData.slice();
    this.selectedMultichannel = this.selectedMeasures;
  }

  singleMeasureChanged(event: WidgetEvent) {
    const selectedSeries = this.series[event.value];

    this.singleLineChartData.map((serie, i) => {
      let meta = this.chart.chart.getDatasetMeta(i);
      meta.hidden = true;
      serie.hidden = true;
      selectedSeries.map((selectedSerie) => {
        if (selectedSerie.label === serie.label) {
          meta.hidden = false;
          serie.hidden = false;
        }
      });
    });

    this.singleLineChartData = this.singleLineChartData.slice();
  }

  updateSingleLineChartData() {
    this.singleLineChartData.map((serie, i) => {
      serie.data = this.multichannelChartData[i].data;
    });
  }

  onMultichannelSelectAll() {
    this.multichannelChartData.map((serie, i) => {
      let meta = this.chart.chart.getDatasetMeta(i);
      meta.hidden = false;
    });
    this.multichannelChartData = this.multichannelChartData.slice();

    this.selectedMultichannel = this.measureType.map((m: SelectItem) => m.value);
  }

  onMultichannelDeselectAll() {
    this.multichannelChartData.map((serie, i) => {
      let meta = this.chart.chart.getDatasetMeta(i);
      meta.hidden = true;
    });
    this.multichannelChartData = this.multichannelChartData.slice();

    this.selectedMultichannel = [];
  }

  onSelectAll() {
    this.selectedMeasures = this.measureType.map((m: SelectItem) => m.value);
  }

  onDeselectAll() {
    this.selectedMeasures = []
  }

  showMeasure(measure: string) {
    return this.selectedMeasures.indexOf(measure) !== -1;
  }


  readClinicalDiary() {
    this.ClinicalDiaryAction.select.push('author.name.fam');
    this.ClinicalDiaryAction.select.push('author.name.giv');
    this.ClinicalDiaryAction.select.push('text');
    this.ClinicalDiaryAction.select.push('availabilityTime');

    this.ClinicalDiaryAction.equal['patientEncounter.internalId'] = this.PatientEncounter.internalId;

    // this.ClinicalDiaryAction.orderBy['cancellationdate'] = 'ASC';
    this.ClinicalDiaryAction.orderBy['availabilityTime'] = 'descending';

    this.ClinicalDiaryAction.read();
  }

  // events
  public chartClicked(e: any): void {
    console.log(e);
  }

  public chartHovered(e: any): void {
    console.log(e);
  }

  private resetData() {
    this.series.PA[0].data = [];
    this.series.PA[1].data = [];
    this.series.SPO2[0].data = [];
    this.series.FC[0].data = [];
    this.series.T[0].data = [];
    this.series.DLC[0].data = [];
    this.series.DIURESIS[0].data = [];
    this.series.PAIN[0].data = [];
    this.series.WEIGHT[0].data = [];
    this.series.HEIGHT[0].data = [];
  }
}
