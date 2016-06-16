package org.opengis.te.stats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class TEReport {

	String username;
	String session;
	String date;
	String test;
	String result;

	public static HashMap<String, String> properties = new HashMap<String, String>();
	
	public void processResult(File userDirPath, File reportFileName) {

		String[] rootDirs = userDirPath.list();

		for (int i = 0; i < rootDirs.length; i++) {

			// Set username
			setUsername(rootDirs[i]);

			// Get the Session List
			String[] sessionList = new File(userDirPath, rootDirs[i]).list();

			if (null != sessionList && sessionList.length > 0) {

				for (int j = 0; j < sessionList.length; j++) {

					//	Set Session
					setSession(sessionList[j]);
					File logFile;
					File sessionFile;
					if (new File(new File(userDirPath, rootDirs[i]),sessionList[j]).isDirectory() && new File(new File(new File(userDirPath, rootDirs[i]), sessionList[j]), "session.xml").exists()) {

						try {
							logFile = new File(new File(new File(userDirPath, rootDirs[i]), sessionList[j]),"log.xml");
							sessionFile = new File(new File(new File(userDirPath, rootDirs[i]), sessionList[j]),"session.xml");

							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							dbf.setValidating(false);
							dbf.setNamespaceAware(true);
							DocumentBuilder docBuilder = dbf.newDocumentBuilder();
							docBuilder.setErrorHandler(new TEReportErrorHandler());
							InputStream in = new FileInputStream(sessionFile);
							Document doc = docBuilder.parse(new InputSource(new InputStreamReader(in, "UTF-8")));

							NodeList sessionAttributeList  = doc.getElementsByTagName("session");
							Element sessionElement = (Element) sessionAttributeList.item(0);
							
							setSession(sessionElement.getAttribute("id"));
							setTest(sessionElement.getAttribute("sourcesId"));
							if (!sessionElement.hasAttribute("date")) {
								Path file = sessionFile.toPath();
		    	                BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
		    	                
		    			        Date dates=new DateTime( attr.creationTime().toString() ).toDate();
		    			        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		    			        String fileCreatinDate=dateFormat.format(dates);
//		    					session.setAttribute("date", currentdate);
								setDate(fileCreatinDate);
							} else {
								setDate(sessionElement.getAttribute("date"));
							}
							/*
							 *  Get Final Result from log file.
							 */
								getFinalResult(logFile);
							
							/*
							 *  It will write the current session info into report.
							 */
							reportWritter(reportFileName);

						} catch (SAXParseException pe) {
							System.out.println("INVALID XML CHAR IN SESSION at ->" + rootDirs[i]+ "/" + sessionList[j]);
						} catch (NullPointerException npe) {
							System.out.println("Error:Mandatory values are Null at ->" + rootDirs[i]+ "/" + sessionList[j]);
						} catch (Exception e) {							
							System.out.println("Execption occured: "+ e.toString() + " at -> "  + rootDirs[i]+ "/" + sessionList[j]);
						}
					}
				}
			}

		}
	}

	/*
	 * Get the overall result from the log file.
	 */
	public void getFinalResult(File logFile){

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			dbf.setValidating(false);
			docBuilder.setErrorHandler(new TEReportErrorHandler());
			InputStream inputStream = new FileInputStream(logFile);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			Document doc = docBuilder.parse(logFile);

			NodeList logElementList = doc.getElementsByTagName("log");

			Element logElement = (Element) logElementList.item(0);
			NodeList testResult = logElement.getElementsByTagName("endtest");

			if (testResult.getLength() == 0) {
				throw new NoSuchElementException(
						"The 'endtest' element not found in log file.");
			} else {
				Element resultStatus = (Element) testResult.item(0);

				if (resultStatus.hasAttribute("result")
						&& !resultStatus.getAttribute("result").equals("")) {
					setResult(resultStatus.getAttribute("result"));
				} else {
					throw new RuntimeException(
							"The 'result' attribute not found or having the NULL value in log file.");

				}
			}
		} catch (SAXParseException pe) {
			setResult("Not Found");
			System.out.println("XML DOC ERROR at -> " + logFile);

		} catch (FileNotFoundException fnfe) {
			setResult("Not Found");
			System.out.println("LOG FILE NOT FOUND at -> " + logFile);

		} catch (NullPointerException npe) {
			setResult("Not Found");
			System.out.println("Mandatory values are null"+npe.getCause()+" at -> " + logFile);

		} catch (Exception e) {
			setResult("Not Found");
			System.out.println("Error: " + e.getMessage()+ "at here-> " + logFile);

		}


	}
	
	/*
	 * This method will write the result into report file.
	 */
	public void reportWritter(File reportFileName){

		BufferedWriter outputFile=null;
		//	    Write the result into file;
		try{
			FileWriter resultsWritter=new FileWriter(reportFileName, true);
			outputFile = new BufferedWriter(resultsWritter);
			// "userName | session | date | testName | overallResult"
			String result = this.getUsername() + "," + this.getSession() + "," + this.getDate() + "," + getTestShortName(this.getTest()) + "," + getResultDescription(this.getResult());
			outputFile.newLine();
			outputFile.write(result);

			outputFile.close();
		}catch(IOException io){
			System.out.println("Exception while writting file.");
			io.printStackTrace();
		}

	}
	
	/*
	 * This method will return the short name of test.
	 */
	public String getTestShortName(String test) {

		String str1 = test.substring(0, test.lastIndexOf("_"));
		String str2 = str1.substring(str1.indexOf("_") + 1);
		String shortName = (String) properties.get(str2);
		// Check if the property is not exist in map then return as it is.
		if (shortName == null) {
			return test;
		}
		return shortName;
	}
	
	public void loadProperties(){
		InputStream input = null;
		/*
		 * Load properties from the property file(resources/config.properties).  
		 */
		try {
			input = getClass().getResourceAsStream("/config.properties");
			if(input == null){
				System.out.println("Unable to find the config.properties.");
				return;
			}
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] property = line.split("=");
			//	update properties map
				properties.put(property[0], property[1]);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	public static void main(String[] args) {

		String userdir = args[0];
		File userDirPath = new File(userdir);
		
		//  Create TE Report File

		BufferedWriter outputFile=null;

		File dir=new File(System.getProperty("user.dir") + File.separator + "result-output" );
		if(!dir.exists()){
			if(!dir.mkdirs()){
				System.out.println("Failed to create directory!");
			}
		}
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd-HHmmss");//dd/MM/yyyy
		Date now = new Date();
		String currentdate = sdfDate.format(now);
		String resultFileName="teamengine-statistics-" + currentdate;
		File finalResult = new File(dir + File.separator + resultFileName + ".txt");
		if(finalResult.exists()){
			finalResult.delete();
		}
		try{
			FileWriter resultsWritter=new FileWriter(finalResult, true);
			outputFile = new BufferedWriter(resultsWritter);
			outputFile.write("userName,session,date,testName,overallResult");
			outputFile.close();
		}catch(IOException io){
			System.out.println("Exception while writting file.");
			io.printStackTrace();
		}

		TEReport te = new TEReport();
		te.loadProperties();
		te.processResult(userDirPath, finalResult);

		System.out.println("\nThe TE Statistics Report has been successfully generated.");
		System.out.println("Here -> " + finalResult);
	}

	
	public String getResultDescription(String result) {
		
		if (result.equals("-1")) {
			return "CONTINUE";
		} else if (result.equals("0")) {
			return "BEST PRACTICE";
		} else if (result.equals("1")) {
			return "PASS";
		} else if (result.equals("2")) {
			return "NOT TESTED";
		} else if (result.equals("3")) {
			return "SKIPPED";
		} else if (result.equals("4")) {
			return "WARNING";
		} else if (result.equals("5")) {
			return "INHERITED FAILURE";
		} else if (result.equals("6")) {
			return "FAIL";
		} else {
			return result;
		}
		
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}

class TEReportErrorHandler implements ErrorHandler {
	
    public void warning(SAXParseException e) throws SAXException {
    	return;
    }

    public void error(SAXParseException e) throws SAXException {
    	return;
    }

    public void fatalError(SAXParseException e) throws SAXException {
    	return;
    }
}
