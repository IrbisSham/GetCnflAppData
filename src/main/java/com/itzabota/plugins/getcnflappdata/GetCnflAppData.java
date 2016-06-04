package com.itzabota.plugins.getcnflappdata;

import java.util.Date;
import java.util.Properties;

public interface GetCnflAppData {
	public Properties getProps();
	public void reschedule(Properties props);	
	public void loadProps(Properties p);

    public Date getLastRun();
    
    public long getInterval();
	public String getDefPathTmp();
	

	public String getFldAppName();
	

	public String getCssSelector();

	public String getColKeeper() ;
	
	public String getFldCascadSelectAppKeeperName();
	
	public String getFldKeeperName();

	public String getConfluenceUrl() ;

	public String getColApp() ;

	public String getConfluencePassword();

	public String getConfluenceLogin() ;
	
	public String getEncoding() ;
	
	
}
