export const environment = {
  production: false,
  hmr: false,
  apiUrl: 'http://localhost:8080/PHI_KLINIK/',
  /*apiUrl: 'http://10.172.44.238:8080/PHI_KLINIK/',*/
  languages: [['it', 'Italiano']/*, ['en', 'English']*//*, ['de', 'Deutsch']*/],
  home: 'dashboard/ambulatory-calendar',
  banner: {
    showPolicy:true,
    showCVC: false,
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
      }
    ]
  },
  openMenuAtHome: false,
  ambulatoryCalendar: {
    rootCtrl: false,
    alterColor: true,
    viewTherapist: false,
    useRead: false,
    startHour: 6,
    endHour: 20,
    startWorkHour: 7,
    endWorkHour: 19,
    minutesPerRow: 10,
    minutesPerRowShort: 10,
    minutesPerRowMedium: 10,
    minutesPerRowLong: 60,
    pixelPerMinute: 2,
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
    columns: 8,
    timeBands: true
  },
  ambulatoryPortal: {
    priorityShow: true,
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

    arrivedProcess: 'MOD_Outpatients/CORE/PROCESSES/Appointment/Link to previous events',
    takeInCharge: true,
    summary: true,
    registry: true,
    history: false,
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
    reportedCheck: true,
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
    showAdtData: false,
    showVitalSignAlert: false,
    report: 'reports/rDashAdt_worklist.seam',
    status: 'PHIDIC:EncounterStatus:new,active,held',
    selectedStatuses: ['active'],
    bradenOrNorton: undefined,
    bracialetReport: false,
    searchByBarcode: false,
    table: {
      bigIcons: true,
      note: false,
      appointmentDate: true,
      chart: false,
      tempWard: false,
      clinicalDiary: false,
      discharge: false,
      dischargeletter: false,
      nurseCheck: false,
      pain: false,
      injuryRisk: false,
      fallRisk: false,
      morse: false,
      referrer: false,
      invoiceClosed: false,
      sdoClosed: false
    },
    procedures: true,
    transfers: false,
    showWaitingProcIcon: false,
    refreshOnInit: false,
    menu: undefined
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
