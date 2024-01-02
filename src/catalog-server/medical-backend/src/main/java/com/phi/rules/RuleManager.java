package com.phi.rules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.RuleAgent;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.virtual.VirtualFile;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.util.CsConstants;




@Name("RuleManager")
@Scope(ScopeType.APPLICATION)
//@Startup
public class RuleManager {


	private static final Logger log = Logger.getLogger(RuleManager.class);
	private HashMap<String,RuleAgent> ruleAgentsMap = new HashMap<String,RuleAgent>();
	private HashMap<String,RulesModifiedDate> storedKnowledgeBase = new HashMap<String,RulesModifiedDate>();

	private VirtualFile ruleFolder;
	private VirtualFile ruleFolderCustomer;

	/**
	 * This BL loads all the rules defined in properties files in the project directory 
	 * in an HashMap in order to be available when they're needed.
	 * 
	 */
	@Create
	public void init(){
		try {
 			  
			RepositoryManager repom = RepositoryManager.instance();
			ruleFolder = repom.getEar().getChild("rules");
 			  
			loadRules(ruleFolder);
			
			Context app = Contexts.getApplicationContext();	
			String customer = (String)app.get(CsConstants.CUSTOMER);
			
			if (customer != null && !customer.isEmpty()) {
				ruleFolderCustomer = ruleFolder.getChild("CUSTOMER_" + customer);
				loadRules(ruleFolderCustomer);
			}

		} catch (Exception e) {
			log.error("Error during init rules "+e);
		}
		  return;
 	 }
	
	private void loadRules(VirtualFile ruleFolder) throws IOException {
		if(!ruleFolder.isLeaf()){
			List<VirtualFile> files = ruleFolder.getChildren();
			if (files != null) {
				for(VirtualFile ruleFile : files){
					//load drl rules - one drl -> one kbase
					if(ruleFile.getName().indexOf(".drl")>0){
						loadRule(ruleFile);
					}
				}
			}
		}
	}
	
	private KnowledgeBase loadRule(VirtualFile ruleFile) throws IOException {
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		Resource drl = ResourceFactory.newInputStreamResource( ruleFile.openStream() );
		kbuilder.add(drl, ResourceType.DRL );
		
		if ( kbuilder.hasErrors() ) {
			log.error( kbuilder.getErrors().toString() );
		}
		
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
		this.storedKnowledgeBase.put(ruleFile.getName(), new RulesModifiedDate(ruleFile.getLastModified(),kbase));
		  
		return kbase;
	}
	
	public RuleAgent getRuleAgent(Properties prop,String ruleAgentName){
		if (! this.ruleAgentsMap.containsKey(ruleAgentName)){
			loadBRMS(prop,ruleAgentName);
		}
		return this.ruleAgentsMap.get(ruleAgentName);

	}
	
	public KnowledgeBase getKnowledgeBase(String ruleName) throws FileNotFoundException, PhiException{
		evaluateRules(ruleName);
		if (this.storedKnowledgeBase != null && this.storedKnowledgeBase.get(ruleName) !=null)
			return this.storedKnowledgeBase.get(ruleName).getKbase();
		else {
			log.error ("KnowledgeBase not found for rule "+ruleName);
			return null;
		}
	}
	
	/**
	 * Every time one rule has to be evaluate this method checks if there is an updated one. 
	 * @param nameFile is the key and the name of the file containing the rules definition
	 * @throws FileNotFoundException
	 * @throws PhiException 
	 */
	public void evaluateRules(String nameFile) throws FileNotFoundException, PhiException{
		try {
			VirtualFile ruleFile = ruleFolder.getChild(nameFile);
			if (ruleFile == null || !ruleFile.exists() ) {
				ruleFile = ruleFolderCustomer.getChild(nameFile);
			}
			if(this.storedKnowledgeBase.containsKey(nameFile)){
				if(ruleFile.getLastModified() > this.storedKnowledgeBase.get(nameFile).getLastModified()){
					loadRule(ruleFile);
				}	
			}
		} catch (Exception e) {
			throw new PhiException(ErrorConstants.APPLICATION_GETTING_METHOD_ERR_INTERNAL_MSG,e,ErrorConstants.APPLICATION_GETTING_METHOD_ERR_CODE);
		}
	}

	public HashMap<String, RulesModifiedDate> getStoredKnowledgeBase() {
		return storedKnowledgeBase;
	}
	
    public static RuleManager instance() {
        return (RuleManager) Component.getInstance(RuleManager.class, ScopeType.APPLICATION);
    }
    
    private void loadBRMS(Properties prop, String ruleAgentName){
    	if (prop!= null){
    		RuleAgent agent = RuleAgent.newRuleAgent(prop);
    		if (agent != null && agent.getRuleBase() != null && agent.getRuleBase().getPackages() != null && agent.getRuleBase().getPackages().length ==1 && agent.getRuleBase().getPackages()[0].getName().equalsIgnoreCase(ruleAgentName)){
    			ruleAgentsMap.put(agent.getRuleBase().getPackages()[0].getName(), agent);
    		}
    	}
    	return;
    }
}
