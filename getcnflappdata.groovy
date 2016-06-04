import com.atlassian.jira.config.util.JiraHome;

import com.atlassian.jira.ComponentManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.component.ComponentAccessor;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.ApplicationUser;

import com.atlassian.jira.issue.IssueManager;

import com.atlassian.jira.issue.fields.CustomField;

import java.io.InputStream;
import java.util.Properties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
 
import com.atlassian.jira.issue.MutableIssue; 
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;

import org.apache.log4j.Category;

import 	com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.IssueInputParameters;
import 	com.atlassian.jira.bc.issue.IssueService.UpdateValidationResult;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.crowd.embedded.api.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

String propertyFile = "getcnflappdata.properties";

log = Category.getInstance("com.itzabota.groovy.UpdateKeeper")
log.warn("com.itzabota.groovy.UpdateKeeper");

ComponentManager componentManager = ComponentManager.getInstance();
 
PluginAccessor pluginAccessor = componentManager.getPluginAccessor();
Class fldOptionsClass = pluginAccessor.getClassLoader().findClass("com.itzabota.plugins.getcnflappdata.FldOptions");

JiraHome defJiraHome = ComponentAccessor.getComponentOfType(JiraHome.class);
Properties p=new Properties();
String propPathTmp = defJiraHome.getLocalHomePath().concat(File.separator).concat("tmp").concat(File.separator).concat("wrk_").concat(propertyFile);
 
//InputStream is = fldOptionsClass.getClassLoader().getResourceAsStream("getcnflappdata.properties");
File f = new File(propPathTmp);
if(f.exists()){
	p.load(new FileReader(f));
}	
		
String defPathTmp = p.getProperty("getcnflappdata.plugin.defPathTmp");			
String fldKeeperName = p.getProperty("getcnflappdata.plugin.fldKeeperName");
String fldAppName = p.getProperty("getcnflappdata.plugin.fldAppName");


String fullPathTmp = defJiraHome.getLocalHomePath().concat(File.separator).concat(defPathTmp).concat(File.separator);

cIssMng = ComponentAccessor.getIssueManager();
MutableIssue currentIssue = issue;//cIssMng. getIssueByCurrentKey("METLIFE-81");

cFldMng = ComponentAccessor.getCustomFieldManager();	
cfldKeeperName = cFldMng.getCustomFieldObject(Long.parseLong(fldKeeperName));
cfldAppName = cFldMng.getCustomFieldObject(Long.parseLong(fldAppName));

cUserMng = ComponentAccessor.getUserManager();	

String appVal = (String) currentIssue.getCustomFieldValue(cfldAppName);	
ApplicationUser keeperValOld = cUserMng.getUserByKey (currentIssue.getCustomFieldValue(cfldKeeperName));		
		
ctor = fldOptionsClass.getDeclaredConstructors();
obj = ctor[0].newInstance(fullPathTmp, fldKeeperName);		

Method getKeeperMethod = fldOptionsClass.getMethod("getKeeper", java.lang.String.class);
String keeperValNewStr = getKeeperMethod.invoke(obj, appVal);		

ApplicationUser keeperValNew = cUserMng.getUserByKey (keeperValNewStr);		

log.warn(keeperValOld);
log.warn(keeperValNew);

ModifiedValue modifiedValue = new ModifiedValue(keeperValOld,
keeperValNew);

log.warn(modifiedValue);

FieldLayoutManager fieldLayoutManager = ComponentAccessor.
getFieldLayoutManager();

FieldLayoutItem fieldLayoutItem = fieldLayoutManager.
getFieldLayout(currentIssue).getFieldLayoutItem(cfldKeeperName);

DefaultIssueChangeHolder issueChangeCholder = new 
DefaultIssueChangeHolder();

cfldKeeperName.updateValue(fieldLayoutItem, currentIssue, modifiedValue, issueChangeCholder);
issueChangeCholder.getChangeItems();

IssueService issueService = ComponentAccessor.getIssueService();
IssueInputParameters issueInputParameters = issueService.
newIssueInputParameters();

issueInputParameters.addCustomFieldValue(cfldKeeperName.
getIdAsLong(), "New Value with event");

User loggedInUser = ComponentAccessor.
getJiraAuthenticationContext().getUser().getDirectoryUser();
UpdateValidationResult validationResult = issueService.
validateUpdate(loggedInUser, currentIssue.getId(), issueInputParameters);
if (validationResult.isValid()){
	IssueResult result = issueService.update(loggedInUser,
	validationResult);
	if (result.isValid()){
		Systen.out.println("New value Set!");
	}
}

