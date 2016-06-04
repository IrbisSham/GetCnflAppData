package com.itzabota.plugins.getcnflappdata;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class SchedulerAction extends JiraWebActionSupport {

	 /**
	 * 
	 */
	
//	private static final String propertyFile = "getcnflappdata.properties"; 
	private final Logger logger = Logger.getLogger(SchedulerAction.class);
	
	private static final long serialVersionUID = 1489174248265833491L;
	private final GetCnflAppData getCnflAppData;
	    private long interval;
	    public String getDefPathTmp() {
			return defPathTmp;
		}

		public void setDefPathTmp(String defPathTmp) {
			this.defPathTmp = defPathTmp;
//			logger.warn("SchedulerAction setDefPathTmp before. props: ");
//			logger.warn(props);
			props.setProperty("getcnflappdata.plugin.defPathTmp", defPathTmp);
//	        setProperty(pathTmpPropFl, "getcnflappdata.plugin.defPathTmp", defPathTmp);
	        setProperty("getcnflappdata.plugin.defPathTmp", defPathTmp);
//	        logger.warn("SchedulerAction setDefPathTmp after. props: ");
//	        logger.warn(props);
		}

		public String getFldAppName() {
			return fldAppName;
		}

		public void setFldAppName(String fldAppName) {
			this.fldAppName = fldAppName;
			props.setProperty("getcnflappdata.plugin.fldAppName", fldAppName);
	        setProperty("getcnflappdata.plugin.fldAppName", fldAppName);
		}

		public String getCssSelector() {
			return cssSelector;
		}

		public void setCssSelector(String cssSelector) {
			this.cssSelector = cssSelector;
			props.setProperty("getcnflappdata.plugin.cssSelector", cssSelector);
	        setProperty("getcnflappdata.plugin.cssSelector", cssSelector);
		}

		public String getColKeeper() {
			return colKeeper;
		}

		public void setColKeeper(String colKeeper) {
			this.colKeeper = colKeeper;
			props.setProperty("getcnflappdata.plugin.colKeeper", colKeeper);
	        setProperty("getcnflappdata.plugin.colKeeper", colKeeper);
		}

		public String getConfluenceUrl() {
			return confluenceUrl;
		}

		public void setConfluenceUrl(String confluenceUrl) {
			this.confluenceUrl = confluenceUrl;
			props.setProperty("getcnflappdata.plugin.confluenceUrl", confluenceUrl);
	        setProperty("getcnflappdata.plugin.confluenceUrl", confluenceUrl);
		}

		public String getColApp() {
			return colApp;
		}

		public void setColApp(String colApp) {
			this.colApp = colApp;
			props.setProperty("getcnflappdata.plugin.colApp", colApp);
	        setProperty("getcnflappdata.plugin.colApp", colApp);
		}

		public String getConfluencePassword() {
			return confluencePassword;
		}

		public void setConfluencePassword(String confluencePassword) {
			this.confluencePassword = confluencePassword;
			props.setProperty("getcnflappdata.plugin.confluencePassword", confluencePassword);
	        setProperty("getcnflappdata.plugin.confluencePassword", confluencePassword);
		}

		public String getConfluenceLogin() {
			return confluenceLogin;
		}

		public void setConfluenceLogin(String confluenceLogin) {
			this.confluenceLogin = confluenceLogin;
			props.setProperty("getcnflappdata.plugin.confluenceLogin", confluenceLogin);
	        setProperty("getcnflappdata.plugin.confluenceLogin", confluenceLogin);
		}

		private String defPathTmp;
		private String fldAppName;
		private String fldKeeperName;
		public String getFldKeeperName() {
			return fldKeeperName;
		}

		public void setFldKeeperName(String fldKeeperName) {
			this.fldKeeperName = fldKeeperName;
			props.setProperty("getcnflappdata.plugin.fldKeeperName", fldKeeperName);
			setProperty("getcnflappdata.plugin.fldKeeperName", fldKeeperName);
		}

		private String fldCascadSelectAppKeeperName;
		public String getFldCascadSelectAppKeeperName() {
			return fldCascadSelectAppKeeperName;
		}

		public void setFldCascadSelectAppKeeperName(String fldCascadSelectAppKeeperName) {
			this.fldCascadSelectAppKeeperName = fldCascadSelectAppKeeperName;
			props.setProperty("getcnflappdata.plugin.fldCascadSelectAppKeeperName", fldCascadSelectAppKeeperName);
			setProperty("getcnflappdata.plugin.fldCascadSelectAppKeeperName", fldCascadSelectAppKeeperName);
		}

		private String encoding;
		public String getEncoding() {
			return encoding;
		}

		public void setEncoding(String encoding) {
	        this.encoding = encoding;
	        props.setProperty("getcnflappdata.plugin.encoding", encoding);
	        setProperty("getcnflappdata.plugin.encoding", encoding);
		}

		private String cssSelector;
		private String colKeeper;
		private String confluenceUrl;
		private String colApp;
		private String confluencePassword;
		private String confluenceLogin;
		private Properties props;

		private JiraHome defJiraHome = ComponentAccessor.getComponentOfType(JiraHome.class);

		private String fullPathTmp;
		
		private static final String propertyFile = "getcnflappdata.properties";


//		private String pathTmpPropFl;
		
		
	    public SchedulerAction(GetCnflAppData getCnflAppData) {		
			fullPathTmp = defJiraHome.getLocalHomePath().concat(File.separator).concat("tmp").concat(File.separator).concat("wrk_").concat(propertyFile);
	        this.getCnflAppData = getCnflAppData;
	        this.props = getCnflAppData.getProps();
//	    	logger.warn("SchedulerAction constructor. props: ");
//	    	logger.warn(props);
	        this.encoding = getCnflAppData.getEncoding();
	        this.interval = getCnflAppData.getInterval();
	        this.colApp = getCnflAppData.getColApp();
	        this.colKeeper = getCnflAppData.getColKeeper();
	        this.confluenceLogin = getCnflAppData.getConfluenceLogin();
	        this.confluencePassword = getCnflAppData.getConfluencePassword();
	        this.confluenceUrl = getCnflAppData.getConfluenceUrl();
	        this.cssSelector = getCnflAppData.getCssSelector();
	        this.defPathTmp = getCnflAppData.getDefPathTmp();
	        this.fldAppName = getCnflAppData.getFldAppName();
	        this.fldKeeperName = getCnflAppData.getFldKeeperName();
	        this.fldCascadSelectAppKeeperName = getCnflAppData.getFldCascadSelectAppKeeperName();
	    }

	    @Override
	    protected String doExecute() throws Exception {
	        return SUCCESS;
	    }

	    public String doReschedule() {
//	    	logger.warn("Rescheduled Props: ");
//	    	logger.warn(props);
	    	getCnflAppData.reschedule(props);
	        return getRedirect("GetCnflAppDataScheduler!default.jspa");
	    }

	    public long getInterval() {
	        return interval;
	    }

	    public void setInterval(long interval) {
	        this.interval = interval;
	        props.setProperty("getcnflappdata.plugin.intervalJob", String.valueOf(interval));
	        setProperty("getcnflappdata.plugin.intervalJob", String.valueOf(interval));
	        
	    }

	    public Date getLastRun() {
	        return getCnflAppData.getLastRun();
	    }	
	
	    // String flPath
	    public void setProperty(String propertName, String propertyValue) {    	
	    	OutputStream out = null;
	        try {

	            Properties props = new Properties();

	            File f = new File(fullPathTmp);
	            if(f.exists()){

	                props.load(new FileReader(f));
	                //Change your values here
	                props.setProperty(propertName, propertyValue);
	            }

	            out = new FileOutputStream( f );
	            SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");	            
	            props.store(out, "Changed ".concat(dt.format(new Date())));
	        }
	        catch (Exception e ) {
	            e.printStackTrace();
	        }
	        finally{

	            if(out != null){

	                try {

	                    out.close();
	                } 
	                catch (IOException ex) {

	                    logger.error("IOException: Could not close".concat(fullPathTmp).concat(" output stream; ").concat(ex.getMessage()));
	                    ex.printStackTrace();
	                }
	            }
	        }
	            
	            
		}
	        
	        
	    
}
