export const environment = {
  production: false,
  hmr: false,
  apiUrl: 'http://localhost:8080/PHI_KLINIK/',
  /*apiUrl: 'http://phiapp-prod.dsc.local/PHI_KLINIK/',*/
languages: [ ['it', 'Italiano'] /* ['en', 'English']/* , ['de', 'Deutsch'] */],
  home: 'dashboard/ambulatory-calendar',
  banner: {
    showPolicy:true,
    startProcess: 'MOD_Patient/CORE/PROCESSES/PatientOverview',
    showLanguage: true,
    showAllergy: false,
    showConsent: true,
    showEncounter: false,
    showExtraInfo: true,
    confirmLogout: false,
    ieRepositoryViewer: false
  },
  manu: {
    processes: [
      {
        area: 'amb',
        path: '/dashboard/ambulatory-calendar',
        sortOrder: 0,
        text: 'Agenda ambulatoriale',
        type: 'd'
      },
      {
        area: 'amb',
        path: '/dashboard/ambulatory-portal',
        sortOrder: 1,
        text: 'Portale ambulatoriale',
        type: 'd'
      },
      {
        area: 'amb',
        path: '/dashboard/ps',
        sortOrder: 2,
        text: 'Portale emergenze',
        type: 'd'
      },
      {
        area: 'ci',
        path: '/dashboard/adt',
        sortOrder: 3,
        text: 'Portale di reparto',
        type: 'd'
      }
    ]
  },
  openMenuAtHome: false,
  ambulatoryCalendar: {
    rootCtrl: false,
    alterColor: true,
    viewTherapist: false,
    useRead: false,
    startHour: 0,
    endHour: 24,
    startWorkHour: 8,
    endWorkHour: 19,
    minutesPerRow: 10,
    minutesPerRowShort: 10,
    minutesPerRowMedium: 10,
    minutesPerRowLong: 60,
    pixelPerMinute: 1.2,
    moveSdl: true,
    saveFavoriteSdl: true,
    showPerformedProcedure: true,
    report: 'reports/rDash_worklist.seam',
    prefixedDuration: true,
    defaultDurationColor: true,
    dblClickProcess: 'MOD_Outpatients/CORE/PROCESSES/Appointment/ModifyAppointment',
    loadIsPsychooncology: false,
    colorizeHolidays: false,
    updateInternalActivitySdl: false,
    columns: 0, // auto resize
    timeBands: false
  },
  ambulatoryPortal: {
    priorityShow: false,
    useRead: true,
    status: 'PHIDIC:EncounterStatus',
    sentBy: 'PHIDIC:SentBy',
    inChargeTable: true,
    dblClickProcess: 'MOD_Clinical_Document/CORE/PROCESSES/Create ambulatory report',
    showAppointmentList: false,
    showFiscalCode: false,
    useGrouperId: true,
    selectedStatuses: ['new', 'active', 'terminated'],
    dateRange: true,
    requiredDeleteReason: false,
    showWaitingProcIcon: false
  },
  calendarMenu: {
    addAppointmentProcess: 'MOD_Outpatients/CORE/PROCESSES/Appointment/CreateNewAppointment',
    addCycleProcess: undefined,
    addIndirectProc: false,
    addActivity: false,
    addCoordination: false,
    addAppointmentNote: false,
    printColumn: false,
    addActivityWithoutProcedure: true
  },
  appointmentMenu: {
    showDetailsProcess: 'MOD_Outpatients/CORE/PROCESSES/Appointment/ModifyAppointment',
    select: false,
    edit: false,
    endCycle: false,
    noteHistory: true,

    arrivedProcess: 'MOD_Outpatients/CORE/PROCESSES/Appointment/GeneralAdmission',
    takeInCharge: true,
    summary: true,
    registry: true,
    history: true,
    episodeList: true,
    invoice: true,
    note: true,
    procedure: true,
    erogate: false,
    report: false,
    reputInWl: false,
    cancel: false,
    arrived: false,
    reopen: true,
    disableDeleteByRole: false,
    reportedCheck: false,
    fakeClassCode: true
  },
  appointmentEdit: {
    loadProcedureDefinitions: false
  },
  appointmentAction: {
    changeStatus: 'status',
    update: '',
    copy: '/copy',
    create: ''
  },
  adt: {
    action: 'adtportal',
    additionals: [/*'history',*/ 'procedures'],
    onlyWardChildren: true,
    showAdtWard: false,
    showAdtPatient: true,
    showAdtFilter: false,
    showAdtData: true,
    showVitalSignAlert: false,
    report: undefined,
    status: 'PHIDIC:EncounterStatus:completed,discharged,active,new,cancelled',
    selectedStatuses: ['new', 'active', 'discharged'],
    bradenOrNorton: undefined,
    bracialetReport: false,
    searchByBarcode: false,
    table: {
      bigIcons: true,
      note: false,
      appointmentDate: true,
      chart: false,
      tempWard: true,
      clinicalDiary: true,
      discharge: true,
      dischargeletter: false,
      nurseCheck: false,
      pain: false,
      injuryRisk: false,
      fallRisk: false,
      morse: false,
      referrer: true,
      invoiceClosed: true,
      sdoClosed: false
    },
    procedures: true,
    transfers: false,
    showWaitingProcIcon: false,
    refreshOnInit: true,
    menu: [
      { label: 'btn-menu-rgrstry', icon: 'fa-user', process: 'MOD_Patient/CORE/PROCESSES/View'
        /*, status: ['new', 'active', 'discharged', 'cancelled']*/},
      { label: 'btn-menu-actv', icon: 'app active', action: 'takeInCharge', status: ['new']},
      { label: 'btn-menu-del', icon: 'fa-window-close', popup: ['adt-cancel', ':encounterId', ':appointmentId'], status: ['new', 'active']},
      { label: 'btn-menu-rfrt', icon: 'fa-file-text-o', process: 'MOD_Clinical_Document/CORE/PROCESSES/Create ambulatory report',
        status: ['active', 'discharged']},
      { label: 'btn-menu-encdata', icon: 'fa-home', process: 'MOD_Outpatients/CORE/PROCESSES/DayHospital Enc/Dh Encounter Data',
        status: ['active', 'discharged']},
      { label: 'btn-menu-prcdr', icon: 'fa-cog', process: 'MOD_Outpatients/CORE/PROCESSES/Ambulatory Enc/Amb Encounter Active',
        status: ['active', 'discharged']},
      { label: 'DcAdtClnclDiry', icon: 'fa-address-book-o', process: 'MOD_Outpatients/CORE/PROCESSES/DayHospital Enc/Medical_diary',
        status: ['active', 'discharged']},
      { label: 'btn-menu-smmry', icon: 'fa-list-alt', process: 'MOD_Accounting/CORE/PROCESSES/Policy Mngt',
        status: ['active', 'discharged', 'completed']},
      { label: 'alt_btn-menu-nvc', icon: 'fa-eur', process: 'MOD_Accounting/CORE/PROCESSES/Invoicing/Search Invoice',
        status: ['active', 'discharged', 'completed']},
      { label: 'DcAdtDischarge', icon: 'fa-sign-out', action: 'discharge', status: ['active']},
      { label: 'btn-menu-reopen', icon: 'fa-folder-open',
        process: 'MOD_Outpatients/CORE/PROCESSES/DayHospital Enc/Dh Encounter Reopen', status: ['completed']},
      { label: 'DcAdtInvoiceClose', icon: 'fa-window-close-o', action: 'closeInvoicing', status: ['active', 'discharged'], disabled: 'invoicingclosed' }
    ]
  },
  nurseActivity: {
    onlyExecutions: true,
    clinicalDiary: false,
    drugs: false
  },
  lockedNodeProcess: undefined,
  sign: {
    basePath: undefined,
    format: '<FontSize>6</FontSize>',
    reason: 'Rappresentazione di un documento firmato elettronicamente, secondo la normativa vigente.\nIl documento � conservato secondo la normativa vigente.',
    location: 'Firmato in data',
    synchro: false
  },
  reportInNewTab: false,
  checkLockedStatus: false,
  options: {
    showComment: false
  }
};
