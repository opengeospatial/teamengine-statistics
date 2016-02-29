/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.occamlab.te.teamengine.statistics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
  
  public AdminLogCreator() {
    testName = null;
    countLastMonth = 0;
    countLast3Month = 0;
    countLastYear = 0;
    countAllTime = 0;
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
              File sessionFile = new File(new File(new File(logDir, rootDirs[i]), dirs[j]), "session.xml");
              DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
              dbf.setNamespaceAware(true);
              DocumentBuilder db = dbf.newDocumentBuilder();
              Document doc = db.parse(sessionFile);
              Element session = (Element) (doc.getElementsByTagName("session").item(0));
              if ((session.getAttribute("sourcesId")).contains(testName)) {
            	  String date=null;
            	  if(session.getAttribute("date") !=null && session.getAttribute("date") !=""){
            		  date=session.getAttribute("date");
            	  } 
            	  try {
                Path file = sessionFile.toPath();
                BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                DateTime fileCreationTime = DateTimeFormat.forPattern("yyyy/MM/dd  HH:mm:ss").parseDateTime(date).withZone(DateTimeZone.getDefault());
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
              } catch (Exception e) {
            	  System.out.println(" Invalid date exception occured : ");
					e.printStackTrace();
					System.exit(1);
  				}
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
              File sessionFile = new File(new File(new File(logDir, rootDirs[i]), dirs[j]), "session.xml");
              DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
              dbf.setNamespaceAware(true);
              DocumentBuilder db = dbf.newDocumentBuilder();
              Document doc = db.parse(sessionFile);
              Element session = (Element) (doc.getElementsByTagName("session").item(0));
              if ((session.getAttribute("sourcesId")).contains(testName)) {
            	  String date=null;
            	  if(session.getAttribute("date") !=null && session.getAttribute("date") !=""){
            		  date=session.getAttribute("date");
            	  } 
            	  try {
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
            	  } catch (Exception e) {
    					System.out.println(" Invalid date exception occured : ");
    					e.printStackTrace();
    					System.exit(1);
    				}
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
    String output = new DateTime( DateTimeZone.UTC ).toString(); 
    Date date = new Date();
    
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    String currentDate=dateformat.format(date);
    System.out.println("Current DATE: " + currentDate);
    File configDir=new File(userDirectory.split("users")[0] + "config.xml");
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(configDir);
    doc.getDocumentElement().normalize();
    NodeList nList = doc.getElementsByTagName("standard");
    System.out.println("\tTest Statistics by Executions (Sessions)");
    for (int temp = 0; temp < nList.getLength(); temp++) {
      String testName = "";
      Element nNode = (Element) nList.item(temp);
      NodeList nName = nNode.getElementsByTagName("name");
      for (int nameCount = 0; nameCount < 2; nameCount++) {
        if (!"".equals(testName)) {
          testName = testName + "_";
        }
        testName = testName + nName.item(nameCount).getTextContent();
      }
      AdminLogCreator adminLogCreator = new AdminLogCreator();
    adminLogCreator.processForExecutions(testName, pathUserDirecFile);
    
    System.out.println("\nTest Name: " + adminLogCreator.getTestName());
    System.out.print("Last Month:" + adminLogCreator.getCountLastMonth());
    System.out.print("\t|\tLast 3 Months:" + adminLogCreator.getCountLast3Month());
    System.out.print("\t\t|\tLast Year:" + adminLogCreator.getCountLastYear());
    System.out.println("\t|\tAll Times:" + adminLogCreator.getCountAllTime());
    }
    System.out.println("\n\tTest Statistics by Users");
    for (int temp = 0; temp < nList.getLength(); temp++) {
      String testName = "";
      Element nNode = (Element) nList.item(temp);
      NodeList nName = nNode.getElementsByTagName("name");
      for (int nameCount = 0; nameCount < 2; nameCount++) {
        if (testName != "") {
          testName = testName + "_";
        }
        testName = testName + nName.item(nameCount).getTextContent();
      }
      AdminLogCreator adminLogCreator = new AdminLogCreator();
    adminLogCreator.processForUsers(testName, pathUserDirecFile);
    
    System.out.println("\nTest Name: " + adminLogCreator.getTestName());
    System.out.print("Last Month:" + adminLogCreator.getCountLastMonth());
    System.out.print("\t|\tLast 3 Months:" + adminLogCreator.getCountLast3Month());
    System.out.print("\t\t|\tLast Year:" + adminLogCreator.getCountLastYear());
    System.out.println("\t|\tAll Times:" + adminLogCreator.getCountAllTime());
    }
  }
}

