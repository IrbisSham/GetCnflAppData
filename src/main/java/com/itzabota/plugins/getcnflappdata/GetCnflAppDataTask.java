package com.itzabota.plugins.getcnflappdata;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.itzabota.plugins.getcnflappdata.FldOptions;
import com.itzabota.plugins.getcnflappdata.HtmlTableData;

public class GetCnflAppDataTask implements PluginJob {

    private final Logger logger = Logger.getLogger(GetCnflAppDataTask.class);

    /**
     * Executes this job.
     *
     * @param jobDataMap any data the job needs to execute. Changes to this data will be remembered between executions.
     */
    public void execute(Map<String, Object> jobDataMap) {

		try {
//			Properties p=new Properties();
	    	JiraHome defJiraHome = ComponentAccessor.getComponentOfType(JiraHome.class);
			
			final GetCnflAppDataImpl monitor = (GetCnflAppDataImpl)jobDataMap.get(GetCnflAppDataImpl.KEY);
		    monitor.setLastRun(new Date());
		    
		    String encoding = monitor.getEncoding();
			String defPathTmp = monitor.getDefPathTmp();			
			String fldAppName =  monitor.getFldAppName();
			String confluenceUrl =  monitor.getConfluenceUrl();
			String cssSelector =  monitor.getCssSelector();
			String colApp =  monitor.getColApp();
			String colKeeper =  monitor.getColKeeper();	
			String confluencePassword =  monitor.getConfluencePassword();		
			String confluenceLogin =  monitor.getConfluenceLogin();
		    
			HtmlTableData htmlTblData = new HtmlTableData(confluenceUrl, cssSelector, colApp, colKeeper, confluenceLogin, confluencePassword, encoding);
			htmlTblData.feelData();			
			String fullPathTmp = defJiraHome.getLocalHomePath().concat(File.separator).concat(defPathTmp).concat(File.separator);		
			htmlTblData.writelistAppKeeperToFile(fullPathTmp);
			if (htmlTblData.getFlModified() == 1) {
				FldOptions fldOptions = new FldOptions(fullPathTmp, fldAppName);
				fldOptions.fillOptions("multiselect");
			}
		} catch (Throwable te) {
			logger.error("Error talking to HtmlTableData: " + te.getMessage(), te);
		}
		
    }
}