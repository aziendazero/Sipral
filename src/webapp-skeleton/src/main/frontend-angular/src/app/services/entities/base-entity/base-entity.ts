export interface BaseEntity {
  createdBy?: string
  creationDate?: Date
  internalId?: number
  isActive?: boolean
  revision?:  any // FIXME?: PhiRevisionEntity
  version?: number

  //Proxy
  entityName?: string;
}
