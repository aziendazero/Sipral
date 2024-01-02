export const environment = {
  production: true,
  hmr: false,
  apiUrl: '/PHI_KLINIK/',
  languages: [['it', 'Italiano']/*, ['en', 'English']*/, ['de', 'Deutsch']],
  home: 'dashboard/ambulatory-calendar',
  banner: {
    showPolicy:true,
    startProcess: 'MOD_Patient/CORE/PROCESSES/PatientOverview',
    showLanguage: true,
    showAllergy: true,
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
        area: 'ci',
        path: '/dashboard/adt',
        sortOrder: 2,
        text: 'Portale di reparto',
        type: 'd'
      },
      {
        area: 'ci',
        path: '/dashboard/activity-prescriber',
        sortOrder: 3,
        text: 'Pianificazione infermieristica',
        type: 'd'
      },
      {
        area: 'ci',
        path: '/dashboard/nurse-activity',
        sortOrder: 4,
        text: 'Lista di attività',
        type: 'd'
      }
    ]
  },
  openMenuAtHome: true,
  ambulatoryCalendar: {
    rootCtrl: false,
    alterColor: true,
    viewTherapist: true,
    useRead: false,
    startHour: 6,
    endHour: 20,
    startWorkHour: 7,
    endWorkHour: 19,
    minutesPerRow: 15,
    minutesPerRowShort: 15,
    minutesPerRowMedium: 15,
    minutesPerRowLong: 60,
    pixelPerMinute: 2,
    moveSdl: true,
    saveFavoriteSdl: true,
    showPerformedProcedure: true,
    report: 'reports/rDash_worklist.seam',
    prefixedDuration: false,
    defaultDurationColor: true,
    dblClickProcess: 'MOD_Outpatients/CORE/PROCESSES/Appointment/ModifyAppointment',
    loadIsPsychooncology: false,
    colorizeHolidays: false,
    updateInternalActivitySdl: false,
    columns: 10,
    timeBands: true
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
    dateRange: false,
    requiredDeleteReason: true,
    showWaitingProcIcon: false
  },
  calendarMenu: {
    addAppointmentProcess: 'MOD_Outpatients/CORE/PROCESSES/Appointment/CreateNewAppointment',
    addCycleProcess: 'MOD_Outpatients/CORE/PROCESSES/Appointment/CreateNewCycle',
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
    action: 'adt',
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
    bradenOrNorton: 'Braden',
    bracialetReport: false,
    searchByBarcode: false,
    table: {
      bigIcons: false,
      note: false,
      appointmentDate: false,
      chart: false,
      tempWard: true,
      clinicalDiary: true,
      discharge: true,
      dischargeletter: true,
      nurseCheck: true,
      pain: true,
      injuryRisk: true,
      fallRisk: false,
      morse: true,
      referrer: false,
      invoiceClosed: false,
      sdoClosed: false
    },
    procedures: true,
    transfers: false,
    showWaitingProcIcon: false,
    refreshOnInit: true,
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
    reason: 'Rappresentazione di un documento firmato elettronicamente, secondo la normativa vigente.\nIl documento è conservato secondo la normativa vigente.',
    location: 'Firmato in data',
    synchro: false
  },
  reportInNewTab: false,
  checkLockedStatus: false,
  options: {
    showComment: false
  }
};
