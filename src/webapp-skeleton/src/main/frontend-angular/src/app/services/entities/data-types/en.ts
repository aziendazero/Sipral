import { CodeValue, IVL } from '.';

export interface EN {
  del?: string
  fam?: string
  formatted?: string
  giv?: string
  pfx?: string
  sfx?: string
  use ?: Set<CodeValue>
  useablePeriod ?: IVL<Date>
}
