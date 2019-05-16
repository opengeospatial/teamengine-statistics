/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opengis.te.stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class AdminLogCreator {

  String testName;
  int countLastMonth;
  int countLast3Month;
  int countLastYear;
  int countAllTime;
  int innercountLastMonth;
  int innercountLast3Month;
  int innercountLastYear;
  int innercountAllTime;
  static int countTestLastYear;
  
  static int jan = 0;
  static int feb = 0;
  static int mar = 0;
  static int apr = 0;
  static int may = 0;
  static int jun = 0;
  static int jul = 0;
  static int aug = 0;
  static int sep = 0;
  static int oct = 0;
  static int nov = 0;
  static int dec = 0;

  static Logger logger = Logger.getLogger(AdminLogCreator.class.getName());
 
  public AdminLogCreator() {
    testName = null;
    countLastMonth = 0;
    countLast3Month = 0;
    countLastYear = 0;
    countAllTime = 0;
    countTestLastYear = 0;
   
    
  }

  public void processForExecutions(String testName, File logDir) throws SAXException, ParserConfigurationException, IOException {
    
	  setTestName(testName);
    String[] rootDirs = logDir.list();
    if (null != rootDirs && 0 < rootDirs.length) {
      Arrays.sort(rootDirs);
      for (int i = 0; i < rootDirs.length; i++) {
        String[] dirs = new File(logDir, rootDirs[i]).list();
        if (null != dirs && 0 < dirs.length) {
          Arrays.sort(dirs);
          for (int j = 0; j < dirs.length; j++) {
            if (new File(new File(new File(logDir, rootDirs[i]), dirs[j]), "session.xml").exists()) {
            	
            try {
              File sessionFile = new File(new File(new File(logDir, rootDirs[i]), dirs[j]), "session.xml");
              DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
              dbf.setValidating(false);
              dbf.setNamespaceAware(true);
              DocumentBuilder db = dbf.newDocumentBuilder();
              db.setErrorHandler(new AdminLogErrorHandler());
              Document doc = db.parse(sessionFile);
              Element session = (Element) (doc.getElementsByTagName("session").item(0));
              if ((session.getAttribute("sourcesId")).contains(testName)) {
            	  String date=null;
            	  if(session.getAttribute("date") !=null && session.getAttribute("date") !=""){
            		  date=session.getAttribute("date");
            	  } else {
            		  throw new NullPointerException("Date attribute is null in : '" + sessionFile + "'");
            	  }
            	  
                Path file = sessionFile.toPath();
                BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                DateTime fileCreationTime = DateTimeFormat.forPattern("yyyy/MM/dd  HH:mm:ss").parseDateTime(date);
                DateTime currentTime = DateTime.now();
                int countDay = Days.daysBetween(fileCreationTime, currentTime).getDays();
                if (countDay <= 30) {
                  setCountLastMonth();
                  setCountLast3Month();
                  setCountLastYear();
                  setCountAllTime();
                } else if (countDay > 30 && countDay <= 90) {
                  setCountLast3Month();
                  setCountLastYear();
                  setCountAllTime();
                } else if (countDay > 90 && countDay <= 365) {
                  setCountLastYear();
                  setCountAllTime();
                } else {
                  setCountAllTime();
                }
              }
            } catch (SAXParseException pe) {
            		logger.log(Level.SEVERE, "Error: Unable to parse xml >>" + " Public ID: "+pe.getPublicId() + ", System ID: "+pe.getSystemId() + ", Line number: "+pe.getLineNumber() + ", Column number: "+pe.getColumnNumber() + ", Message: "+pe.getMessage());
            	}
            	catch (NullPointerException npe) {
	            	logger.log(Level.SEVERE, "Error:"+ npe.getMessage());
				}
	            catch (Exception e) {
	            	logger.log(Level.SEVERE, "Error: Mandatory values are not valid: " + "' "+ e.getMessage() + " '");
				}
            }
          }
        }
      }
    }
  }
  
  public void testRunsInLastYear(String testName, File logDir) throws SAXException, ParserConfigurationException, IOException {
  
  this.countLastYear = 0;  
  setTestName(testName);
  String[] rootDirs = logDir.list();
  if (null != rootDirs && 0 < rootDirs.length) {
    Arrays.sort(rootDirs);
    for (int i = 0; i < rootDirs.length; i++) {
      String[] dirs = new File(logDir, rootDirs[i]).list();
      if (null != dirs && 0 < dirs.length) {
        Arrays.sort(dirs);
        for (int j = 0; j < dirs.length; j++) {
          if (new File(new File(new File(logDir, rootDirs[i]), dirs[j]), "session.xml").exists()) {
              
          try {
            File sessionFile = new File(new File(new File(logDir, rootDirs[i]), dirs[j]), "session.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new AdminLogErrorHandler());
            Document doc = db.parse(sessionFile);
            Element session = (Element) (doc.getElementsByTagName("session").item(0));
            if ((session.getAttribute("sourcesId")).contains(testName)) {
                String date=null;
                if(session.getAttribute("date") !=null && session.getAttribute("date") !=""){
                    date=session.getAttribute("date");
                } else {
                    throw new NullPointerException("Date attribute is null in : '" + sessionFile + "'");
                }
                
              DateTime testExecutionTime = DateTimeFormat.forPattern("yyyy/MM/dd  HH:mm:ss").parseDateTime(date);
              DateTime currentTime = DateTime.now();
              int testExecYear = testExecutionTime.getYear();
              int currentYear = currentTime.getYear();
              if (testExecYear == currentYear) {
                  countTestLastYear++;
              }
            }
          } catch (SAXParseException pe) {
                  logger.log(Level.SEVERE, "Error: Unable to parse xml >>" + " Public ID: "+pe.getPublicId() + ", System ID: "+pe.getSystemId() + ", Line number: "+pe.getLineNumber() + ", Column number: "+pe.getColumnNumber() + ", Message: "+pe.getMessage());
              }
              catch (NullPointerException npe) {
                  logger.log(Level.SEVERE, "Error:"+ npe.getMessage());
              }
              catch (Exception e) {
                  logger.log(Level.SEVERE, "Error: Mandatory values are not valid: " + "' "+ e.getMessage() + " '");
              }
          }
        }
      }
    }
  }
}  
  
  public void TestRunsPerMonthInLastYear(String testName, File logDir) throws SAXException, ParserConfigurationException, IOException {
    
  setTestName(testName);
  String[] rootDirs = logDir.list();
  if (null != rootDirs && 0 < rootDirs.length) {
    Arrays.sort(rootDirs);
    for (int i = 0; i < rootDirs.length; i++) {
      String[] dirs = new File(logDir, rootDirs[i]).list();
      if (null != dirs && 0 < dirs.length) {
        Arrays.sort(dirs);
        for (int j = 0; j < dirs.length; j++) {
          if (new File(new File(new File(logDir, rootDirs[i]), dirs[j]), "session.xml").exists()) {
              
          try {
            File sessionFile = new File(new File(new File(logDir, rootDirs[i]), dirs[j]), "session.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new AdminLogErrorHandler());
            Document doc = db.parse(sessionFile);
            Element session = (Element) (doc.getElementsByTagName("session").item(0));
            if ((session.getAttribute("sourcesId")).contains(testName)) {
                String date=null;
                if(session.getAttribute("date") !=null && session.getAttribute("date") !=""){
                    date=session.getAttribute("date");
                } else {
                    throw new NullPointerException("Date attribute is null in : '" + sessionFile + "'");
                }                
              DateTime testExecutionTime = DateTimeFormat.forPattern("yyyy/MM/dd  HH:mm:ss").parseDateTime(date);
              DateTime currentTime = DateTime.now();
              int testExecYear = testExecutionTime.getYear();
              int testExecMonth = testExecutionTime.getMonthOfYear();
              int currentYear = currentTime.getYear();
              
              if(testExecYear == currentYear){
                
                switch(testExecMonth){
                case 1 :
                  jan++;
                  break;
                case 2 :
                  feb++;
                  break;
                case 3 :
                  mar++;
                  break;
                case 4 :
                  apr++;
                  break;
                case 5 :
                  may++;
                  break;
                case 6 :
                  jun++;
                  break;
                case 7 :
                  jul++;
                  break;
                case 8 :
                  aug++;
                  break;  
                case 9 :
                  sep++;
                  break;
                case 10 :
                  oct++;
                  break;
                case 11 :
                  nov++;
                  break;
                case 12 :
                  dec++;
                  break;  
                }
              }
            }
          } catch (SAXParseException pe) {
                  logger.log(Level.SEVERE, "Error: Unable to parse xml >>" + " Public ID: "+pe.getPublicId() + ", System ID: "+pe.getSystemId() + ", Line number: "+pe.getLineNumber() + ", Column number: "+pe.getColumnNumber() + ", Message: "+pe.getMessage());
              }
              catch (NullPointerException npe) {
                  logger.log(Level.SEVERE, "Error:"+ npe.getMessage());
              }
              catch (Exception e) {
                  logger.log(Level.SEVERE, "Error: Mandatory values are not valid: " + "' "+ e.getMessage() + " '");
              }
          }
        }
      }
    }
  }
}

  public void processForUsers(String testName, File logDir) throws SAXException, ParserConfigurationException, IOException {
    setTestName(testName);
    String[] rootDirs = logDir.list();
    if (null != rootDirs && 0 < rootDirs.length) {
      Arrays.sort(rootDirs);
      for (int i = 0; i < rootDirs.length; i++) {
        innercountLastMonth = 0;
        innercountLast3Month = 0;
        innercountLastYear = 0;
        innercountAllTime = 0;
        String[] dirs = new File(logDir, rootDirs[i]).list();
        if (null != dirs && 0 < dirs.length) {
          Arrays.sort(dirs);
          for (int j = 0; j < dirs.length; j++) {
            if (new File(new File(new File(logDir, rootDirs[i]), dirs[j]), "session.xml").exists()) {
            	
            try {
              File sessionFile = new File(new File(new File(logDir, rootDirs[i]), dirs[j]), "session.xml");
              DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
              dbf.setValidating(false);
              dbf.setNamespaceAware(true);
              DocumentBuilder db = dbf.newDocumentBuilder();
              db.setErrorHandler(new AdminLogErrorHandler());
              Document doc = db.parse(sessionFile);
              Element session = (Element) (doc.getElementsByTagName("session").item(0));
              if ((session.getAttribute("sourcesId")).contains(testName)) {
            	  String date=null;
            	  if(session.getAttribute("date") !=null && session.getAttribute("date") !=""){
            		  date=session.getAttribute("date");
            	  } else {
            		  throw new NullPointerException("Date attribute is null in : '" + sessionFile + "'");
            	  }
            	  
                Path file = sessionFile.toPath();
                BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                DateTime fileCreationTime = DateTimeFormat.forPattern("yyyy/MM/dd  HH:mm:ss").parseDateTime(date).withZone(DateTimeZone.getDefault());
                DateTime currentTime = DateTime.now();
                int countDay = Days.daysBetween(fileCreationTime, currentTime).getDays();
                if (countDay <= 30) {
                  innercountLastMonth = 1;
                } else if (countDay > 30 && countDay <= 90) {
                  innercountLast3Month = 1;
                } else if (countDay > 90 && countDay <= 365) {
                  innercountLastYear = 1;
                } else {
                  innercountAllTime = 1;
                }
            	  
              }
            } catch (SAXParseException pe) {
            		logger.log(Level.SEVERE, "Error: Unable to parse xml >>" + " Public ID: "+pe.getPublicId() + ", System ID: "+pe.getSystemId() + ", Line number: "+pe.getLineNumber() + ", Column number: "+pe.getColumnNumber() + ", Message: "+pe.getMessage());
            	}
	            catch (NullPointerException npe) {
	            	logger.log(Level.SEVERE, "Error:"+ npe.getMessage());
				}
	            catch (Exception e) {
	            	logger.log(Level.SEVERE, "Error: Mandatory values are not valid: " + "' "+ e.getMessage() + " '");
				}
            }
          }
        }
        if (innercountLastMonth == 1) {
          setCountLastMonth();
          setCountLast3Month();
          setCountLastYear();
          setCountAllTime();
        } else if (innercountLast3Month == 1) {
          setCountLast3Month();
          setCountLastYear();
          setCountAllTime();
        } else if (innercountLastYear == 1) {
          setCountLastYear();
          setCountAllTime();
        } else if (innercountAllTime == 1) {
          setCountAllTime();
        }
      }
    }
  }

  public String getTestName() {
    return testName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public int getCountLastMonth() {
    return countLastMonth;
  }

  public void setCountLastMonth() {
    this.countLastMonth = this.countLastMonth + 1;
  }

  public int getCountLast3Month() {
    return countLast3Month;
  }

  public void setCountLast3Month() {
    this.countLast3Month = this.countLast3Month + 1;
  }

  public int getCountLastYear() {
    return countLastYear;
  }

  public void setCountLastYear() {
    this.countLastYear = this.countLastYear + 1;
  }

  public int getCountAllTime() {
    return countAllTime;
  }

  public void setCountAllTime() {
    this.countAllTime = this.countAllTime + 1;
  }

  public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException {
    
	String userDirectory = args[0];
    File pathUserDirecFile=new File(userDirectory);
    String pattern = Pattern.quote(File.separator);
    String[] splitPath = userDirectory.split(pattern);
    String splitFrom=splitPath[splitPath.length-1];
    File configDir=new File(userDirectory.split(splitFrom)[0] + "config.xml");
    FileHandler logFile = null;
    BufferedWriter outputFile=null;
//    Write the result into file;
    
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
    	 File finalResult = new File(dir + File.separator + resultFileName);
    	 if(finalResult.exists()){
    		 finalResult.delete();
    	 }
    	 try{
    	 FileWriter resultsWritter=new FileWriter(finalResult, true);
    	 outputFile = new BufferedWriter(resultsWritter);
    }catch(IOException io){
    	System.out.println("Exception while writting file.");
    	io.printStackTrace();
    }
    
    
    try{
    	DateTime logDate = new DateTime();
    	DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    	String loggerDate = formatter.print(logDate);
    	File adminLogDir=new File(System.getProperty("user.dir"),"log");
    	File adminLogFile=new File(System.getProperty("user.dir"),"log"+File.separator+"AdminLog-" + loggerDate +".log");
    	
    	if(!adminLogDir.exists()){
    		adminLogDir.mkdir();
    	} 
    	logFile=new FileHandler(adminLogFile.toString(),true);
    	logger.setUseParentHandlers(false);
    	logFile.setFormatter(new SimpleFormatter());
    	logger.addHandler(logFile);
    	
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(configDir);
    doc.getDocumentElement().normalize();
    ArrayList listOfLastYearMapCount = new ArrayList();
    
    
    NodeList nList = doc.getElementsByTagName("standard");
    String testVersionName="";
    System.out.println("\tTest Statistics by Executions (Sessions)");
    for (int temp = 0; temp < nList.getLength(); temp++) {
      String testName = "";
      Element nNode = (Element) nList.item(temp);
     
      NodeList nName = nNode.getElementsByTagName("name");
      NodeList nVersionList = nNode.getElementsByTagName("version");
      testName =nName.item(0).getTextContent();
      for (int nv = 0; nv < nVersionList.getLength(); nv++) {
    	  Element nVersionNode = (Element) nVersionList.item(nv);
    	  NodeList nVersionName = nVersionNode.getElementsByTagName("name");
      for (int nameCount = 0; nameCount < 1; nameCount++) {
    	 testVersionName="";
        if (!"".equals(testName)) {
          testVersionName = testName + "_";
        }
        testVersionName = testVersionName + nVersionName.item(nameCount).getTextContent();
      }
      AdminLogCreator adminLogCreator = new AdminLogCreator();
    adminLogCreator.processForExecutions(testVersionName, pathUserDirecFile);
    
    System.out.println("\nTest Name: " + adminLogCreator.getTestName());
    System.out.print("Last Month:" + adminLogCreator.getCountLastMonth());
    System.out.print("\t|\tLast 3 Months:" + adminLogCreator.getCountLast3Month());
    System.out.print("\t\t|\tLast Year:" + adminLogCreator.getCountLastYear());
    System.out.println("\t|\tAll Times:" + adminLogCreator.getCountAllTime() + "\n");
    }
    }
    

    System.out.println("\n\n\n\n==========================================================\n\n");
    System.out.println("\nTest runs in last year\n\n");
    for (int temp = 0; temp < nList.getLength(); temp++) {
      String testName = "";
      Map<String, Object> lastYearCount = new HashMap<String, Object>();
      Element nNode = (Element) nList.item(temp);
     
      NodeList nName = nNode.getElementsByTagName("name");
      NodeList nVersionList = nNode.getElementsByTagName("version");
      testName =nName.item(0).getTextContent();
      for (int nv = 0; nv < nVersionList.getLength(); nv++) {
          Element nVersionNode = (Element) nVersionList.item(nv);
          NodeList nVersionName = nVersionNode.getElementsByTagName("name");
      for (int nameCount = 0; nameCount < 1; nameCount++) {
          lastYearCount = new HashMap<String, Object>();
         testVersionName="";
        if (!"".equals(testName)) {
          testVersionName = testName + "_";
        }
        testVersionName = testVersionName + nVersionName.item(nameCount).getTextContent();
      }
      AdminLogCreator adminLogCreator = new AdminLogCreator();
    adminLogCreator.testRunsInLastYear(testVersionName, pathUserDirecFile);    
        
    lastYearCount.put("name", adminLogCreator.getTestName());
    lastYearCount.put("y", countTestLastYear);
    listOfLastYearMapCount.add(lastYearCount);
    }     
    }
    
    JSONObject json = new JSONObject();
    json.put("data", listOfLastYearMapCount);
    System.out.println(json);
    
    System.out.println("\n\n\n\n==========================================================\n\n");
    System.out.println("\nTest runs per month in last year\n\n");
    for (int temp = 0; temp < nList.getLength(); temp++) {
      String testName = "";
      Element nNode = (Element) nList.item(temp);
     
      NodeList nName = nNode.getElementsByTagName("name");
      NodeList nVersionList = nNode.getElementsByTagName("version");
      testName =nName.item(0).getTextContent();
      for (int nv = 0; nv < nVersionList.getLength(); nv++) {
          Element nVersionNode = (Element) nVersionList.item(nv);
          NodeList nVersionName = nVersionNode.getElementsByTagName("name");
      for (int nameCount = 0; nameCount < 1; nameCount++) {
         testVersionName="";
        if (!"".equals(testName)) {
          testVersionName = testName + "_";
        }
        testVersionName = testVersionName + nVersionName.item(nameCount).getTextContent();
      }
      AdminLogCreator adminLogCreator = new AdminLogCreator();
      adminLogCreator.TestRunsPerMonthInLastYear(testVersionName, pathUserDirecFile);
    }
    }
    ArrayList<Integer> testRunsPerMonth = new ArrayList<Integer>(Arrays.asList(jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec));
    
    JSONObject resultPerMonth = new JSONObject();
    resultPerMonth.put("data", testRunsPerMonth);
    System.out.println(resultPerMonth);
    
   System.out.println("\n\n\n\n==========================================================\n\n"); 
   System.out.println("\n\tTest Statistics by Users");
    for (int temp = 0; temp < nList.getLength(); temp++) {
    	String testName = "";
        Element nNode = (Element) nList.item(temp);
       
        NodeList nName = nNode.getElementsByTagName("name");
        NodeList nVersionList = nNode.getElementsByTagName("version");
        testName =nName.item(0).getTextContent();
        for (int nv = 0; nv < nVersionList.getLength(); nv++) {
      	  Element nVersionNode = (Element) nVersionList.item(nv);
      	  NodeList nVersionName = nVersionNode.getElementsByTagName("name");
        for (int nameCount = 0; nameCount < 1; nameCount++) {
      	 testVersionName="";
          if (!"".equals(testName)) {
            testVersionName = testName + "_";
          }
          testVersionName = testVersionName + nVersionName.item(nameCount).getTextContent();
        }
      AdminLogCreator adminLogCreator = new AdminLogCreator();
    adminLogCreator.processForUsers(testVersionName, pathUserDirecFile);
    
    System.out.println("\nTest Name: " + adminLogCreator.getTestName());
    System.out.print("Last Month:" + adminLogCreator.getCountLastMonth());
    System.out.print("\t|\tLast 3 Months:" + adminLogCreator.getCountLast3Month());
    System.out.print("\t\t|\tLast Year:" + adminLogCreator.getCountLastYear());
    System.out.println("\t|\tAll Times:" + adminLogCreator.getCountAllTime() + "\n");
    }
    }

    } catch (SAXParseException pe) {
    	logger.log(Level.SEVERE, "Error: Unable to parse xml >>" + " Public ID: "+pe.getPublicId() + ", System ID: "+pe.getSystemId() + ", Line number: "+pe.getLineNumber() + ", Column number: "+pe.getColumnNumber() + ", Message: "+pe.getMessage());
    	}
        catch (Exception e) {
        	logger.log(Level.SEVERE, "Error: In main method Mandatory values are not valid: " + "' "+ e.getMessage() + " '");
        	e.printStackTrace();
		}
    	finally {
			 if(logFile != null){
				 	logFile.close();
			 }
			 
	  }
  }
}


class AdminLogErrorHandler implements ErrorHandler {

	static	Logger logger=Logger.getLogger(AdminLogCreator.class.getName());
	
    public void warning(SAXParseException e) throws SAXException {
    	logger.log(Level.SEVERE,e.getMessage());
    }

    public void error(SAXParseException e) throws SAXException {
		logger.log(Level.SEVERE,e.getMessage());
    }

    public void fatalError(SAXParseException e) throws SAXException {
		logger.log(Level.SEVERE,e.getMessage());
    }
}

