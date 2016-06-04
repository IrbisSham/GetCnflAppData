package com.itzabota.plugins.getcnflappdata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
public class FldOptions {

	private final Logger log = Logger.getLogger(FldOptions.class);
	
	private Map<String, ArrayList<String>> appKeeper = new TreeMap<>();
	private OptionsManager optionsManager;
	private CustomFieldManager cFldMng;
	private FieldConfigSchemeManager fieldConfigSchemeManager;
	private FieldConfig config;
	private String cFldName;
	public String getcFldName() {
		return cFldName;
	}

	public void setcFldName(String cFldName) {
		this.cFldName = cFldName;
	}

	private CustomField cFld;
	private Options options;
 
//	private Properties p;
//	 
//	public Properties getP() {
//		return p;
//	}
//
//	public void setP(Properties p) {
//		this.p = p;
//	}

	
	 
	
	
	public FldOptions(String defPathTmp, String cFldName) throws Throwable {
		// TODO Auto-generated constructor stub
		this.cFldName = cFldName;
		appKeeper = HtmlTableData.readlistAppKeeperFromFile(defPathTmp);
		init();	

	}
	
//	public FldOptions() throws Throwable {
//		// TODO Auto-generated constructor stub		
//		p=new Properties();
//		InputStream is = getClass().getClassLoader().getResourceAsStream("getcnflappdata.properties");
//		if (is!=null)
//		{
//		  p.load(is);
//		}
//
//	}	
	
	public Map<String, ArrayList<String>> getAppKeeper() {
		return appKeeper;
	}

	public void setAppKeeper(Map<String, ArrayList<String>> appKeeper) {
		this.appKeeper = appKeeper;
	}

	private void init() throws Throwable {				
		optionsManager = ComponentAccessor.getOptionsManager();	
		cFldMng = ComponentAccessor.getCustomFieldManager();	
		cFld = cFldMng.getCustomFieldObject(Long.parseLong(cFldName));		
		fieldConfigSchemeManager =
		ComponentAccessor.getComponent(FieldConfigSchemeManager.class);		
		List<FieldConfigScheme> schemes = fieldConfigSchemeManager.getConfigSchemesForField(cFld);		
		config = schemes.get(0).getOneAndOnlyConfig();	
		options = optionsManager.getOptions(config);	
	}
	
	public String getKeeper (String app) {
		String str = "";
		for (String res : appKeeper.keySet()) {
			if (res.equalsIgnoreCase(app)) {
				str = appKeeper.get(res).get(0);
				break;
			}
		}
		return str;
	}
	
	public void fillOptions(String type) throws Throwable {
		for (Option option : options) {
			boolean fnd = false;
			for (String res : appKeeper.keySet()) {
				if (option.getValue().equalsIgnoreCase(res)) {
					if (option.getDisabled()) {
						optionsManager.enableOption(option);
					}
					fnd = true;
					break;
				}				
			}
			if (!fnd) {				
				optionsManager.disableOption(option);				
			}
		}
		options = optionsManager.getOptions(config);	                
		Integer i = options.size();        
		for (String res : appKeeper.keySet()) {
			boolean isAbsent = true;
			for (Option option : options) {
				if (option.getValue().equalsIgnoreCase(res)) {
					isAbsent = false;
					break;
				} 
			}
			if (isAbsent) {
				i++;
				Option optPar = optionsManager.createOption(config, null,
					i.longValue(), res);			
				options.add(optPar);			
			}
		}	
	}		

}