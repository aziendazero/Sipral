package com.phi.cs.paging;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.CriteriaImpl.OrderEntry;
import org.hibernate.impl.CriteriaImpl.Subcriteria;
import org.jboss.seam.core.Conversation;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.resultTransformer.AliasToEntityMapResultTransformer;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PhiException;
import com.phi.entities.actions.BaseAction.OrderBy;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.II;


/**
 * Lazy list 4 pagination of dataTables. It loads one page for each fetch miss.
 * @author zupan
 *
 * @param <T>
 */
public class LazyList<T> extends AbstractList<T> implements Serializable {

	private static final long serialVersionUID = -4024021276036690793L;

	private final static Logger log = Logger.getLogger(LazyList.class);

	/** backing Criteria query */
	protected Criteria criteria;

	/** backing Projection List */
	protected ProjectionList projections;

	protected boolean isDistinct = false;

	protected Class<T> entityClass;

	/** cache of loaded items */
	protected Map<Integer, T> loaded;

	/** total number of results expected */
	protected long numResults = Integer.MAX_VALUE;

	/** number of results to fetch on cache miss */
	protected int pageSize;

	/** READ without count if numResultsUnknown is true */
	protected boolean numResultsUnknown = false;

	protected boolean hasMoreResults = true;

	/** default constructor */
	public LazyList() {
		loaded = new HashMap<Integer, T>();
	}

	public LazyList(Criteria critaria, int pageSize) {
		this(critaria, pageSize, Integer.MAX_VALUE);
	}

	/**
	 * Create a LazyList backed by QueryManager, using pageSize results
	 * per page, and expecting numResults from the query.
	 */
	public LazyList(Criteria criteria, int pageSize, long numResults) {
		this(criteria, pageSize, numResults, null, false, null);
	}

	public LazyList(Criteria criteria, int pageSize, long numResults, ProjectionList projections, boolean isDistinct, Class<T> entityClass) {
		this();
		this.criteria = criteria;
		this.pageSize = pageSize;
		this.numResults = numResults;
		this.projections = projections;
		this.isDistinct = isDistinct;
		this.entityClass = entityClass;
		this.numResultsUnknown = true;
	}

	public T get(int i) {
		if (!loaded.containsKey(i)) {
			int pageIndex = i / pageSize; 

			int firstResult;
			int maxResults;
			if (numResultsUnknown) { // READ without count: take one more results than page size, to know if we have more pages
				if (pageIndex==0) { 
					firstResult = 0;
					maxResults = pageSize + 1;
				} else {
					firstResult = pageIndex * pageSize + 1;
					maxResults = pageSize;
				}
			} else {
				firstResult = pageIndex * pageSize;
				maxResults = pageSize;
			}

			CriteriaImpl criteriaImpl = (CriteriaImpl)criteria;
			//        	 ResultTransformer resultTransformer = criteriaImpl.getResultTransformer();
			/* FOR PAGED DISTINCT ONLY:
			 * perform a projection query to obtain only IDs and ordering column data
			 */
			if (isDistinct) {
				// create a new projection list with only DISTINCT internalId
				ProjectionList tempProjectionList = Projections.projectionList().add(Projections.distinct(Projections.property("internalId")),"this.internalId");
				Iterator<OrderEntry> orderings = criteriaImpl.iterateOrderings();
				while (orderings.hasNext()) {
					OrderEntry orderEntry = orderings.next();
					String order = orderEntry.getOrder().toString().replaceAll(" (asc|desc)$", "");
					if (!ArrayUtils.contains(tempProjectionList.getAliases(), order)) {
						// add projections for ordering
						tempProjectionList.add(Projections.property(order), order);
					}
				}
				criteria.setProjection(tempProjectionList);
			}
			criteria.setFirstResult(firstResult).setMaxResults(maxResults);

			long startTime = 0, startMem = 0;
			if (log.isDebugEnabled()) {	
				startTime = System.currentTimeMillis();
			}

			List<T> results = criteria.list();

			if (log.isDebugEnabled()) {
				long dT = System.currentTimeMillis() - startTime;
				log.info("[cid="+Conversation.instance().getId()+"] " 
						+ "Read "+entityClass.getSimpleName()+ ": " + results.size()
						+ " from " + firstResult + " to " + (firstResult+maxResults) 
						+ " in "
						+ dT
						+ " ms  {entityCriteria: " + criteria.toString() + "} {pageSize: "+pageSize+"}");
				/*
     			log.debug("PAGED QUERY: from: " + firstResult + " getting: " + maxResults + " pageSize: " + pageSize + " : " + criteria.toString());
     			log.debug("Found " + results.size() + " Query -> dt: " + dT + " ms, dm: " + (startMem - Runtime.getRuntime().freeMemory()) + " B.");
				 */
			}

			if (numResultsUnknown) {
				if ( results.size() < maxResults) {
					numResults = firstResult + results.size();
					hasMoreResults = false;
				}
			} else {
				if ( numResults <= firstResult + maxResults)
					hasMoreResults = false;
			}

			/* FOR PAGED DISTINCT ONLY:
			 * perform a query based on previously obtained IDs and orders
			 */
			if (isDistinct && !results.isEmpty()) {
				List<Long> resultsIds =new ArrayList<Long>();
				for (T result : results) {
					Long id = null;
					if (result instanceof Long) {
						id = (Long)result;
					}
					else {
						id = (Long)((Object[])result)[0];            			 
					}
					resultsIds.add(id);
				}
				Criteria entityCriteria = CatalogPersistenceManagerImpl.instance().createCriteria(entityClass);
				// if the original query had projectons instead of full object...
				if (projections != null) {
					// set old projections
					entityCriteria.setProjection(projections);
					// use map transformer to get results as a map
					entityCriteria.setResultTransformer(new AliasToEntityMapResultTransformer(entityClass));
					// add original subcriteria
					Iterator<Subcriteria> subcriteriaIterator = criteriaImpl.iterateSubcriteria();
					while (subcriteriaIterator.hasNext()) {
						Subcriteria subcriteria = subcriteriaIterator.next();

						if(subcriteria.getParent()!=null && !"this".equals(subcriteria.getParent().getAlias())){
							Criteria parent = findSubCriteria((CriteriaImpl)entityCriteria, subcriteria.getParent().getAlias());
							if(parent!=null){
								parent.createCriteria(subcriteria.getPath(), subcriteria.getAlias(), subcriteria.getJoinType());
							}
						}else{
							entityCriteria.createCriteria(subcriteria.getPath(), subcriteria.getAlias(), subcriteria.getJoinType());
						}
					}
				}
				// adding the internalId only restriction
				entityCriteria.add(Restrictions.in("internalId", resultsIds));
				// add original orderings...
				Iterator<OrderEntry> orderings = criteriaImpl.iterateOrderings();
				while (orderings.hasNext()) {
					OrderEntry orderEntry = orderings.next();
					entityCriteria.addOrder(orderEntry.getOrder());
					// ...and subcriteria, based on ordering if we miss them
					Iterator<Subcriteria> subcriteriaIterator = criteriaImpl.iterateSubcriteria();
					while (subcriteriaIterator.hasNext()) {
						Subcriteria subcriteria = subcriteriaIterator.next();
						String subCritAlias = subcriteria.getAlias();
						if (orderEntry.toString().startsWith(subCritAlias)) {
							Iterator<Criteria> subCriterias = ((CriteriaImpl)entityCriteria).iterateSubcriteria();

							while (subCriterias.hasNext()) {
								Criteria crit = subCriterias.next();
								if (crit.getAlias().equals(subCritAlias)) {
									subCriterias.remove();
									break;
								}
							}

							entityCriteria.createCriteria(subcriteria.getPath(), subCritAlias, subcriteria.getJoinType());

							break;
						}
					}
				}

				results = entityCriteria.list();

				//put things back as they were originally
				this.criteria.setProjection(projections);
				this.criteria.setResultTransformer(new AliasToEntityMapResultTransformer(entityClass));

			}

			for (int j = 0; j < results.size(); j++) {
				loaded.put(firstResult + j, results.get(j));
			}

		}
		return loaded.get(i);
	}


	/**
	 * Return the total number of items in the list. This is done by
	 * using an equivalent COUNT query for the backed query.
	 */
	public int size() {
		return (int) numResults;
	}

	/** update the number of results expected in this list */
	public void setNumResults(long numResults) {
		this.numResults = numResults;
	}

	/**
	 * Used by datagrids to set the pageSize.
	 * @param pageSize
	 * @return
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize; 
	}

	public int getPageSize() {
		return this.pageSize;
	}

	@Override
	public String toString() {
		return "Loaded elements: " + loaded.size() + " (" + loaded.keySet() + "), of: " + numResults;
	}

	/**
	 * Used on PDF tables to get complete non-paginated list. Also fill loaded items cache
	 * @return complete objects list from query
	 */
	public List<T> asNormalList () {
		criteria.setFirstResult(0);
		criteria.setMaxResults(Integer.MAX_VALUE);

		return criteria.list();
	}

	public boolean numResultsUnknown() {
		return numResultsUnknown;
	}

	public boolean hasMoreResults() {
		return hasMoreResults;
	}

	/**
	 * Drops current list and reloads it with new sorting option
	 * 
	 * @param expression - sorting by parameter binding expression
	 * @param orderingDirection - ascending or descending ordering
	 * @throws PhiException
	 */
	public void orderBy(String expression, OrderBy orderingDirection) throws PhiException {
		try {
			// LIST CLEARING
			loaded.clear();
			Iterator<OrderEntry> orderEntries = ((CriteriaImpl)criteria).iterateOrderings();
			while (orderEntries.hasNext()) {
				orderEntries.next();
				orderEntries.remove();
			}

			//FIXME: we need to change this procedure using QueryManager directly! 
			// SEARCH FOR RIGHT ALIAS
			if (expression.contains("[")) {
				expression = expression.replace("[", ".");
				expression = expression.replace("]", "");
			}

			String[] elParts = expression.split("\\."); 
			Class currentClass = Class.forName(((CriteriaImpl)criteria).getEntityOrClassName());
			Criteria currentCriteria = criteria;

			for (int z = 0; z<elParts.length; z++) {

				PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(currentClass);
				PropertyDescriptor pd = null;
				for(PropertyDescriptor prop : props){
					if(elParts[z].equals(prop.getName())){
						pd = prop;
						break;
					}
				}

				if (pd == null) {
					throw new IllegalArgumentException(criteria + " does not contain property: " + elParts[z]);
				}

				Class nextType = pd.getPropertyType();

				boolean isList = false; 

				Type[] typez = pd.getWriteMethod().getGenericParameterTypes();

				if (typez != null && typez[0] instanceof ParameterizedType ) { // If type is list find the type of the list
					ParameterizedType pt = (ParameterizedType)typez[0];
					nextType = (Class)pt.getActualTypeArguments()[0];
					isList = true;
				}

				if (nextType.getAnnotation(Entity.class) != null) {
					if (z + 1 < elParts.length) {
						if (nextType.isAssignableFrom(CodeValue.class)) { //FIX ME 
							nextType = CodeValuePhi.class;
						}
						currentClass = nextType;
						Criteria subcriteria = findSubCriteria(generateAliasFromExpression(elParts,z));

						//If not present add only not null restriction
						if (subcriteria == null) {
							currentCriteria = currentCriteria.createCriteria(elParts[z], generateAliasFromExpression(elParts,z), Criteria.LEFT_JOIN);
						} else {
							currentCriteria = subcriteria;
						}
					}
				} else if (nextType.isAssignableFrom(II.class)) {
					currentClass = II.class;
					Criteria subcriteria = findSubCriteria(elParts[z]);

					if (subcriteria == null) {
						currentCriteria = currentCriteria.createCriteria(elParts[z],generateAliasFromExpression(elParts,z),CriteriaSpecification.LEFT_JOIN);
					} else {
						currentCriteria = subcriteria;
					}
				} else {
					String property = "";
					for (int j=z;j<elParts.length;j++) {
						property += "." + elParts[j];
					}

					// ADDING NEW ORDERING CRITERIA
					if (orderingDirection == OrderBy.ascending) {
						criteria.addOrder(Order.asc(generateAliasFromExpression(elParts,z-1) + property).ignoreCase());
					} else {
						criteria.addOrder(Order.desc(generateAliasFromExpression(elParts,z-1) + property).ignoreCase());
					}
					break;
				}
			}
		} catch (Exception e) {
			throw new PhiException("Error re-sorting DataGrid...", e, ErrorConstants.APPLICATION_GENERIC_ERR_CODE);
		}
	}

	/**
	 * Same as BaseAction method
	 */
	private String generateAliasFromExpression (String[] elParts, int z) {
		String alias = "";

		for (int i = 0; i<=z; i++) {
			String t = elParts[i];
			t = t.substring(0,1).toUpperCase()+t.substring(1,t.length());
			alias +=  t;
		}
		if (alias.isEmpty()) {
			return "this";
		}
		return alias;
	}

	/**
	 * Same as BaseAction method
	 */
	protected Criteria findSubCriteria(String alias) {

		return findSubCriteria((CriteriaImpl)criteria, alias);
	}

	/**
	 * Same as BaseAction method
	 */
	protected Criteria findSubCriteria(CriteriaImpl crit, String alias) {

		Criteria subCriteria = null;
		if(crit==null)
			return subCriteria;

		Iterator<Criteria> subcriteria = crit.iterateSubcriteria();

		while (subcriteria.hasNext()) {
			Criteria nextCrit = subcriteria.next();
			if (nextCrit.getAlias().equals(alias)) {
				subCriteria = nextCrit;
				break;
			}
		}

		return subCriteria;
	}
}