package org.opengis.te.stats;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.management.AttributeNotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class is used to generate the statistics 
 * report and it will provide data which used in our
 * HTML template to create visual report.
 * (e.g. Pie chart, Bar chart and line charts.)
 * 
 * @author Keshav
 *
 */
public class StatisticsReport {

  static Logger logger = Logger.getLogger(StatisticsReport.class.getName());
  
  private static Map<String, Object> runPerTestSuiteInLastYear = new HashMap<String, Object>();
  
  public static void main(String[] args) {
    String usersDir = "";
    String configFilePath = "";
    FileHandler logFile = null;
    
    // Parse arguments from command-line
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.startsWith("-usersDir=")) {
        usersDir = arg.substring(10);
      } else if (arg.startsWith("-configFile=")) {
        configFilePath = arg.substring(12);
      } else {
        System.out.println("[ERROR]: Please provide users directory.");
        return;
      }
    }

    File userDir = new File(usersDir);
    if (!userDir.exists()) {
      System.out.println("[ERROR]: Requested directory done not exists.");
      return;
    }
    File configFile = null;
    if (configFilePath == null || configFilePath == "") {
      System.out.println("[WARNING]: Config file path is not provided. Searching in current directory...");
      String pattern = Pattern.quote(File.separator);
      String[] splitPath = usersDir.split(pattern);
      String splitFrom = splitPath[splitPath.length - 1];
      configFile = new File(usersDir.split(splitFrom)[0] + "config.xml");
      if (!configFile.exists()) {
        System.out.println("[ERROR]: Config file is not provided or does not exist!");
        return;
      }
    } else {
      configFile = new File(configFilePath);
    }
    
    System.out.println("Using the users directory: " + usersDir);
    System.out.println("Using the config file: " + configFile.toString());
    
    try{
      DateTime logDate = new DateTime();
      DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
      int year = logDate.getYear();
      String loggerDate = formatter.print(logDate);
      File statLogDir=new File(System.getProperty("user.dir"),"log");
      File statLogFile=new File(System.getProperty("user.dir"),"log"+File.separator+"StatisticsLog-" + loggerDate +".log");
      File statResultDir=new File(System.getProperty("user.dir"),"result");
      if(!statLogDir.exists()){
        statLogDir.mkdir();
      } 
      logFile=new FileHandler(statLogFile.toString(),true);
      logger.setUseParentHandlers(false);
      logFile.setFormatter(new SimpleFormatter());
      logger.addHandler(logFile);
      
      // Process all the session from all users
      Map<String, List<SessionDetails>> userDetails = processUserDir(userDir);
      
      // Parse config.xml file
      Document doc = parse(configFile);
      
      NodeList nList = doc.getElementsByTagName("standard");
      String testVersionName="";
      
      /***********************************************
       * 
       * Test Runs per test suite in last year
       * 
       **********************************************/
      System.out.println("\nRuns per test suite in last year:\n");
      ArrayList<Map<String, Object>> listOfLastYearMapCount = new ArrayList<Map<String, Object>>();
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
        
        runPerTestSuiteInLastYear(testVersionName, userDetails);    
          
      listOfLastYearMapCount.add(runPerTestSuiteInLastYear);
      }     
      }
      JSONObject json = new JSONObject();
      json.put("data", listOfLastYearMapCount);
      System.out.println("\t" + json);
      
      /***********************************************
       * 
       * Test Runs per month in last year
       * 
       **********************************************/
      System.out.println("\nTest Runs per month in last year:\n");
      ArrayList<Integer> testRunsPerMonth = new ArrayList<Integer>();
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
        testRunsPerMonth = testRunsPerMonthInLastYear(testVersionName, userDetails);              
      }     
      }
      JSONObject resultPerMonth = new JSONObject();
      resultPerMonth.put("data", testRunsPerMonth);
      System.out.println("\t" + resultPerMonth);
      
      /***********************************************
       * 
       *  Total number of users per month in last year
       *  
       **********************************************/
      System.out.println("\nTotal number of users per month in last year:\n");
      ArrayList<Long> usersPerMonth = new ArrayList<Long>();
        usersPerMonth = usersPerMonthInLastYear(userDetails);              
      JSONObject userPerMonthResult = new JSONObject();
      userPerMonthResult.put("data", usersPerMonth);
      System.out.println("\t" + userPerMonthResult);
      
    /***********************************************
     * 
     * Number of users per test suite in last year.
     *
     **********************************************/
      System.out.println("\nNumber of users per test suite in last year:\n");
      Map<String, Object> numberOfUsersPerTestInLastYear = new HashMap<String, Object>();
      ArrayList<Map<String, Object>> listNumberOfUsersPerTestInLastYear = new ArrayList<Map<String, Object>>();
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
        numberOfUsersPerTestInLastYear = numberOfUsersPerTestSuite(testVersionName, userDetails);  
        listNumberOfUsersPerTestInLastYear.add(numberOfUsersPerTestInLastYear);
      }     
      }
      JSONObject numberOfUserPerTestSuite = new JSONObject();
      numberOfUserPerTestSuite.put("data", listNumberOfUsersPerTestInLastYear);
      System.out.println("\t" + numberOfUserPerTestSuite);
      
      /**********************************************
       * 
       * Generate statistics HTML report from the all results.
       * 
       *********************************************/
      
       generateStatisticsHtml(year, statResultDir, getListMapAsString(listOfLastYearMapCount), testRunsPerMonth, getArrayListAsString(usersPerMonth), getListMapAsString(listNumberOfUsersPerTestInLastYear));
      
       
    } catch(Exception e){
      System.out.println("Error: " + e.getMessage());
    }
    
  }

  /**
   * This method is used to process the user data
   * and its session to perform further operation.
   * 
   * @param userDir
   *        The directory which contains the users info.
   * @return Object of Map
   */
  private static Map<String, List<SessionDetails>> processUserDir(File userDir) {

    Map<String, List<SessionDetails>> userDetails= new HashMap<String, List<SessionDetails>>();
    String[] rootDirs = userDir.list();
    if (null != rootDirs && 0 < rootDirs.length) {
      Arrays.sort(rootDirs);
      for (int i = 0; i < rootDirs.length; i++) {
        String[] dirs = new File(userDir, rootDirs[i]).list();
        if (null != dirs && 0 < dirs.length) {
          Arrays.sort(dirs);
          List<SessionDetails> sessions = new ArrayList<SessionDetails>();
          for (int j = 0; j < dirs.length; j++) {
            File sessionFile = new File(new File(new File(userDir, rootDirs[i]), dirs[j]), "session.xml");
            if (sessionFile.exists()) {
                
            try {
              
              DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
              dbf.setValidating(false);
              dbf.setNamespaceAware(true);
              DocumentBuilder db = dbf.newDocumentBuilder();
              db.setErrorHandler(new StatisticsReportErrorHandler());
              Document doc = db.parse(sessionFile);
              Element session = (Element) (doc.getElementsByTagName("session").item(0));
              SessionDetails user = new SessionDetails(); 
              if(session.hasAttribute("id")){
                user.setId(session.getAttribute("id"));
              } else {
                throw new AttributeNotFoundException("'id' attribute not found in : '" + sessionFile + "'");
              }
              if(session.hasAttribute("sourcesId")){
                user.setEtsName(session.getAttribute("sourcesId"));
              } else {
                throw new AttributeNotFoundException("'sourceId' attribute not found in : '" + sessionFile + "'");
              }
              if(session.hasAttribute("date")){
                user.setDate(session.getAttribute("date"));
              } else {
                throw new AttributeNotFoundException("'date' attribute not found in : '" + sessionFile + "'");
              }
              sessions.add(user);
            } catch (SAXParseException pe) {
                    logger.log(Level.SEVERE, "Error: Unable to parse xml >>" + " Public ID: "+pe.getPublicId() + ", System ID: "+pe.getSystemId() + ", Line number: "+pe.getLineNumber() + ", Column number: "+pe.getColumnNumber() + ", Message: "+pe.getMessage());
                }
                catch (NullPointerException npe) {
                    logger.log(Level.SEVERE, "Error:"+ npe.getMessage());
                }
                catch (AttributeNotFoundException anfe) {
                    logger.log(Level.SEVERE, "Error: Attribute not found in session."+ anfe.getMessage());
                }
                catch (Exception e) {
                    logger.log(Level.SEVERE, "Error: Mandatory values are not valid: " + "' "+ e.getMessage() + " '");
                }
            }
          }
          userDetails.put(rootDirs[i], sessions);
        }
      }
    }
    return userDetails;
  }

  /**
   * This method is used to generate result of test
   * runs per month in last year.
   * 
   * @param testVersionName Test suite name with version.
   * @param sessionDetailsList Map of user sessions.
   */
  private static void runPerTestSuiteInLastYear(String testVersionName,
      Map<String, List<SessionDetails>> sessionDetailsList){
    
    runPerTestSuiteInLastYear = new HashMap<String, Object>();
    long count = 0;
    List<SessionDetails> foundSessions = null;
    for(Map.Entry<String, List<SessionDetails>> userSessions : sessionDetailsList.entrySet()){
      List<SessionDetails> sessionList = userSessions.getValue();
      
      foundSessions = sessionList.stream()
          .filter(session -> session.etsName.contains(testVersionName))
          .collect(Collectors.toList());      
      DateTime currentTime = null;
      if(foundSessions != null && !foundSessions.isEmpty()){
        for(SessionDetails session : foundSessions){
          try{
          currentTime = DateTime.now();
          String testExecutionTime = session.getDate();
          //String lastYearDate = getLastYear(formatter.print(currentTime));
          DateTime testExecutionDt = DateTimeFormat.forPattern("yyyy/MM/dd  HH:mm:ss").parseDateTime(testExecutionTime);
          //DateTime lastYearDt = DateTimeFormat.forPattern("yyyy/MM/dd  HH:mm:ss").parseDateTime(lastYearDate);
          if(testExecutionDt.getYear() == currentTime.getYear()){
            count++;
          } 
          } catch(Exception e){
            System.out.println("Error: " + e.getMessage());
          }       
        }
      }
    }      
    runPerTestSuiteInLastYear.put("name", testVersionName);
    runPerTestSuiteInLastYear.put("y", count);
  }
  
  /**
   * This method will create statistics or test runs
   * per month in last year.
   * 
   * @param testVersionName 
   *            The name test suite with version.
   * @param sessionDetailsList 
   *            Map of users sessions.
   * @return ArrayList Of Integer
   *            test count per month.
   */
  private static ArrayList<Integer> testRunsPerMonthInLastYear(String testVersionName,
      Map<String, List<SessionDetails>> sessionDetailsList) {
    
    int jan = 0, feb = 0, mar = 0, apr = 0, may = 0, jun = 0, jul = 0, aug = 0, sep = 0, oct = 0, nov = 0, dec = 0;
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd  HH:mm:ss");
    DateTime currentTime = DateTime.now();
    List<SessionDetails> foundSessions = null;
    for (Map.Entry<String, List<SessionDetails>> userSessions : sessionDetailsList
        .entrySet()) {
      List<SessionDetails> sessionList = userSessions.getValue();
      foundSessions = sessionList
          .stream()
          .filter(
              session -> formatter.parseDateTime(session.getDate()).getYear() == currentTime
                  .getYear()).collect(Collectors.toList());
      if (foundSessions != null && !foundSessions.isEmpty()) {
        for (SessionDetails session : foundSessions) {
          DateTime sessionDt = formatter.parseDateTime(session.getDate());
          int sessionMonth = sessionDt.getMonthOfYear();
          switch (sessionMonth) {
          case 1:
            jan++;
            break;
          case 2:
            feb++;
            break;
          case 3:
            mar++;
            break;
          case 4:
            apr++;
            break;
          case 5:
            may++;
            break;
          case 6:
            jun++;
            break;
          case 7:
            jul++;
            break;
          case 8:
            aug++;
            break;
          case 9:
            sep++;
            break;
          case 10:
            oct++;
            break;
          case 11:
            nov++;
            break;
          case 12:
            dec++;
            break;
          }
        }
      }
    }
    ArrayList<Integer> testRunsPerMonth = new ArrayList<Integer>(Arrays.asList(jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec));
    return testRunsPerMonth;
  }
  
  /**
   * This method is used to get result of users
   * executed test per month in last year.
   * 
   * @param sessionDetailsList 
   * @return ArrayList of user count per month
   */
  private static ArrayList<Long> usersPerMonthInLastYear( Map<String, List<SessionDetails>> sessionDetailsList ) {
    
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd  HH:mm:ss");
    DateTime currentTime = DateTime.now();
    ArrayList<Integer> monthList = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
    ArrayList<Long> userPerMonth = new ArrayList<Long>();
    for (Integer month : monthList) {
      long count = 0;
      long cnt = 0;
      for (Map.Entry<String, List<SessionDetails>> userSessions : sessionDetailsList
          .entrySet()) {
        List<SessionDetails> sessionList = userSessions.getValue();
        count = sessionList
            .stream()
            .filter(
                session -> formatter.parseDateTime(session.getDate()).getYear() == currentTime
                    .getYear()
                    && formatter.parseDateTime(session.getDate())
                        .getMonthOfYear() == month)
            .collect(Collectors.counting());
        if (count > 0) {
          cnt++;
        }
      }
      userPerMonth.add(cnt);
    }
    return userPerMonth;
  }

  /**
   * This method is used to generate statistics
   * of number of users per test suite in last year.
   * 
   * @param testVersionName
   *        Test suite name with version
   * @param sessionDetailsList
   *        List of user object with sessions.
   * @return Map of test suite with user count.
   */
  private static Map<String, Object>  numberOfUsersPerTestSuite(String testVersionName,
      Map<String, List<SessionDetails>> sessionDetailsList){
    
    Map<String, Object> numberOfUsersPerTestSuite = new HashMap<String, Object>();
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd  HH:mm:ss");
    DateTime currentTime = DateTime.now();
    long count = 0;
    long userCount = 0;
    for (Map.Entry<String, List<SessionDetails>> userSessions : sessionDetailsList
        .entrySet()) {
      List<SessionDetails> sessionList = userSessions.getValue();

      count = sessionList
          .stream()
          .filter(
              session -> session.etsName.contains(testVersionName)
                  && formatter.parseDateTime(session.getDate()).getYear() == currentTime
                      .getYear()).collect(Collectors.counting());
      if (count > 0) {
        userCount++;
      }

    }
    numberOfUsersPerTestSuite.put("name", testVersionName);
    numberOfUsersPerTestSuite.put("y", userCount);

    return numberOfUsersPerTestSuite;
  }
  
  /**
   * The method will return the last year
   * date from the provided date.
   * 
   * @param DateTime input
   * @return Last year datetime
   */
  private static String getLastYear(String dt) {

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat dateTimeParser = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
    Date lastYearDate = null;
    try {
      cal.setTime(dateTimeParser.parse(dt));
      cal.add(Calendar.YEAR, -1); // to get previous year add 1
      cal.add(Calendar.DAY_OF_MONTH, -1); // to get previous day add -1
      lastYearDate = cal.getTime();
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse date. " + e.getMessage());
    }
    return dateTimeParser.format(lastYearDate);
  }
  
  /**
   * Retrieve the last month date using this method.
   * 
   * @return DateTime of last month
   */
  private static String getLastThreeMonth(String dt) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat dateTimeParser = new SimpleDateFormat(
        "yyyy/MM/dd  HH:mm:ss");
    Date lastMonthDate = null;
    try {
      cal.setTime(dateTimeParser.parse(dt));
      cal.add(Calendar.MONTH, -3);// to get last three month date add -3
      cal.add(Calendar.DATE, -1);// to get previous day add -1
      lastMonthDate = cal.getTime();
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse date. " + e.getMessage());
    }
    return dateTimeParser.format(lastMonthDate);
  }
  
  /**
   * The method is used to parse given XML file.
   * @param configFile
   * @return Document object
   */
  private static Document parse(File configFile) {
    Document doc = null;
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.parse(configFile);
      doc.getDocumentElement().normalize();
    } catch (SAXParseException pe) {
      logger.log(
          Level.SEVERE,
          "Error: Unable to parse xml >>" + " Public ID: " + pe.getPublicId()
              + ", System ID: " + pe.getSystemId() + ", Line number: "
              + pe.getLineNumber() + ", Column number: " + pe.getColumnNumber()
              + ", Message: " + pe.getMessage());
    } catch (Exception e) {
      logger.log(
          Level.SEVERE,
          "Error: In main method Mandatory values are not valid: " + "' "
              + e.getMessage() + " '");
      e.printStackTrace();
    }
    return doc;
  }

  /**
   * Convert ArrayList of Map to the key-value pair
   * similar to array list into string,
   * which is used in HTML chart to represent the data
   * 
   * @param listOfLastYearMapCount
   * @return String
   */
  private static String getListMapAsString(ArrayList<Map<String, Object>> listOfLastYearMapCount) {
    String resultArrayList = "";
    for (int i = 0; i < listOfLastYearMapCount.size(); i++) {
      Map<String, Object> resultMap = listOfLastYearMapCount.get(i);
      if (i == 0) {
        resultArrayList += "[";
      }
      resultArrayList += "{\"name\" : \"" + resultMap.get("name") + "\",";
      resultArrayList += "\"y\" : " + resultMap.get("y") + "}";
      if (i != listOfLastYearMapCount.size() - 1) {
        resultArrayList += ",";
      }
      if (i == listOfLastYearMapCount.size() - 1) {
        resultArrayList += "]";
      }
    }
    return resultArrayList;
  }

  /**
   * This method is used to convert ArrayList 
   * to string which is used in charts.
   * 
   * @param usersPerMonth
   * @return String
   */
  private static String getArrayListAsString(ArrayList<Long> usersPerMonth) {

    Iterator<Long> it = usersPerMonth.iterator();
    String arrayList = "[";
    while (it.hasNext()) {
      arrayList += it.next();
      if (it.hasNext()) {
        arrayList += ",";
      }
      if (!it.hasNext()) {
        arrayList += "]";
      }
    }
    return arrayList;
  }
  
  /**
   * Generate Statistics HTML report by using
   * XSL file and results.
   * @param year 
   * @param statResultDir
   * @param listMapAsString
   * @param testRunsPerMonth
   * @param usersPerMonthResultList
   * @param listNumberOfUsersPerTestInLastYear
   */
  private static void generateStatisticsHtml(int year, File statResultDir,
      String listOfLastYearMapCountResult, ArrayList<Integer> testRunsPerMonth,
      String usersPerMonthResultList,
      String listNumberOfUsersPerTestInLastYear) {
    
    FileOutputStream fo;
    try{
      String xmlTemplate = StatisticsReport.class.getResource("/template.xml").toString();
      String statXsl = StatisticsReport.class.getResource("/statistics-reporter.xsl").toString();
      Transformer transformer = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null).newTransformer( new StreamSource( statXsl ) );
      transformer.setParameter( "year", year);
      transformer.setParameter( "testRunPertestSuite", listOfLastYearMapCountResult);
      transformer.setParameter( "testRunsPerMonth", testRunsPerMonth);
      transformer.setParameter( "usersPerMonth", usersPerMonthResultList);
      transformer.setParameter( "listNumberOfUsersPerTestInLastYear", listNumberOfUsersPerTestInLastYear);
      if(!statResultDir.exists()){
       statResultDir.mkdir(); 
      }
      File indexHtml = new File( statResultDir, "TE-StatisticsReport.html" );
      indexHtml.createNewFile();
      fo = new FileOutputStream( indexHtml );
      transformer.transform(new StreamSource(xmlTemplate), new StreamResult( fo ) );
      fo.close();
      System.out.println("\nGenerated HTML report here: " + indexHtml);
    } catch(Exception e){
      e.printStackTrace();
    }
    
  }
  
}


class StatisticsReportErrorHandler implements ErrorHandler {

    static  Logger logger=Logger.getLogger(StatisticsReport.class.getName());
    
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
