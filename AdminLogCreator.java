/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
    Arrays.sort(rootDirs);
    for (int i = 0; i < rootDirs.length; i++) {
      String[] dirs = new File(logDir, rootDirs[i]).list();
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
            Path file = sessionFile.toPath();
            BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
            DateTime fileCreationTime = new DateTime(attr.creationTime().toString());
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
        }
      }
    }
  }

  public void processForUsers(String testName, File logDir) throws SAXException, ParserConfigurationException, IOException {
    setTestName(testName);
    String[] rootDirs = logDir.list();
    Arrays.sort(rootDirs);
    for (int i = 0; i < rootDirs.length; i++) {
      innercountLastMonth = 0;
      innercountLast3Month = 0;
      innercountLastYear = 0;
      innercountAllTime = 0;
      String[] dirs = new File(logDir, rootDirs[i]).list();
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
            Path file = sessionFile.toPath();
            BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
            DateTime fileCreationTime = new DateTime(attr.creationTime().toString());
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
    String testName=args[0];
    File userDirectory=new File(args[1]);
    AdminLogCreator adminLogCreator = new AdminLogCreator();
    adminLogCreator.processForExecutions(testName,userDirectory);
    System.out.println("Test Statistics by Executions (Sessions)");
    System.out.println("Test Name: " + adminLogCreator.getTestName());
    System.out.println("Last Month: " + adminLogCreator.getCountLastMonth());
    System.out.println("Last 3 Months: " + adminLogCreator.getCountLast3Month());
    System.out.println("Last Year: " + adminLogCreator.getCountLastYear());
    System.out.println("All Times: " + adminLogCreator.getCountAllTime());
    adminLogCreator = new AdminLogCreator();
    adminLogCreator.processForUsers(testName,userDirectory);
    System.out.println("Test Statistics by Users");
    System.out.println("Test Name: " + adminLogCreator.getTestName());
    System.out.println("Last Month: " + adminLogCreator.getCountLastMonth());
    System.out.println("Last 3 Months: " + adminLogCreator.getCountLast3Month());
    System.out.println("Last Year: " + adminLogCreator.getCountLastYear());
    System.out.println("All Times: " + adminLogCreator.getCountAllTime());
  }
}
