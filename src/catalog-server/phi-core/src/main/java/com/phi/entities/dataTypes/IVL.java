package com.phi.entities.dataTypes;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.hibernate.envers.Audited;

@Embeddable
@Audited
public class IVL<T> extends ANY implements Serializable {

	private static final long serialVersionUID = -3333282591360403879L;

	T low = null;
	T high = null;
	//Boolean lowClosed;
	//Boolean highClosed;

	public IVL() {
	}

	public T getLow() {
		return low;
	}

	public void setLow(T low) {
		this.low = low;
	}

	public T getHigh() {
		return high;
	}

	public void setHigh(T high) {
		this.high = high;
	}

	/*public Boolean getLowClosed() {
		return lowClosed;
	}

	public void setLowClosed(Boolean lowClosed) {
		this.lowClosed = lowClosed;
	}

	public Boolean getHighClosed() {
		return highClosed;
	}

	public void setHighClosed(Boolean highClosed) {
		this.highClosed = highClosed;
	}*/

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		if (low != null) {
			/*if (lowClosed != null) {
				if (lowClosed == true) {
					sb.append("[");
				} else {
					sb.append("]");
				}
			}*/
			sb.append(low.toString());
		}

		if (low != null || high != null) {
			sb.append(';');
		}

		if (high != null) {
			sb.append(high.toString());
			/*if (highClosed != null) {
				if (highClosed == true) {
					sb.append("]");
				} else {
					sb.append("[");
				}
			}*/
		}

		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((high == null) ? 0 : high.hashCode());
		result = prime * result;
				//+ ((highClosed == null) ? 0 : highClosed.hashCode());
		result = prime * result + ((low == null) ? 0 : low.hashCode());
		result = prime * result;
				//+ ((lowClosed == null) ? 0 : lowClosed.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IVL other = (IVL) obj;
		if (high == null) {
			if (other.high != null)
				return false;
		} else if (!high.equals(other.high))
			return false;
		/*if (highClosed == null) {
			if (other.highClosed != null)
				return false;
		} else if (!highClosed.equals(other.highClosed))
			return false;*/
		if (low == null) {
			if (other.low != null)
				return false;
		} else if (!low.equals(other.low))
			return false;
		/*if (lowClosed == null) {
			if (other.lowClosed != null)
				return false;
		} else if (!lowClosed.equals(other.lowClosed))
			return false;*/
		return true;
	}
	
	public IVL cloneIVL() {
		
		IVL i = new IVL();
		i.high=this.high;
		i.low=this.low;
		return i;
	}
	
	
//	/**
//	 * Return true if the current IVL is an IVL<TS> and the input Date is included in this interval, extreme included.
//	 * @param d
//	 * @return
//	 */
//	public boolean containsDate (Date d) {
//		return includeDateIVLTS (d,true);
//	}
//	
//	/**
//	 * Return true if the current IVL is an IVL<TS> and the input Date is included in this interval. 
//	 * the passed date can not be equal to the low or the high values, must be strictly greather and lower.
//	 * @param d
//	 * @return
//	 */
//	public boolean strictlyContainsDate (Date d) {
//		return includeDateIVLTS (d,false);
//	}
//	
//	/**
//	 * Return true if the current IVL is an interval of TS and the input IVL<TS> is in overlap with the current.
//	 * The two IVL (A,B) and (B,C) are not considered in overlap, so the method return false. 
//	 * @param d
//	 * @return
//	 */
//	public boolean isOverlapWith (IVL<TS> ivlts) {
//		return isOverlapWith(ivlts,true);
//	}
//	
//	/**
//	 * Return true if the current IVL is an interval of TS and the input IVL<TS> is in overlap with the current.
//	 * The two IVL (A,B) and (B,C) are considered in overlap, so the method return true. 
//	 * @param d
//	 * @return
//	 */
//	public boolean isStrictlyOverlapWith (IVL<TS> ivlts) {
//		return isOverlapWith(ivlts,false);
//	}
//	
//	
//	
//	private boolean includeDateIVLTS (Date d, boolean allowEquals) {
//		if (!(low instanceof TS && high instanceof TS)) 
//			return false;
//		
//		if (d==null )
//			return false;
//		if (low == null && high == null)
//			return false;
//		if (low == null) 
//			return isDateInRange(d, null, (TS)high, allowEquals );
//		
//		if (high == null) 
//			return isDateInRange(d, (TS)low, null, allowEquals );
//		
//		return isDateInRange(d, (TS)low, (TS)high, allowEquals );
//	}
//	
//	
//	/**
//	 * Return true if the current IVL is an IVL<TS> and the input IVL<TS> is in overlap with the current.
//	 * with allowEquals true, the  
//	 * @param d
//	 * @return
//	 */
//	private boolean isOverlapWith (IVL<TS> ivlts, boolean allowEquals) {
//		
//		if (ivlts == null)
//			return false;
//		
//		if (!(low instanceof TS && high instanceof TS)) 
//			return false;
//		
//		if (low == null && high == null)
//			return false;
//		
//		if (high == null) {
//			//this iterval is superiorly open (+<><>)
//			//check only the low value of ivlts is contained. 
//			if (ivlts.getLow() == null)
//				return false;
//			
//			if (this.includeDateIVLTS(ivlts.getLow().getDate(),allowEquals))
//				return true;
//			else
//				return false;
//		}
//		
//		if (low == null) {
//			//open interval inferiorly opern (-<><>)
//			//check only the high value of ivlts is contained. 
//			if (ivlts.getHigh() == null)
//				return false;
//			
//			if (this.includeDateIVLTS(ivlts.getHigh().getDate(),allowEquals))
//				return true;
//			else
//				return false;
//		}
//		
//		//this iterval is not open. 
//		//check only the low and the high value are not contained in the provided ivlts.
//		if (ivlts.includeDateIVLTS(((TS)low).getDate(),allowEquals) || ivlts.includeDateIVLTS(((TS)high).getDate(),allowEquals) ) {
//			return true;
//		}
//		
//		return false;
//	}
//	
//	/**
//	 * check date in inside interval. 
//	 * with allowEquals = true, the date can be one extreme of the interval. 
//	 * E.g.:  
//	 * with allowEquals = true,  Date A and IVL (A,B), the method return true.
//	 * with allowEquals = false, Date A and IVL (A,B), the method return false. (Date must be strictly internal)
//	 *   
//	 */
//	private boolean isDateInRange (Date d, TS lowVal, TS highVal, boolean allowEquals) {
//		if (d==null)
//			return false;
//		if (lowVal == null && highVal == null)
//			return false;
//		
//		Date startRange =null;
//		Date endRange = null;
//		
//		if (lowVal != null)
//			startRange = lowVal.getDate();
//		
//		if (highVal != null)
//			endRange = highVal.getDate();
//		
//		if (allowEquals) {
//			if (startRange==null && (d.before(endRange)|| (d.equals(endRange) )))
//				return true;
//			if (endRange == null && (d.after(startRange)|| d.equals(startRange)))
//				return true;
//			if ((d.after(startRange)|| d.equals(startRange)) && (d.before(endRange)|| d.equals(endRange)))
//				return true;
//		}
//		else {
//			if (startRange==null && d.before(endRange))
//				return true;
//			if (endRange == null && d.after(startRange))
//				return true;
//			if (d.after(startRange) && d.before(endRange) )
//				return true;
//		}
//		
//		return false;
//	}
}