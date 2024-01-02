import { BaseEntity } from '../base-entity';
import { Manufacturer, Substance } from './index';

import { CodeValueCodifa, CodeValuePhi, EN, IVL } from '../data-types';

export interface Medicine extends BaseEntity {
  aifaNote: Array<CodeValuePhi>
  atcCode: CodeValueCodifa
  box: CodeValueCodifa
  codifaId: string
  concessionPeriod: IVL<Date>
  dosageInstruction: string
  eqGroupCode: CodeValueCodifa
  expiringCode: CodeValueCodifa
  externalId: string
  freezeCauseCode: CodeValueCodifa
  freezeDate: Date
  goodsClassId: string
  governmentId: string
  lifePeriod: string
  manufacturer: Manufacturer
  name: EN
  pharmaceuticFormCode: CodeValueCodifa
  price: number // double
  productClassification: CodeValueCodifa
  quantityPerBox: string
  quantityPerBoxUnit: CodeValueCodifa
  recipeCode: CodeValueCodifa
  reference: boolean
  regionalId: string
  routeCode: CodeValueCodifa
  ssnCode: CodeValueCodifa
  substance: Array<Substance>
  temperatureStorageCode: CodeValueCodifa
  tempMax: number // Double
  tempMin: number // Double
  terapeuticGroup: CodeValueCodifa
  usageType: CodeValueCodifa
}
