package com.phi.db.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.db.importer.EntityManagerUtilities;
import com.phi.entities.role.ServiceDeliveryLocation;


public class SdlocToNestedSet  extends EntityManagerUtilities{
	
	private static final Logger log = Logger.getLogger(SdlocToNestedSet.class);
	
	private static HashMap<Long, Long> childrenNumber = new HashMap<Long, Long>();
	private static HashMap<Long, Long[]> sdlLeftRight = new HashMap<Long, Long[]>();
	private static HashMap<Long, Long> sdlGrandParent = new HashMap<Long, Long>();
	private static ChildComparator comp = new ChildComparator();
	public SdlocToNestedSet() {
		
	}
	
	public SdlocToNestedSet(EntityManager em2) {
		this.em=em2;
	}


	public static void main (String[] args) {
		System.out.println("STARTTTT");
//		emf2  = setupEm2(dbPropertiesFile);
//		EntityManager em2 = emf2.createEntityManager();
		
		
		SdlocToNestedSet sdlocToNestedSet=new SdlocToNestedSet();
		sdlocToNestedSet.setupEntityManagerFactory();
		sdlocToNestedSet.setupEm();
		sdlocToNestedSet.fixSiblings();
		
		sdlocToNestedSet.closeEm();
		sdlocToNestedSet.closeResource();
		System.out.println("Completed.");
	}


	public void fixSiblings() {
		String testQuery="SELECT sdl FROM ServiceDeliveryLocation sdl";
		Query q = em.createQuery(testQuery);
		@SuppressWarnings("unchecked")
		List<ServiceDeliveryLocation> result = (List<ServiceDeliveryLocation>)q.getResultList();
		
		System.out.println(" Found "+result.size()+" SDL");
		if (result.size() < 1){
			System.out.println("No Sdl to be updated");
			System.exit(0);
		}
		List<ServiceDeliveryLocation> sdlList = (List<ServiceDeliveryLocation> ) result;
		List<ServiceDeliveryLocation> rootSdlList = new ArrayList<ServiceDeliveryLocation>();
		
		
		String rootSdlListId="";
		for (ServiceDeliveryLocation sdl : sdlList) {
			long internalId = sdl.getInternalId();
			
			//first find roots sdl (the ones without parent_id)
			if (sdl.getParent() == null ) {
				rootSdlList.add(sdl);
				rootSdlListId+=internalId+", ";
			}
			
			//calculate children number for all SDL:
			calculateChildrenNumber(sdl);
			System.out.println(internalId + ": "+childrenNumber.get(internalId) + " childs.");
		}
		System.out.println("Root SDL are: "+rootSdlListId.substring(0,rootSdlListId.length()-2));
		
		
		for (ServiceDeliveryLocation rootSdl : rootSdlList) {
			Long rootleft = 1L;
			Long rootRight = 2*childrenNumber.get(rootSdl.getInternalId());
			List<ServiceDeliveryLocation> rootSdlElement = new ArrayList<ServiceDeliveryLocation>();
			rootSdlElement.add(rootSdl);
			calculateLeftRight(rootSdlElement,rootleft,rootRight);
		}
	}
	
	
	private void calculateLeftRight(List<ServiceDeliveryLocation> listSDL, Long minChildLeft, Long maxChildRight) {
		
		Long left = minChildLeft;
		Long right = maxChildRight;
		Collections.sort(listSDL, comp);		
		for (ServiceDeliveryLocation sdl : listSDL) {
			long internalId = sdl.getInternalId();
			Long numberOfChild = childrenNumber.get(internalId);
			right = left+2*numberOfChild+1;
			Long leftRight[] = new Long[]{left,right};
			sdlLeftRight.put(internalId, leftRight);
			Long grandParent= getRootParentId(sdl);
			sdlGrandParent.put(internalId, grandParent);
			//System.out.println("id: "+internalId+" left="+left+" right="+right + " rootId=" +grandParent);
			
			//System.out.println("Update Service_delivery_location set NS_LEFT="+left+", NS_RIGHT="+right+", NS_THREAD="+grandParent+" where internal_id="+internalId);
			String query = "Update ServiceDeliveryLocation sdl set sdl.nodeInfo.nsLeft="+left+", sdl.nodeInfo.nsRight="+right+", sdl.nodeInfo.nsThread="+grandParent+" where internalId="+internalId;
			//System.out.println(query);

			//ServiceDeliveryLocationNestedSetNodeInfo nodeInfo = new ServiceDeliveryLocationNestedSetNodeInfo();
//			nodeInfo.setNsLeft(left);
//			nodeInfo.setNsRight(right);
//			nodeInfo.setNsThread(grandParent);
//			
//			sdl.setNodeInfo(nodeInfo);
//			em2.persist(sdl);
			
			em.getTransaction().begin();
			
			Query q = em.createQuery(query);
			q.executeUpdate();
			
			em.getTransaction().commit();
			//if ("admin".equals(sdl.getCreatedBy())) sdl.setCreatedBy("admin1");  //to be removed
			
			if (numberOfChild > 0) {
				calculateLeftRight(sdl.getChildren(), left+1, right-1);
			}
			
			left = right+1;
			
		}
		//System.out.println("called with left="+minChildLeft+" right="+maxChildRight+" finally right="+right);
	}
	
	
	private Long calculateChildrenNumber (ServiceDeliveryLocation sdl) {
		Long sdlInternalId = sdl.getInternalId();
		if (childrenNumber.containsKey(sdlInternalId)) {
			return  childrenNumber.get(sdlInternalId);
		}
		
		if (sdl.getChildren() == null || sdl.getChildren().size() == 0) {
			childrenNumber.put(sdlInternalId, 0L);
			return 0L;
		}
		
		Long ret=0L;
		for (ServiceDeliveryLocation child : sdl.getChildren()) {
			ret +=1+calculateChildrenNumber(child);
		}
		childrenNumber.put(sdlInternalId, ret);
		return ret;
		
	}
	
	private long getRootParentId (ServiceDeliveryLocation sdl) {
		if (sdl.getParent() == null)
			return sdl.getInternalId();
		ServiceDeliveryLocation tmpSdl = sdl;
		while (tmpSdl.getParent() != null) {
			tmpSdl=tmpSdl.getParent();
		}
		return tmpSdl.getInternalId();
		
	}
	
	private static class ChildComparator implements Comparator<ServiceDeliveryLocation>{

		@Override
		public int compare(ServiceDeliveryLocation o1,
				ServiceDeliveryLocation o2) {

			long n1 = childrenNumber.get(o1.getInternalId());
			long n2 = childrenNumber.get(o2.getInternalId());
			
			if(n1<n2)
				return 1;
			else if(n1>n2)
				return -1;
			else
				return 0;
		}
		
	}
}
