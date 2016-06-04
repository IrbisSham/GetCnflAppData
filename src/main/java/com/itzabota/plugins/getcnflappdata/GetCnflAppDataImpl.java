package com.itzabota.plugins.getcnflappdata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.scheduling.PluginScheduler;

public class GetCnflAppDataImpl implements GetCnflAppData, LifecycleAware {

    /* package */ static final String KEY = GetCnflAppDataImpl.class.getName() + ":instance";
    private static final String JOB_NAME = GetCnflAppDataImpl.class.getName() + ":job";

    private final Logger logger = Logger.getLogger(GetCnflAppDataImpl.class);
    private final PluginScheduler pluginScheduler;  // provided by SAL

    private long interval = 30000L;      // default job interval (30 sec)
    private Date lastRun = null;        // time when the last search returned
    
    private String encoding;
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}



	private String defPathTmp;
	private String fldAppName;
	private String fldKeeperName;
	private String cssSelector;
	private String colKeeper;
	private String confluenceUrl;
	private String colApp;
	private String confluencePassword;
	private String confluenceLogin;
	private String fldCascadSelectAppKeeperName;
	private Properties props;
	 
	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public String getDefPathTmp() {
		return defPathTmp;
	}

	public String getFldAppName() {
		return fldAppName;
	}

	public String getCssSelector() {
		return cssSelector;
	}

	public String getColKeeper() {
		return colKeeper;
	}

	public String getConfluenceUrl() {
		return confluenceUrl;
	}

	public String getColApp() {
		return colApp;
	}

	public String getConfluencePassword() {
		return confluencePassword;
	}

	public String getConfluenceLogin() {
		return confluenceLogin;
	}


	public String getFldKeeperName() {
		return fldKeeperName;
	}


	public String getFldCascadSelectAppKeeperName() {
		return fldCascadSelectAppKeeperName;
	}


	
	private static final String propertyFile = "getcnflappdata.properties";

    public GetCnflAppDataImpl(PluginScheduler pluginScheduler) throws Throwable {

		
        this.pluginScheduler = pluginScheduler;
        loadProperties();
    }

    public void loadProperties() throws IOException, URISyntaxException, Throwable {
    	
    	    	
    	InputStream is = getClass().getClassLoader().getResourceAsStream(propertyFile);
    	
		props=new Properties();		
		 
		if (is!=null)
		{
			props.load(is);
		}
		JiraHome defJiraHome = ComponentAccessor.getComponentOfType(JiraHome.class);
		String fullPathTmp = defJiraHome.getLocalHomePath().concat(File.separator).concat("tmp").concat(File.separator).concat("wrk_").concat(propertyFile);		
		File f = new File(fullPathTmp);
		if (f.exists()) {
			f.delete();
		}
		FileOutputStream out = new FileOutputStream(f);
        props.store(out, "");
        out.close();
        
		loadProps(props);
		
		is.close();	
		logger.info("Properties of plugin getcnflappdata initially loaded");
//		interval = Long.valueOf(p.getProperty("getcnflappdata.plugin.intervalJob"));
//		defPathTmp = p.getProperty("getcnflappdata.plugin.defPathTmp");			
//		fldAppName = p.getProperty("getcnflappdata.plugin.fldAppName");
//		fldKeeperName = p.getProperty("getcnflappdata.plugin.fldKeeperName");
//		fldCascadSelectAppKeeperName = p.getProperty("getcnflappdata.plugin.fldCascadSelectAppKeeperName");
//		confluenceUrl = p.getProperty("getcnflappdata.plugin.confluenceUrl");
//		cssSelector = p.getProperty("getcnflappdata.plugin.cssSelector");
//		colApp = p.getProperty("getcnflappdata.plugin.colApp");
//		colKeeper = p.getProperty("getcnflappdata.plugin.colKeeper");	
//		confluencePassword = p.getProperty("getcnflappdata.plugin.confluencePassword");		
//		confluenceLogin = p.getProperty("getcnflappdata.plugin.confluenceLogin");
    }
    
    public void loadProps(Properties p) {
		Enumeration<?> e = p.propertyNames();

	    while (e.hasMoreElements()) {
	      String key = (String) e.nextElement();
	      String value = p.getProperty(key);
	      switch (key) {
			case "getcnflappdata.plugin.intervalJob":
				this.interval = Long.valueOf(value);
				break;
			case "getcnflappdata.plugin.defPathTmp":
				this.defPathTmp = value;
				break;
			case "getcnflappdata.plugin.fldAppName":
				this.fldAppName = value;
				break;
			case "getcnflappdata.plugin.fldKeeperName":
				this.fldKeeperName = value;
				break;
			case "getcnflappdata.plugin.fldCascadSelectAppKeeperName":
				this.fldCascadSelectAppKeeperName = value;
				break;
			case "getcnflappdata.plugin.confluenceUrl":
				this.confluenceUrl = value;
				break;
			case "getcnflappdata.plugin.cssSelector":
				this.cssSelector = value;
				break;
			case "getcnflappdata.plugin.colApp":
				this.colApp = value;
				break;
			case "getcnflappdata.plugin.colKeeper":
				this.colKeeper = value;
				break;
			case "getcnflappdata.plugin.confluencePassword":
				this.confluencePassword = value;
				break;
			case "getcnflappdata.plugin.confluenceLogin":
				this.confluenceLogin = value;	
				break;	
			case "getcnflappdata.plugin.encoding":
				this.encoding = value;	
				break;									
			default:
				break;
			}
	    }
	}
    
    // declared by LifecycleAware
    public void onStart() {
        reschedule(props);
    }

    public void reschedule(Properties props) {
    	loadProps(props);
        
        pluginScheduler.scheduleJob(
                JOB_NAME,                   // unique name of the job
                GetCnflAppDataTask.class,     // class of the job
                new HashMap<String,Object>() {/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

				{
                    put(KEY, GetCnflAppDataImpl.this);
                }},                         // data that needs to be passed to the job
                new Date(),                 // the time the job is to start
                interval);                  // interval between repeats, in milliseconds
        logger.info(String.format("Confluence Application Data task scheduled to run every %dms", interval));
    }


    /* package */ void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }
    
    public long getInterval() {
        return interval;
    }

    public Date getLastRun() {
        return lastRun;
    }
 
}




