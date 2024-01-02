package com.phi.entities.actions;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;

import com.phi.annotations.ShowInDesigner;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.lock.LockManager;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.act.DocRepository;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.pk.PdfExporter;
import com.phi.ps.ProcessManager;
import com.phi.security.UserBean;
import com.phi.sign.DocSigner;
import com.phi.sign.SignType;
import com.phi.sign.SigningUserCheck;

@BypassInterceptors
@Name("DocRepositoryAction")
@Scope(ScopeType.CONVERSATION)
public class DocRepositoryAction extends BaseAction<DocRepository, Long> {

	private static final long serialVersionUID = -5605575989669663192L;

	private static final Logger log = Logger.getLogger(DocRepositoryAction.class);


	/**
	 * Conversation scope instance retrieving
	 * @return instance of the action, singleton into the conversation
	 */
	public static DocRepositoryAction instance() {
		return (DocRepositoryAction) Component.getInstance(
				DocRepositoryAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * METHOD ONLY FOR AMB
	 */


	/**
	 * Set column version_sign with the number of sign
	 * @param docRepos
	 */
	public int getNumberForVersionSign(DocRepository docRepos, Boolean isTmp){
		int value = 0;
		if(docRepos!= null){
			if(docRepos.getVersionSign()==0){
				if((isTmp !=null && isTmp==true) || docRepos.getInternalId() == 0 ){
					value =  1;
				}
				else{
					value = 2;
				}
			}

			else
				value = docRepos.getVersionSign()+1;
		}
		return value;
	}
	/**
	 * You can create a list of selectItem. Get in input the id of the label separated by comma.
	 * An example:
	 * DocRepostoryAction.getTypeDocument('Label_1391696419187','Label_1391696481487')
	 * @param messsages
	 * @return
	 */
	public List<SelectItem> getTypeDocument(String... messsages) {

		List<SelectItem> list = new ArrayList<SelectItem>();
		if(messsages!=null){
			FunctionsBean fb = FunctionsBean.instance();
			for (int i=0; i<messsages.length; i++ ){
				SelectItem sel = new SelectItem();
				String mes= messsages[i];
				String label= fb.getTranslation(mes);
				sel.setLabel(label);
				sel.setValue(mes);
				list.add(sel);

			}
		}
		return list;
	}

	/**
	 * Method for massive signature
	 * @param employee
	 * @throws PhiException
	 * @throws NamingException 
	 */
	public void massiveSign(Employee employee) throws PhiException, NamingException{

		temporary.put("massive", true);
		for (DocRepository docRepos : getSelectedEntitiesList()){
			this.refresh();
			this.getTemporary().put("assignedDocAuth", docRepos.getAssignedDocAuth());
			//docRepos.setAssignedDocAuth(employee);
			this.inject(docRepos);
			this.temporary.put("multiple", true);
			this.signDocument(null);
			if(((Boolean)temporary.get("errorPin"))== false){
				addSigningUser(docRepos);
				unlock();
			}
			ca.create(docRepos);
			this.eject();
		}
		temporary.put("massive", null);
		ca.flushSession();
	}
	/**
	 * Control if a document is locked and lock the document
	 * @return
	 * @throws NamingException
	 * @throws PersistenceException 
	 */
	public boolean lockList() throws NamingException, PersistenceException{
		boolean lock= true;
		LockManager locker = (LockManager)Component.getInstance("Locker");
		for(int i = 0; i< getSelectedEntitiesList().size(); i++){
			DocRepository docRepository = getSelectedEntitiesList().get(i); 
			String idInt = Long.toString(docRepository.getInternalId());
			lock = locker.lockIt(docRepository.getClass().getSimpleName(), idInt, false);
			if(lock == false){
				this.inject(docRepository);
				break;
			}

		}

		return lock;

	}

	/**
	 * Unlock all the document in a list
	 * @throws NamingException 
	 */
	public void unlockList() throws NamingException{
		LockManager locker = (LockManager)Component.getInstance("Locker");
		for(DocRepository docRepository : getSelectedEntitiesList()){
			if(docRepository != null){
				locker.unlock(docRepository.getClass().getSimpleName(), docRepository.getInternalId());
			}

		}
	}
	/**
	 * Create a row if the user signed the document
	 * @param docRepos
	 * @throws PersistenceException
	 */
	public void addSigningUser(DocRepository docRepos) throws PersistenceException{
		SigningUserCheck signingUserCheck=null;
		signingUserCheck= new SigningUserCheck();
		signingUserCheck.setCreationDate(new Date());
		signingUserCheck.setDocId(docRepos.getInternalId());
		signingUserCheck.setCreationDate(new Date());
		String currentUser = UserBean.instance().getUsername();
		signingUserCheck.setUserName(currentUser);
		ca.create(signingUserCheck);
	}

	/**
	 * Check if the user already signed the document
	 * @param docRepository
	 * @return
	 */
	public boolean alreadySign(DocRepository docRepository){
		boolean alreadySign= false;
		if(docRepository !=null){
			long docId= docRepository.getInternalId();
			String userName = UserBean.instance().getUsername();
			Query queryBuilder = ca.createQuery("SELECT un FROM SigningUserCheck un where docId=?1 and userName=?2");
			queryBuilder.setParameter(1, docId);
			queryBuilder.setParameter(2, userName);
			List<?> result= queryBuilder.getResultList();
			if (result.size()>0){
				alreadySign= true;
			}
		}
		return alreadySign;

	}

	@SuppressWarnings("unchecked")
	public boolean allDocumentAlreadySigned(){

		Context conversation = Contexts.getConversationContext();
		if(conversation.get("DocRepositoryList") != null){
			List<DocRepository> docReposList = ((PhiDataModel<DocRepository>) conversation.get("DocRepositoryList")).getList();
			if (docReposList!= null && !docReposList.isEmpty()){
				for(DocRepository docRepos : docReposList){
					if(alreadySign(docRepos)==false){
						return false;
					}
				}
			}
		}
		return true;
	}
	/**
	 * Delete the row in the table if the mdoerator singed the document.
	 * @param docRepository
	 * @throws PersistenceException 
	 */
	public void deleteSigningUser(DocRepository docRepository) throws PersistenceException{
		long docId= docRepository.getInternalId();
		Query queryBuilder = ca.createQuery("DELETE SigningUserCheck un where docId=?1");
		queryBuilder.setParameter(1, docId);
		/*int resultNumber =*/queryBuilder.executeUpdate();
	}
	// -------------------------
	// --- Document controls ---
	// -------------------------

	/**
	 * Injected document sign status
	 * @return true if the injected document has been signed
	 */
	@ShowInDesigner(description = "injected document sign status")
	public boolean isSigned() {
		return isSigned(getEntity());
	}

	/**
	 * Injected document deactivation status
	 * @return true if the injected document has been deactivated
	 */
	@ShowInDesigner(description = "injected document deactivation status")
	public boolean isCancelled() {
		return isCancelled(getEntity());
	}

	/**
	 * Document sign status
	 * @param docrepos document to be verified
	 * @return true if the document has been signed
	 */
	@ShowInDesigner(description = "document sign status")
	public boolean isSigned(DocRepository docrepos) {
		if(docrepos!=null){
			if (docrepos.getCompletionStatus()!=null && "LA".equals(docrepos.getCompletionStatus().getCode()))
				return true;
		}
		return false;
	}

	/**
	 * Document deactivation status
	 * @param docrepos document to be verified
	 * @return true iif the document has been deactivated
	 */
	@ShowInDesigner(description = "document deactivation status")
	public boolean isCancelled(DocRepository docrepos) {
		if(docrepos!=null){
			if (docrepos.getAvailabilityStatus()!=null && "CA".equals(docrepos.getAvailabilityStatus().getCode()))
				return true;
		}
		return false;
	}
	/**
	 * Puts a digital signiture on the document.<br/>
	 * The process uses the 'firmadigitale' module, by sending the pdf via 
	 * webservice and getting the signed version back.<br/>
	 * A version note is also put in order to justify the new version that will 
	 * be created.
	 */
	@ShowInDesigner(description = "puts the digital signiture into the PDF")
	public void signDocument(SignType signType) throws PhiException {
		try {
			DocRepository docrepos = getEntity();
			String versionNote = docrepos.getVersionNote();
			if(versionNote == null || versionNote.trim().isEmpty()) {
				versionNote = "";
			}

			// sign document
			Date signDocDateReplace = docrepos.getSignDocDate();
			if((Boolean)temporary.get("multiple")== null || ((Boolean)temporary.get("multiple"))!= true){
				docrepos.setSignDocDate(new Date());
			}
			String contentType = DocSigner.instance().sign(docrepos, signType, signDocDateReplace);

			docrepos.setVersionNote (versionNote);
			setCodeValue("contentType",	"PHIDIC", "PHIDOCContentType", contentType);
			setCodeValue("completionStatus","PHIDIC", "PHIDOCCompletionStatus", "LA");
			temporary.put("errorPin", false);
		} catch(Exception e) {
			log.error("exception during digital signature " + e.toString(), e);
			temporary.put("errorPin", true);
			DocSigner docSigner = DocSigner.instance();
			docSigner.setPin(null);
			//throw new PhiException("exception during digital signature ",e.getCause(),"");
		}
	}

	/**
	 * Override of create() that updates the docVersion property.
	 */
	@Override
	@ShowInDesigner(description="Save an object")
	public void create() throws PhiException {
		Boolean excludeHistory = getEntity().getExcludeHistory();
		createWithFlags(excludeHistory == null ? false : excludeHistory);
	}

	@ShowInDesigner(description="Save an object and set History Exclusion flag")
	public void createWithFlags(boolean excludeHistory) throws PhiException {
		DocRepository docrepos = getEntity();
		if (docrepos.getContent() != null) {
			docrepos.setExcludeHistory(excludeHistory);
			Integer docVersion = docrepos.getDocVersion();
			if ("PHI".equals(docrepos.getApplicationOwner())){
				String parentDocNumber = null;
				if(docVersion != null) {
					docVersion++;
					parentDocNumber=docrepos.getUniqueDocumentNumber();
				} else {
					docVersion = 0;
				}
				docrepos.setDocVersion(docVersion);
				//in replace the parentDocId = uniqueDocNumber
				docrepos.setParentDocumentNumber(parentDocNumber);
				docrepos.setDocDate(new Date());//date of each change happened
				//the UNIQUE DOC ID is DOCVERSION+INTERNALID for doc auto generate in PHI
				docrepos.setUniqueDocumentNumber(Long.toString(docrepos.getInternalId())+"-"+docVersion);
				super.create();
			}else{
				if(docVersion != null) {
					docVersion++;
				} else {
					docVersion = 0;
				}
				docrepos.setDocVersion(docVersion);
				super.create();
			}
		}

	}


	// ------------------------------ FIXME -----------------------------------
	// This method is currently shown in this class only as a reminder: its 
	// functionality has been replaced by cancelDDocument().
	// This method should be added to BaseAction (integration note: all the 
	// entities that will be manipulated are supposed to extend BaseEntity).
	//
	// This method, as much as delete() might be error-prone because of the 
	// current implementation of eject() method.
	// 
	// Scenario:
	// 1) the user selects n records from a list
	// 2) the user clicks on DELETE
	// 3) the system calls delete(), this means
	//	  a) each selected item is deleted from the db, or deactivated
	//    b) eject() is called
	//    -> at this stage, eject removes from the ConversationContext the 
	//       injected bean, identified by getConversationName(), which is 
	//       always the same
	// 4) the button pressure forces the record list to be redisplayed on the ui
	// -> if the developer doesn't append a read() after the delete() call, the
	//    view that will be displayed will be different from the database data, 
	// since the deleted values are still into the conversation.
	// 
	// 		BASIC SOLUTION
	// if (ent != null && selectedEntities.get(ent) == true) {
	//		ent.delete(ent);
	//      if(ent.getInternalId().equals(getEntity().getInternalId)) {
	//			eject();
	// 		}
	//		multiselected = true;
	// }
	// The injected bean is ejected only if among the selected ones.
	// The developer has to append a read() call to delete().
	//	
	//		INTEGRATION
	// The deleted entities should be removed even from the DataModel, avoiding 
	// the charge of reloading the list from the database. 
	// ------------------------------------------------------------------------
	/**
	 * Logical delete of the selected entities. Instead of physically delete a 
	 * record, the isActive property is changed to false and the relative 
	 * objects are ejected from the dataset.<br/>
	 * Useful to delete entities linked by referential integrity constraints. It 
	 * is compulsory that the UI which displays the updated data will 
	 * discriminate the deactivated records.
	 * @throws PhiException 
	 * if there has been an exception during the database update.
	 */
	@ShowInDesigner(description="Delete logically an object")
	@Deprecated
	public void logicalDelete() throws PhiException {

		//Multiselect:
		boolean multiselected = false;
		if (selectedEntities != null && selectedEntities.size() >0) {
			for (DocRepository ent : selectedEntities.keySet()) {
				if (ent != null && selectedEntities.get(ent) == true) {
					ent.setIsActive(false);
					ca.update(ent);
					eject();
					multiselected = true;
				}
			}
		}

		if (multiselected == false && getEntity() != null) {
			entity.setIsActive(false);
			ca.update(entity);
			eject();
		}
	}

	/**
	 * Saves the PDF generated by the pageName to the file denoted by the
	 * fileName
	 *
	 * @param fileName
	 *            the file name
	 * @param pageName
	 *            the page name
	 */
	public void saveOnFS(String fileName, String pageName, String cid) throws PhiException{
		PdfExporter pdfExporter = new PdfExporter();
		byte[] pdfBytes = pdfExporter.pdfExport(pageName,cid);
		try {
			FileOutputStream fos = new FileOutputStream(fileName);

			fos.write(pdfBytes);

			fos.close();
		} catch (Exception e) {
			log.error("exception during save on FS " + e.toString(), e);
			throw new PhiException("exception during save on FS ",e.getCause(),"");
		}
	}

	/**
	 * Useful when you have already an instance of DocRepository and you want to CANCEL it with
	 * a legally document
	 * 
	 * @param pageName must have this format '/MOD_Document/CORE/FORMS/r_deactivated.xhtml'
	 * @param cid conversation id
	 * @param nextProcess must have this format 'ButtonBack_1378160043328;BACK' if null NO NEXT process
	 */
	public void cancelDDocument(String pageName, String cid) throws PhiException{
		PdfExporter pdfExporter = (PdfExporter)Component.getInstance(PdfExporter.class, ScopeType.EVENT);
		byte[] cancelDoc = pdfExporter.pdfExport(pageName,cid);
		try {
			DocRepository docrepos = getEntity();
			byte[] currentContent = docrepos.getContent();
			docrepos.setOriginalContent(currentContent);
			docrepos.setContent(cancelDoc);
			//    		DocRepositoryAction docReposAction = DocRepositoryAction.instance();
			setCodeValue("contentType", "PHIDIC", "PHIDOCContentType", "PDF");
			setCodeValue("completionStatus", "PHIDIC", "PHIDOCCompletionStatus", "AU");
			setCodeValue("availabilityStatus", "PHIDIC", "PHIDOCAvailabilityStatus", "CA");
			docrepos.setIsActive(false);
			//create method is called in deactive process

		} catch (Exception e) {
			log.error("exception during cancel DDocument " + e.toString(), e);
			throw new PhiException("exception during cancel DDocument ",e.getCause(),"");
		}
	}

	public void insertNewDDocument(String pageName, String docType) throws PhiException {
		insertNewDDocument(pageName, docType, null);
	}

	public void insertNewDDocument(String pageName, String docType, BaseEntity generatorEntity) throws PhiException {
		insertNewDDocument(pageName, Conversation.instance().getId(), null, docType, generatorEntity);
	}

	public void insertNewDDocument(String pageName, String cid, String nextProcess, String docType) throws PhiException{
		insertNewDDocument(pageName, cid, nextProcess, docType, null);
	}

	/**
	 * This method is useful when PHI needs to INSERT a NEW a PDF-PHI REPORTs in the docrepos
	 * 
	 * @param pageName must have this format '/MOD_Document/CORE/FORMS/r_deactivated.xhtml'
	 * @param cid conversation id
	 * @param nextProcess has this format 'ButtonBack_1378160043328;BACK' if null NO NEXT process
	 * @param docType type of doc IN DIC PHIDOCDocumentType
	 * @param generatorEntity entity to build discriminator for same report linked to multiple entities
	 */
	public void insertNewDDocument(String pageName, String cid, String nextProcess, String docType, BaseEntity generatorEntity) throws PhiException{
		PdfExporter pdfExporter = (PdfExporter)Component.getInstance(PdfExporter.class, ScopeType.EVENT);
		byte[] pdfBytes = pdfExporter.pdfExport(pageName,cid);
		try {
			//    		DocRepositoryAction docReposAction = DocRepositoryAction.instance();
			DocRepository docrepos = new DocRepository();
			inject(docrepos);
			Patient patient = PatientAction.instance().getEntity();
			PatientEncounter patEnc = PatientEncounterAction.instance().getEntity();

			// SET CONTENT
			docrepos.setContent(pdfBytes);
			// SET CONTENT TYPE
			setCodeValue("contentType", "PHIDIC", "PHIDOCContentType", "PDF");
			// SET APPLICATION OWNER
			docrepos.setApplicationOwner("PHI");
			// SET DOCUMENT TYPE
			if (docType!=null && !docType.isEmpty()) {
				setCodeValue("code", "PHIDIC", "PHIDOCDocumentType", docType);
			}
			// SET COMPLETION STATUS
			setCodeValue("completionStatus", "PHIDIC", "PHIDOCCompletionStatus", "AU"); //ValidDocument like default?
			// SET AVAILABILITY STATUS
			setCodeValue("availabilityStatus", "PHIDIC", "PHIDOCAvailabilityStatus", "AV"); //ValidDocument like default?
			// SET CONFIDENTIALITY CODE
			setCodeValue("confidentiality", "PHIDIC", "PHIDOCConfidentiality", "N"); //Normal like default?
			if (!isPatientRelatedDocument(docType)) {
				// SET PATIENT ENCOUNTER IF NOT A FRPH DIARY
				docrepos.setEncounter(patEnc);
			}
			// SET PATIENT
			docrepos.setPatient(patient);
			// SET SDL
			ServiceDeliveryLocation sdl = ServiceDeliveryLocationAction.instance().getEntity();
			if (sdl == null && patEnc != null) { 
				sdl = patEnc.getServiceDeliveryLocation();
			}
			docrepos.setServiceDeliveryLocation(sdl);
			// SET FILE NAME
			docrepos.setFileName(pageName);
			// SET GENERATOR ENTITY
			if (generatorEntity != null) {
				String entityName = HibernateProxyHelper.getClassWithoutInitializingProxy(generatorEntity).getSimpleName();
				docrepos.setGeneratorEntityId(entityName + "-" + generatorEntity.getInternalId());
			}
			// SET AUTHOR
			UserBean ub = (UserBean)Component.getInstance(UserBean.class);
			if (ub != null) {
				docrepos.setAssignedDocAuth(ub.getCurrentEmployee());
			}
			// SET TRANSFER
			//docrepos.setTransfer(getLastTransfer());

			// MANAGE PROCESS
			if (nextProcess!=null && !nextProcess.isEmpty()){
				ProcessManager processManager = (ProcessManager)Component.getInstance("processManager");
				processManager.manageTask(nextProcess);
			}else{
				log.warn("nextProcess is null or empty, so THE PROCESS won't go ahead");
			}

		} catch (Exception e) {
			log.error("exception during insert New DDocument " + e.toString(), e);
			throw new PhiException("exception during insert New DDocument ",e.getCause(),"");
		}
	}

	public void replaceDDocument(String pageName, String docType) throws PhiException {
		replaceDDocument(pageName, docType, null);
	}

	public void replaceDDocument(String pageName, String docType, BaseEntity generatorEntity) throws PhiException {
		replaceDDocument(pageName, Conversation.instance().getId(), null, docType, generatorEntity);
	}

	public void replaceDDocument(String pageName, String cid, String nextProcess, String docType) throws PhiException{
		replaceDDocument(pageName, cid, nextProcess, docType, null);
	}

	/**
	 * This method is useful when PHI needs to REPLACE a PDF-PHI REPORTs in the docrepos
	 * 
	 * @param pageName must have this format '/MOD_Document/CORE/FORMS/r_deactivated.xhtml'
	 * @param cid conversation id
	 * @param nextProcess has this format 'ButtonBack_1378160043328;BACK' if null NO NEXT process
	 * @param docType type of doc IN DIC PHIDOCDocumentType
	 * @param generatorEntityId discriminator for same report linked to multiple entities
	 */
	public void replaceDDocument(String pageName, String cid, String nextProcess, String docType, BaseEntity generatorEntity) throws PhiException{
		try {
			//    		DocRepositoryAction docReposAction = DocRepositoryAction.instance();
			Patient patient = PatientAction.instance().getEntity();
			PatientEncounter patEnc = PatientEncounterAction.instance().getEntity();

			cleanRestrictions();
			setReadPageSize(null);
			
			equal.put("fileName", pageName);
			if (patient != null) {
				equal.put("patient", patient);
			}
			notEqual.put("availabilityStatus.code","CA");

			if (generatorEntity == null) {
				if (isPatientRelatedDocument(docType)) {
					equal.put("code.code", docType);
				} else if (patEnc!=null && patEnc.getInternalId()>0) {
					equal.put("encounter", patEnc);
				}
			} else {
				String entityName = HibernateProxyHelper.getClassWithoutInitializingProxy(generatorEntity).getSimpleName();
				equal.put("generatorEntityId", entityName + "-" + generatorEntity.getInternalId());
			}

			//    		equal.put("transfer", getLastTransfer());

			orderBy.put("docDate","descending");

			List<DocRepository> docReposList = list();
			DocRepository docrepos = null;

			if (isPatientRelatedDocument(docType) && docReposList.size() > 1) {
				log.warn("More than one document with filename: "+ pageName + (patient == null ? "" : " - patient: "+patient.getInternalId()) + ". Maybe you have old wrong copies of this document in database: remove them.");
				DocRepository docReposTemp = docReposList.get(0);
				docReposList.clear();
				docReposList.add(docReposTemp);
			}
			if (docReposList.size()==1){
				//if (docrepos.getAvailabilityStatus()!=null && !"CA".equals(docrepos.getAvailabilityStatus().getCode())){
				PdfExporter pdfExporter = new PdfExporter();
				byte[] pdfBytes = pdfExporter.pdfExport(pageName,cid);

				docrepos = docReposList.get(0);

				//use to drives the process REPLCACE in decision "is replaced?" instead of parentDocId that is sent WHEN the user click ok in the preview UI in the procesdocReposAction.getTemporary().put("replaceInAction",true);
				if (getEntity() == null) {
					inject(docrepos);
				}
				refresh();

				// SET CONTENT
				docrepos.setContent(pdfBytes);
				docrepos.setVersionNote("");//useful reset to prepare text area for new rev note

				// SET AUTHOR
				UserBean ub = (UserBean)Component.getInstance(UserBean.class);
				if (ub != null) {
					docrepos.setAssignedDocAuth(ub.getCurrentEmployee());
				}
				//} else {
				//log.error("doc id :"+docrepos.getInternalId()+" is CANCELED");

				//}

				if (nextProcess!=null && !"".equals(nextProcess)){
					inject(docrepos);
					ProcessManager processManager = (ProcessManager)Component.getInstance("processManager");
					processManager.manageTask(nextProcess);
				} else {
					log.warn("nextProcess is null or empty, so THE PROCESS won't go ahead");
				}

			} else if (docReposList.size()==0){ 
				insertNewDDocument(pageName,cid,nextProcess,docType, generatorEntity);
			} else {
				log.error("More than one document with filename: " + pageName + (patient == null ? "" : " - patient: "+patient.getInternalId()) + (patEnc == null ? "" : (" - encounter ID:"+patEnc.getInternalId())));
				throw new PhiException("More than one document with filename: " + pageName + (patient == null ? "" : " - patient: "+patient.getInternalId()) + (patEnc == null ? "" : (" - encounter ID:"+patEnc.getInternalId())),"");
			}


		} catch (Exception e) {
			log.error("exception during insert New DDocument " + e.toString(), e);
			throw new PhiException("exception during insert New DDocument ",e.getCause(),"");
		}
	}

	/**
	 * Sets an or filter between PatientEncounter and docType
	 * 
	 * @param patEnc - a PatientEncounter
	 * @param docTypes - a docType code
	 */
	public void setEncounterOrCodeRestriction(PatientEncounter patEnc, String... docTypes) {

		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);

		if(findSubCriteria("docType")==null)
			entityCriteria.createAlias("code", "docType", Criteria.LEFT_JOIN);

		Criterion docTypesCriterion = Restrictions.eq("docType.code", docTypes[0]);
		for (int i=1; i<docTypes.length; i++) {
			String docType = docTypes[i];
			docTypesCriterion = Restrictions.or(docTypesCriterion, Restrictions.eq("docType.code", docType));
		}

		entityCriteria.add(Restrictions.or(Restrictions.eq("encounter", patEnc), docTypesCriterion));
	}

	/**
	 * Gets an array of selected DocRepository documents
	 * 
	 * @return an array of selected DocRepository documents
	 */
	public byte[][] getConversationDocs() {

		List<byte[]> result = new ArrayList<byte[]>();

		// GET SELECTED DOCREPOSITORIES
		List<DocRepository> selectedDocRepository = getSelectedEntitiesList();

		// ORDER SELECTED DOCREPOSITORIES BY DATE
		Collections.sort(selectedDocRepository, new Comparator<DocRepository>() {
			@Override
			public int compare(DocRepository o1, DocRepository o2) {
				if (o1 == null) {
					if (o2 == null) {
						return 0;
					} else {
						return -1;
					}
				} else if (o2 == null) {
					return 1;
				}

				Date docDate1 = o1.getDocDate();
				Date docDate2 = o2.getDocDate();

				if (docDate1 == null) {
					if (docDate2 == null) {
						return 0;
					} else {
						return -1;
					}
				} else if (docDate2 == null) {
					return 1;
				} else {
					return docDate1.compareTo(docDate2);
				}
			}
		});

		// GET BYTEARRAY PDFs
		for (DocRepository dr : selectedDocRepository) {
			result.add(dr.getContent());
		}

		return result.toArray(new byte[result.size()][]);
	}


	public void selectAllDocuments() {
		Boolean areAllDocsSelected = (Boolean)getTemporary().get("allDocsSelected");
		selectAllDocuments(areAllDocsSelected);
	}    

	@SuppressWarnings("unchecked")
	public void selectAllDocuments(Boolean areAllDocsSelected) {
		if (areAllDocsSelected != null && areAllDocsSelected) {
			getSelectedEntities().clear();
			getTemporary().remove("allDocsSelected");
		} else {
			IdataModel<DocRepository> docRepositoryDataModel = (IdataModel<DocRepository>)Contexts.getConversationContext().get("DocRepositoryList");
			if (docRepositoryDataModel != null) {
				List<DocRepository> docRepositoryList = (List<DocRepository>)docRepositoryDataModel.getFullList();
				Map<DocRepository, Boolean> selectedEntities = getSelectedEntities();
				for (DocRepository docRepos : docRepositoryList) {
					selectedEntities.put(docRepos, true);
				}
				getTemporary().put("allDocsSelected", true);
			}
		}
	}

	//    public Transfer getLastTransfer() throws PhiException {
	//    	Transfer lastTransfer = null;
	//
	//		PatientEncounter patEnc = PatientEncounterAction.instance().getEntity();
	//    	TransferAction ta = TransferAction.instance();
	//    	ta.cleanRestrictions();
	//    	ta.getOrderBy().put("registrationDate", "descending");
	//    	ta.getEqual().put("patientEncounter", patEnc);
	//    	ta.getEqual().put("isSupport", false);
	//    	ta.getEqual().put("isActive", true);
	//    	List<Transfer> transferList = ta.list();
	//    	
	//    	if (transferList != null && !transferList.isEmpty()) {
	//    		lastTransfer = transferList.get(0);
	//    	}
	//    	
	//    	return lastTransfer;
	//    }

	public boolean isPatientRelatedDocument(String docType) {
		boolean result = false;

		result |= "49".equals(docType); // ALLERGIES LIST
		result |= "72".equals(docType); // FRPH DIARY
		result |= "91".equals(docType); // FALL ASSESSMENT SCALE

		return result;
	}

	public byte[] pdfRender(String pageName){
		return pdfRender(pageName, Conversation.instance().getId());
	}

	public byte[] pdfRender(String pageName, String conversationId){
		PdfExporter pdfExporter = (PdfExporter)Component.getInstance(PdfExporter.class, ScopeType.EVENT);
		return pdfExporter.pdfExport(pageName,conversationId);
	}

	public boolean isBytePdf(byte[] content){
		return content[0] == 0x25		// %
				&& content[1] == 0x50	// P
				&& content[2] == 0x44	// D
				&& content[3] == 0x46	// F
				&& content[4] == 0x2D;	// -
	}

}