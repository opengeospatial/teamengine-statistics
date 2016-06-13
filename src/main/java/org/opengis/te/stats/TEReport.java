package org.opengis.te.stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

public class TEReport {

	String username;
	String session;
	String date;
	String test;
	String result;

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

					if (new File(new File(userDirPath, rootDirs[i]),sessionList[j]).isDirectory() && new File(new File(new File(userDirPath, rootDirs[i]), sessionList[j]), "session.xml").exists()) {

						try {
							File logFile = new File(new File(new File(userDirPath, rootDirs[i]), sessionList[j]),"log.xml");
							File sessionFile = new File(new File(new File(userDirPath, rootDirs[i]), sessionList[j]),"session.xml");

							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							DocumentBuilder docBuilder = dbf.newDocumentBuilder();
							Document doc = docBuilder.parse(sessionFile);

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
							System.out.println("Error: Unable to parse xml >>");

							System.out.println("   Public ID: " + pe.getPublicId());
							System.out.println("   System ID: " + pe.getSystemId());
							System.out.println("   Line number: " + pe.getLineNumber());
							System.out.println("   Column number: " + pe.getColumnNumber());
							System.out.println("   Message: " + pe.getMessage());
							System.exit(1);
						} catch (NullPointerException npe) {
							System.out.println("Error:Mandatory values are Null >> " + npe.getMessage());
							System.exit(1);
						} catch (Exception e) {
							System.out.println("Execption occured: "+ e.toString());
							System.exit(1);
						}
					}
				}
			}

		}
	}

	
	public void getFinalResult(File logFile){

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
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
			
			setResult("Error:" + pe.getMessage());

		} catch (FileNotFoundException fnfe) {
			
			setResult("Error: The log file not exist.");

		} catch (NullPointerException npe) {

			setResult("Error:" + npe.getLocalizedMessage());

		} catch (Exception e) {

			setResult("Error: " + e.getMessage());

		}


	}
	
	
	public void reportWritter(File reportFileName){

		BufferedWriter outputFile=null;
		//	    Write the result into file;
		try{
			FileWriter resultsWritter=new FileWriter(reportFileName, true);
			outputFile = new BufferedWriter(resultsWritter);
			// "userName | session | date | testName | overallResult"
			String result = this.getUsername() + "," + this.getSession() + "," + this.getDate() + "," + this.getTest() + "," + getResultDescription(this.getResult());
			outputFile.newLine();
			outputFile.write(result);

			outputFile.close();
		}catch(IOException io){
			System.out.println("Exception while writting file.");
			io.printStackTrace();
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
		te.processResult(userDirPath, finalResult);

		System.out.println("The TE Statistics Report has been successfully generated.");
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
