package org.opengis.te.stats;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.DateTime;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class StatisticsCreator {
	static String currentDate;
	public static ArrayList<String> updatedList= new ArrayList<String>();
	
	public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException {
	    
	    String userDirectory = args[0];
	    File userDirPath=new File(userDirectory);
	    
	    // get user directory list under "users/" directory, e.g. ogctest, abc, xyz .....
	    String[] rootDirs = userDirPath.list();
	    
	    if(null != rootDirs && rootDirs.length > 0){
		    for(int i=0; i < rootDirs.length; i++){
		    	
		    	// getSession dir list from each user directory "users[i]/ogctest/" e.g. s0001,s0002...
		    	String[] sessionList=new File(userDirPath,rootDirs[i]).list();
		    	
		    	if(null != sessionList && sessionList.length > 0){
		    		
		    		for(int j=0; j < sessionList.length; j++){
		    			
		    			if(new File(new File(userDirPath,rootDirs[i]),sessionList[j]).isDirectory() && new File(new File(new File(userDirPath,rootDirs[i]),sessionList[j]), "session.xml").exists()){
		    				
		    				try{
		    				File sessionFile=new File(new File(new File(userDirPath,rootDirs[i]),sessionList[j]), "session.xml");
		    				
		    				DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		    				DocumentBuilder docBuilder=dbf.newDocumentBuilder();
		    				Document doc=docBuilder.parse(sessionFile);
		    				Element session = (Element) (doc.getElementsByTagName("session").item(0));
		    				
		    				Boolean checkAttr=session.hasAttribute("date");
		    				
		    				if(!checkAttr){
		    					
		    					Path file = sessionFile.toPath();
		    	                BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
		    	                
		    			        java.util.Date dates=new DateTime( attr.creationTime().toString() ).toDate();
		    			        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		    			        String currentdate=dateFormat.format(dates);
		    					session.setAttribute("date", currentdate);
		    					
		    					Transformer transformer = TransformerFactory.newInstance().newTransformer();
		    					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    					transformer.setOutputProperty("omit-xml-declaration", "yes");
		    					
		    					//initialize StreamResult with File object to save to file
		    					StreamResult result = new StreamResult(sessionFile);
		    					DOMSource source = new DOMSource(doc);
		    					transformer.transform(source, result);
		    					
		    					updatedList.add(rootDirs[i] + "/" + sessionList[j]);
		    				}
		    			} catch (SAXParseException pe) {
		    				System.out.println("Error: Unable to parse xml >>");

		                	System.out.println("   Public ID: "+pe.getPublicId());
		                    System.out.println("   System ID: "+pe.getSystemId());
		                    System.out.println("   Line number: "+pe.getLineNumber());
		                    System.out.println("   Column number: "+pe.getColumnNumber());
		                    System.out.println("   Message: "+pe.getMessage());
		                	System.exit(1);
		                	}
		    	            catch (NullPointerException npe) {
		    					System.out.println("Error:Mandatory values are Null >> "+ npe.getMessage());
		    					System.exit(1);
		    				}
		    				catch(Exception e){
			    				System.out.println("Execption occured: " + e.toString());
			    				System.exit(1);
		    				}
		    		}
		    	}
		    }
		 }
		    
		    // Print user session which updated with date attribute
		    System.out.println("Following session are Updated with date attribute:");
		    
		    	int count=1;
			    for(int l=0; l < updatedList.size(); l++){
			    	System.out.println("\t" + count+". " + updatedList.get(l));
			    	count++;
			    }
		}
	}
}
