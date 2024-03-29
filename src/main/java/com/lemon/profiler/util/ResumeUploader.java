package com.lemon.profiler.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

import com.lemon.profiler.constants.ProfilerConstants;
import com.lemon.profiler.service.AuthenticationService;
import com.lemon.profiler.service.PropertyReaderService;
import com.lemon.profiler.service.impl.AuthenticationServiceImpl;
import com.lemon.profiler.service.impl.PropertyReaderServiceImpl;
import com.opensymphony.xwork2.ActionContext;

public class ResumeUploader {
	private static final Logger log = Logger.getLogger(ResumeUploader.class);
	Tika tika = new Tika();
	public static void main(String args[]) throws Exception{
		File folder = new File("C:\\Noushad\\Personal\\Profiler\\Resumes\\Sampl3\\");
		File jSonfolder = new File("C:\\Noushad\\Personal\\Profiler\\Resumes\\json\\");
		File[] listOfFiles = folder.listFiles();		
		int oz =1;
		//trigger those many threads!
		File jsonFile;
		ResumeUploader ru = new ResumeUploader();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				log.info("Processing file : " + file.getName() + " " + oz + " th file out of " + listOfFiles.length);
				log.info("Normal file? "+jSonfolder+"\\"+file.getName()+".json");
				jsonFile = new File(jSonfolder+"\\"+file.getName()+".json");
				if(jsonFile.isFile()) System.out.println("Processed JSON file found for "+jsonFile.getName());
				ru.tryParsing(jsonFile);
				//ru.uploadFiles(file, jsonFile);
//				 BufferedReader br = new BufferedReader(new FileReader(jsonFile));
//				 String line = null;
//				 while ((line = br.readLine()) != null) {
//				   System.out.println(line);
//				 }
			}
			oz++;
		}

	}
	
	
	//Fields to Read
	//Name
	//Nationality
	//Education -------> Done
	//Experience
	//Email -----------> Done
	//DOB
	//Title -----------> Done
	public void tryParsing(File jsonFile){
		JSONParser parser = new JSONParser();
		  try {
			  System.out.println("===========================================================");
			  
	            Object obj = parser.parse(new FileReader(jsonFile));
	 
	            JSONObject jsonObject = (JSONObject) obj;
	            String cleaned = Jsoup.parse(jsonObject.toString()).text();
	            jsonObject = (JSONObject) parser.parse(cleaned);
//	            String basics = (String) jsonObject.get("basics");
	            Object subParser ;
	            JSONArray jsonArr;
	            Set<String> keys = jsonObject.keySet();
	            JSONObject basOb, skillOb, eduOb;
	            for (String key : keys) {
	                System.out.println("Working on >> "+key);
	                if(key.equals("basics")){
	                //	System.out.println(jsonObject.get("name"));
	                	subParser = parser.parse(jsonObject.get("basics").toString());
	                	basOb = (JSONObject) subParser;
	                	//Title
	                	System.out.println("Title :: " +basOb.get("title"));
	                	
	                	//Email returns as an array.. pick one!
	                	//System.out.println("Email ::"+basOb.get("email"));
	                	subParser = parser.parse(basOb.get("email").toString());
	                	jsonArr = (JSONArray) subParser;
	                	for(int i=0;i<jsonArr.size(); i++){
	                		System.out.println("Email :: "+jsonArr.get(i).toString());
	                	}
	                }
	                if(key.equals("skills")){
//	                	subParser = parser.parse(jsonObject.get("skills").toString());
//	                	jsonArr = (JSONArray) subParser;
//	                	//Title
//	                	for (int i = 0;  i < jsonArr.size();  i++)
//	                    {
//	                		subParser = parser.parse(jsonArr.get(i).toString());
//	                		skillOb = (JSONObject) subParser;
//	                		System.out.println("Expertise :: " +skillOb.get("Expertise"));
//	                    }
                	  JSONArray msg = (JSONArray) jsonObject.get("skills");
                      Iterator<String> iterator = msg.iterator();
                      while (iterator.hasNext()) {
                          System.out.println(iterator.next());
                      }
	                	
	                }
	                if(key.equals("education_and_training")){
	                	subParser = parser.parse(jsonObject.get("education_and_training").toString());
	                	jsonArr = (JSONArray) subParser;
	                	//Title 
	                	for (int i = 0;  i < jsonArr.size();  i++)
	                    {
	                		subParser = parser.parse(jsonArr.get(i).toString());
	                		eduOb = (JSONObject) subParser;
	                		System.out.println("Education :: " +eduOb.get("Education"));
	                    }
	                }
	            }
 
	          
//	            
//	            String name = (String) jsonObject.get("name");
//	            String email = (String) jsonObject.get("email"); 
//	            
//	            
//	           // String we = (String) jsonObject.get("work_experience"); 
//	            JSONArray we = (JSONArray) jsonObject.get("Company List");
//	            String education = (String) jsonObject.get("education"); 
//	 
//	            System.out.println("Name: " + name);
//	            System.out.println("Email: " + email);
//	            System.out.println("\nCompany List:");
//	            Iterator<String> iterator = we.iterator();
//	            while (iterator.hasNext()) {
//	                System.out.println(iterator.next());
//	            }
//	            System.out.println("education: " + education);
	            System.out.println("===========================================================");
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    
	}
	
	public void uploadFiles(File file, File processed){
		//Get the ticket
		//Establish Connection
		//Upload File
		//Close the connection
		AuthenticationService authService = new AuthenticationServiceImpl().getInstance();
		PropertyReaderService propS = PropertyReaderServiceImpl.getInstance();
		String strURL = propS.getKeyValue("contentServerURL")
				+ propS.getKeyValue("applicationServiceURL")+"uploadUnProcessedProfile";
		String ticket = "";
		PostMethod post = new PostMethod(strURL);
		String uploadedPath = "";
		ticket = authService.readTicket(ProfilerConstants.USERTYPE_USER);
		String cType = "";
		String organization = ActionContext.getContext().getSession().get(ProfilerConstants.PROPERTY_USER_ORGANIZATION).toString();
		if (ticket != null && !ticket.isEmpty()) {			
			post.addParameter("alf_ticket", ticket);
			try {
//				 MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
//				 MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
//				 MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.OpendesktopMimeDetector");
			       // File f = new File ("c:/temp/mime/test.doc");
			        //Collection<?> mimeTypes = MimeUtil.getMimeTypes(file);
			        //  output : application/msword
			        //if(mimeTypes.size()>0) cType = mimeTypes.iterator().next().toString();
				
			        cType = new ResumeUploader().metadataFinder(file);
			        if(cType.isEmpty() || cType=="") {
			        	if(file.getName().endsWith("doc"))
			        		cType="application/msword";
			        	if( file.getName().endsWith("docx"))
			        		cType="application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			        	if( file.getName().endsWith("ppt"))
			        		cType="application/vnd.ms-powerpoint";
			        	if(file.getName().endsWith("pptx"))
			        		cType="application/vnd.openxmlformats-officedocument.presentationml.presentation";
			        }
			        System.out.println("the mimetype:"+cType);
			        File pdffile = null;
			        try {
			        	String filePath = processed.getAbsolutePath().replace(".json", "");
			        	filePath = filePath.replace("\\", "/");;
			        	System.out.println("PDF File "+filePath);			        	
			             pdffile = new File(filePath);				             
			        } catch (Exception ex) {
			        	ex.printStackTrace();
			        }
			        
				Part[] parts = { new StringPart("fileName", file.getName()), 
						new FilePart("file", file),
						new FilePart("pdffile", pdffile),
					//	new StringPart("pdfmimetype", "application/pdf"),
						new FilePart("jsoncontent", processed),						
						new StringPart("mimetype",cType),
						new StringPart("id", randomId()),
						//TODO: Update the organization hardcoding to read from session
						new StringPart("organizationid", organization),
						new StringPart("jsonfileid",file.getName()+".json")						
				};
				post.setRequestEntity(new MultipartRequestEntity(parts, post
						.getParams()));
				InputStream in = null;
				HttpClient client = new HttpClient();
				Credentials loginCreds = new UsernamePasswordCredentials(
						"admin", "admin");
				client.getState().setCredentials(AuthScope.ANY, loginCreds);
				post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
						new DefaultHttpMethodRetryHandler(3, false));
				int statusCode = client.executeMethod(post);
				System.out.println(statusCode);
				if (statusCode != -1) {
					in = post.getResponseBodyAsStream();

					DocumentBuilderFactory factory2 = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder builder2 = factory2.newDocumentBuilder();
					Document doc3 = builder2.parse(in);
					TransformerFactory factory7 = TransformerFactory
							.newInstance();
					Transformer xform = factory7.newTransformer();
					xform.transform(new DOMSource(doc3), new StreamResult(
							System.out));
					uploadedPath = doc3.getElementsByTagName("id").item(0).getTextContent();
				}

			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}
	public String randomId(){
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		
		return sb.toString();
	}
	
	public String metadataFinder(File f) {
//		FileInputStream is = null;
		try {
	//	ContentHandler contenthandler = new BodyContentHandler();
//	      Metadata metadata = new Metadata();
//	      metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName());
//	      Parser parser = new AutoDetectParser();
//	      // OOXMLParser parser = new OOXMLParser();
//	      is = new FileInputStream(f);
//	      ParseContext context=new ParseContext();	      
//	      parser.parse(is, contenthandler, metadata, context);
//	      System.out.println("Returning the metadata content type:"+metadata.CONTENT_TYPE);
	 //     return metadata.get(metadata.CONTENT_TYPE);
	      
	      System.out.println("Detected Mimetype :"+tika.detect(f));
	      tika.detect(f);
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		return "";
	}
}

class UploadService implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}