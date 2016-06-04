package com.itzabota.plugins.getcnflappdata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class HtmlTableData {
	
    private static Logger logger = Logger.getLogger(HtmlTableData.class);
    
    private String Encoding = "cp1251"; // UTF-8
    
    public ArrayList<String> getListApp() {
		return listApp;
	}
	public void setListApp(ArrayList<String> listApp) {
		this.listApp = listApp;
	}
	public Element getTblAppKeeper() {
		return tblAppKeeper;
	}
	public void setTblAppKeeper(Element tblAppKeeper) {
		this.tblAppKeeper = tblAppKeeper;
	}

	private ArrayList<String> listApp = new ArrayList<>();
    
    private Map<String, ArrayList<String>> listAppKeeper = new TreeMap<>();
    
    public Map<String, ArrayList<String>> getListAppKeeper() {
		return listAppKeeper;
	}
	public void setListAppKeeper(Map<String, ArrayList<String>> listAppKeeper) {
		this.listAppKeeper = listAppKeeper;
	}

	private Element tblAppKeeper;
    
    private Integer flModified = 0;
    
    public static String appKeeperFlName = "AppKeeper.ar1";
    
    public static String appKeeperFlNameTmp = "AppKeeperTmp.ar1";
    

	public Integer getFlModified() {
		return flModified;
	}
	public void setFlModified(Integer flModified) {
		this.flModified = flModified;
	}

	public static String separatorApp = "|";
    
    public static String separatorKeeper = "?";

    public static String lineSaparator = System.getProperty("line.separator");
    
	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public String getCssSelector() {
		return cssSelector;
	}

	public void setCssSelector(String cssSelector) {
		this.cssSelector = cssSelector;
	}

	private Document doc;	
	private String cssSelector;
    public String getColApp() {
		return colApp;
	}

	public void setColApp(String colApp) {
		this.colApp = colApp;
	}

	public String getColKeeper() {
		return colKeeper;
	}

	public void setColKeeper(String colKeeper) {
		this.colKeeper = colKeeper;
	}

	private String colApp;
    private String colKeeper;

	private String pathFile;

	public String getConfluenceLogin() {
		return confluenceLogin;
	}
	public void setConfluenceLogin(String confluenceLogin) {
		this.confluenceLogin = confluenceLogin;
	}
	public String getConfluencePassword() {
		return confluencePassword;
	}
	public void setConfluencePassword(String confluencePassword) {
		this.confluencePassword = confluencePassword;
	}

	private String confluenceLogin;

	private String confluencePassword;
    
    public String getPathFile() {
		return pathFile;
	}

	public void setPathFile(String pathFile) {
		this.pathFile = pathFile;
	}

	HtmlTableData(String pathFile) throws Throwable {    	
    	
    }
	
    
    public HtmlTableData(String pathFile, String cssSelector, String colApp, String colKeeper, String confluenceLogin, String confluencePassword, String encoding) throws Throwable {
    	if (pathFile == null || pathFile.equals("")) {
    		logger.error("pathFile is null or empty!");
    		throw new Throwable();
    	}
    	if (cssSelector == null || cssSelector.equals("") ) {
    		logger.error("cssSelector is empty or null!");
    		throw new Throwable();
    	}		
    	if (encoding != null) {
    		this.Encoding = encoding;
    	}
    	this.confluenceLogin = confluenceLogin;
    	this.confluencePassword = confluencePassword;
		this.pathFile = pathFile;
		this.cssSelector = cssSelector;
		this.colApp = colApp;
		this.colKeeper = colKeeper;
		
		if (pathFile.toLowerCase().contains("http")) {
			if (pathFile.toLowerCase().contains("https")) {
				prepareHttps();
			}
			urlWork();
		}
		else {
			File input = new File(pathFile);
			doc = Jsoup.parse(input, Encoding, "");
		}
	}
		
	private void urlWork() throws IOException {
		String auth = getAuthentificationString(confluenceLogin, confluencePassword);
		String result = invokeGetMethod(auth, pathFile);
		BufferedWriter writer = null;
		boolean isWrite = false;
		try
		{
		    writer = new BufferedWriter( new FileWriter("tmpCnfl"));
		    writer.write(result);
		    isWrite = true;
		}
		catch ( IOException e)
		{
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( IOException e)
		    {
		    }
		}
		File input = new File("tmpCnfl");			
		if (isWrite) {
			doc = Jsoup.parse(input, Encoding, ""); //Jsoup.connect(pathFile).get();
			input.delete();
		}
		else {
			logger.error("There are no authority write to path ".concat(input.getCanonicalPath()));
		}
	}
    
	public static void prepareHttps() throws NoSuchAlgorithmException, KeyManagementException {
	       // Create a trust manager that does not validate certificate chains
     TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
             public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                 return null;
             }
             public void checkClientTrusted(X509Certificate[] certs, String authType) {
             }
             public void checkServerTrusted(X509Certificate[] certs, String authType) {
             }
         }
     };

     // Install the all-trusting trust manager
     SSLContext sc = SSLContext.getInstance("SSL");
     sc.init(null, trustAllCerts, new java.security.SecureRandom());
     HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

     // Create all-trusting host name verifier
     HostnameVerifier allHostsValid = new HostnameVerifier() {
         public boolean verify(String hostname, SSLSession session) {
             return true;
         }
     };

     // Install the all-trusting host verifier
     HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);		
	}	    
    
   public static String getAuthentificationString(String username, String password) {
    	String authString = username + ":" + password;
    	byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		return new String(authEncBytes);			        	    	
	}    
    
	public static String invokeGetMethod(String auth, String url) throws AuthenticationException, ClientHandlerException {
        Client client = Client.create();
        WebResource webResource = client.resource(url);
        ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
                .accept("application/json").get(ClientResponse.class);
        int statusCode = response.getStatus();
        if (statusCode == 401) {
            throw new AuthenticationException("Имя пользователя или пароль ошибочны");
        }
        if (statusCode == 403) {
            throw new AuthenticationException("Недостаточно прав для выполнения операции");
        }    
        if (statusCode == 404) {
            throw new AuthenticationException("Данный ресурс не существует");
        }             
        return response.getEntity(String.class);
    }     


	
// Get Name or Key	
    public static String parse(String str, String parameter) {
    	String lStr = str.replace("&nbsp;", "");    	
    	if (parameter.equalsIgnoreCase("APP")) {
    		return lStr.trim();
    	}

    	if (lStr.toLowerCase().contains("data-username=\"") && lStr.toLowerCase().contains("\" href=")) {
    		return lStr.substring(lStr.indexOf("data-username=\"")+"data-username=\"".length(), lStr.indexOf("\" href=")).trim();
    	}
    	else {
    		return "";
    	}

    	// Get FIO
//        public static String parse(String str) {
//        	String lStr = str.replace("&nbsp;", "");
//        	if (lStr.toLowerCase().contains("<a")) {
//        		return lStr.substring(lStr.indexOf(">")+1, lStr.indexOf("</a>")).trim(); //  - str.indexOf(">")
//        	}
//        	else {
//        		return lStr.trim();
//        	}
//        	
    	
    	
//    	<a href="mailto:Tatiana.Filatova@metlife.ru" class="external-link" rel="nofollow">Филатова Татьяна</a>
    	
// <a href="https://confluence.alico.ru/display/ITKNOW/Alfa+bank+and+Alfa+click" rel="nofollow">Alfa bank and Alfa click</a>    	
	}
    
    public void writelistAppKeeperToFile(String pathFile) throws IOException {
    	BufferedWriter writer = null;
    	File fileOutTmp = null;
    	File fileOut = new File(pathFile.concat(appKeeperFlName));
    	if (fileOut.exists()) {
    		fileOutTmp = new File(pathFile.concat(appKeeperFlNameTmp));
    		if (fileOutTmp.exists()) {
    			fileOutTmp.delete();
    		}
    		FileUtils.copyFile(fileOut, fileOutTmp);
    		if (fileOut.canWrite()) {
            	fileOut.delete();    			
    		}
    		else {
    			flModified = 0;
    			logger.warn("File ".concat(fileOut.getCanonicalPath()).concat(" is busy"));
    			return;
    		}
    		
    	}
       	try {                   
//    		writer = new BufferedWriter(new FileWriter(fileOut));
    		writer = new BufferedWriter
    			    (new OutputStreamWriter(new FileOutputStream(fileOut),Encoding));
    		for(Entry<String, ArrayList<String>> entry : listAppKeeper.entrySet()) {
    		    String app = entry.getKey();
    		    ArrayList<String> value = entry.getValue();
    		    writer.write(app.concat(separatorApp));
    		    if (value.size() > 0) {
        		    for (String keeper : value) {
        		    	writer.write(keeper.concat(separatorKeeper));
    				}        		    
    		    }
    		    else {
    		    	writer.write(separatorKeeper);
    		    }
    		    writer.write(lineSaparator);

    		}    	  
            writer.flush();
            flModified = 1;
            logger.debug("Array of applications and their keepers were saved to ".concat(fileOut.getCanonicalPath()));
            if (fileOutTmp != null) {
            	fileOutTmp.delete();	
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...      		  
                writer.close(); 
            } catch (Exception e) {
            }
        }
        
	}
    
    public void printlistAppKeeper() {    	
		for(Entry<String, ArrayList<String>> entry : listAppKeeper.entrySet()) {
		    String app = entry.getKey();
		    ArrayList<String> value = entry.getValue();
		    String str = app.concat(separatorApp);
		    if (value.size() > 0) {
    		    for (String keeper : value) {
    		    	str = str.concat(keeper.concat(separatorKeeper));
				}        		    
		    }
		    else {
		    	str = str.concat(separatorKeeper);
		    }
		    str = str.concat(lineSaparator);
		    logger.debug(str);
		}    	  
	}
    
    public static HashMap<String, ArrayList<String>> readlistAppKeeperFromFile(String pathFile) {
    	HashMap<String, ArrayList<String>> listAppKeeperLocal = new HashMap<>();    	
    	File fileIn = new File(pathFile.concat(appKeeperFlName));
    	File fileOutTmp = new File(pathFile.concat(appKeeperFlNameTmp));
    	File fileWrk = null;
    	if (fileIn.exists() && fileIn.canWrite()) {
    		fileWrk = fileIn;
    	}
    	else {
    		fileWrk = fileOutTmp;
    	}
    	if (fileWrk.exists()) {	
    		Scanner scanner = null;
    			try {    				
    				scanner = new Scanner(fileWrk);    				
    		        scanner.useDelimiter(lineSaparator);
    		        String nextExp;    		    		        
    		        while (scanner.hasNext())
    		        {	      	
    	        		nextExp = scanner.nextLine();    	        		
//    	        		logger.debug("Строка пользователей джиры:" + nextExp);
    	        		String[] nextExpAr = nextExp.split("\\".concat(separatorApp));    
    	        		String[] expKeeper = null;
    	        		if (nextExpAr.length > 1) {
    	        			expKeeper = nextExpAr[1].split("\\".concat(separatorKeeper));        	        		
    	        		}
//    	        		for (int i = 0; i < nextExpAr.length; i++) {
//    	        			logger.debug("Пользователь джиры:" + nextExpAr[i]);
//						}
    	        		ArrayList<String> listKeeperLocal = new ArrayList<>();
    	        		for (String str : expKeeper) {
    	        			listKeeperLocal.add(str);    	        			
						}        	    	        		
        		    	listAppKeeperLocal.put(nextExpAr[0], listKeeperLocal);    	    	        		
    		        }
    			} 
    			catch (UnsupportedOperationException e) {
    				logger.error("Unsupported Operation Exception ".concat(e.getMessage()));
    				e.printStackTrace();
    			}
    			catch (FileNotFoundException e) {
    				logger.error("File of applications and their keepers wasn't find on path "+pathFile);
    				logger.error("File Not Found Exception ".concat(e.getMessage()));
    				e.printStackTrace();    				
    			}
    			catch (ClassCastException  e) {
    				logger.error("Class Cast Exception ".concat(e.getMessage()));    				
    				e.printStackTrace();
    			}
    			catch (IllegalArgumentException  e) {
    				logger.error("Illegal Argument Exception ".concat(e.getMessage()));    				
    				e.printStackTrace();
    			}
    			catch (NullPointerException  e) {
    				logger.error("Null Pointer Exception ".concat(e.getMessage()));    				
    				e.printStackTrace();
    			}
    			finally {				   				
			        scanner.close();
				}    	         
    		logger.debug("Reading array of applications and their keepers...");
    	}
    	else {
    		String s = "File of applications and their keepers wasn't find on path "+pathFile;
    		logger.error(s);    
    		FileNotFoundException e = new FileNotFoundException(s);    				
			e.printStackTrace();   				
		}
    	return listAppKeeperLocal;
	}
    
	public void feelData() {
		Integer colapp = 0;
		Integer colkeeper = 0;
    	if (colApp.equalsIgnoreCase("FIRST")) {
    		colapp = 0;
    	}    	
        Element table = doc.select(cssSelector).get(0); //select the first table.
        Elements rows = table.select("tr");

        for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
            Element row = rows.get(i);
            Elements cols = row.select("td");
            if (colKeeper.equalsIgnoreCase("LAST")) {
            	colkeeper = cols.size() - 1;
            }            
            String appCur = cols.get(colapp).text().replace("\u00a0", "").trim();
            if (!appCur.equals("")) {
            	appCur = parse(appCur, "APP");
                String keeperCur = cols.get(colkeeper).toString().replace("\u00a0", "");                
                if (!listApp.contains(appCur)) {
            		logger.debug(appCur);                	
                	listApp.add(appCur);
                	keeperCur = parse(keeperCur, "KEEPER");
                	ArrayList<String> listKeeper = new ArrayList<>();
                	listKeeper.add(keeperCur);
                	if (appCur != null && !appCur.equalsIgnoreCase("") ) {
                		listAppKeeper.put(appCur, listKeeper);                		
                	}
                }            	
            }
        }
        
        logger.debug("colApp feeled".concat(String.valueOf(colApp.length())));
    }		
	}
    

