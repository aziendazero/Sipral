export interface CodeValue {
  changeReason?: string
  children?: Array<CodeValue>
  code?: string
  codeSystem?: any // FIXME: CodeSystem
  creator?: string
  currentTranslation?: string
  defaultChild?: boolean
  description?: string
  displayName?: string
  id?: string
  keywords?: string
  langDe?: string
  langEn?: string
  langIt?: string
  oid?: string
  parent?: CodeValue
  revisedDate?: Date
  revision?: any // FIXME: PhiRevisionEntity
  sequenceNumber?: number
  status?: number
  type?: string
  validFrom?: Date
  validTo?: Date
  version?: number

  //Proxy
  codeSystemName: string
  domainName: string
}
