package JobsAPI;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
//import org.json.simple.JSONObject;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Utilities.excelUtil;
import Utilities.propertyReader;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class JobsPostParam {
	static int jobId;
	static String flag;
	String jobComp;
	String path;
	String sheetName;
	String sheetPost;
	String sheetPut;
	String sheetDelete;
	excelUtil excelUtil;
	Properties properties;

	public JobsPostParam() {
		propertyReader propReader = new propertyReader();
		properties = propReader.loadProperties();
	}
	
	@BeforeClass
	public void setUp() {
		sheetPost = properties.getProperty("sheetPost");
		sheetPut = properties.getProperty("sheetPut");
		sheetDelete = properties.getProperty("sheetDelete");
		excelUtil = new excelUtil();
				
	}

	@Test(enabled = true, priority = 1, dataProvider="ReadDP")
	public void createNewJob(String JobId, String JobTitle, String JobCompanyName, String JobLocation, String JobType, 
		String JobPostedtime, String JobDescription ) throws IOException {
	
		RequestSpecification requestSpec = RestAssured.given().auth()
				.basic(properties.getProperty("username"), properties.getProperty("password")).log().all();
		path = properties.getProperty("jobsbase_uri");
	
		HashMap<String, Object> reqParams = new HashMap<>();
		reqParams.put("JobId", JobId);
		reqParams.put("JobTitle", JobTitle);
		reqParams.put("JobCompanyName", JobCompanyName);
		reqParams.put("JobLocation", JobLocation);
		reqParams.put("JobType", JobType);
		reqParams.put("JobPostedtime", JobPostedtime);
		reqParams.put("JobDescription", JobDescription);
		

		//System.out.println("excel program name : " + pname);
		requestSpec.header("Content-Type", "application/json")
		.queryParam("Job Id" , JobId)
		.queryParam("Job Title" , JobTitle)
		.queryParam("Job Company Name" , JobCompanyName)
		.queryParam("Job Location" , JobLocation)
		.queryParam("Job Type" , JobType)
		.queryParam("Job Description" , JobDescription)
		.queryParam("Job Posted time" , JobPostedtime); 
		//requestSpec.body(reqParams.toString());
		requestSpec.body(reqParams);
		Response response = requestSpec.post(path);
		response.prettyPrint();
		String jsonString = response.asString();
		
	//	JsonPath jpath = response.jsonPath();
		//jobId = jpath.get("JobId");
		
		//System.out.println("JsonString od response " + jsonString);
		JSONObject inputJsonObject = new JSONObject(jsonString);
		//getKey(inputJsonObject, "data");
		assertTrue(inputJsonObject.equals(JobId));
		assertTrue(inputJsonObject.equals(JobTitle));
		assertTrue(inputJsonObject.equals("Diana"));
		
		//inputJsonObject.equals(JobId);
		//inputJsonObject.equals(JobTitle);
		//inputJsonObject.equals("Diana");
		
		//mamata
		//JsonPath inputJsonObject = new JsonPath(jsonString.replaceAll("NaN", null));
		//Map<String, String> JobIDMap = inputJsonObject.get("data.'Job Id'");
		//System.out.println("JobId from JSON Response" + JobIDMap);
		
		/*
		try {
			int actualStatus = response.getStatusCode();
			Assert.assertEquals(actualStatus, 200);
			String responseBody = response.getBody().asString();
			Assert.assertEquals(responseBody.contains(JobId),true);
			assertThat("Json Schema" , responseBody, matchesJsonSchemaInClasspath("JobsSchema.json"));
		} catch (Exception e) {
			
			String responseBody = response.getBody().asString();
			//System.out.println(response.getBody().jsonPath().getString("message"));
			String responseString =  response.asString();
			//System.out.println(response.jsonPath().get("message"));
		}
		*/
	}
	
	public static void getKey(JSONObject json, String key) {
	 boolean exist	= json.has(key);
	 Iterator<?> keys;
	 String nextKeys;
	 
	 if(!exist) {
		 keys = json.keys();
		 while(keys.hasNext()) {
			 nextKeys = (String) keys.next();
			 try {
				 if(json.get(nextKeys) instanceof JSONObject) {
					 
					 if(exist == false) {
						 //recursive method
						 getKey(json.getJSONObject(nextKeys),key);
					 }
					 
				 }else if (json.get(nextKeys) instanceof JSONArray) {
					JSONArray jsonarray = json.getJSONArray(nextKeys);
					for(int i=0; i < jsonarray.length(); i++) {
					String jsonarrayString	=  jsonarray.get(i).toString();
					JSONObject innerJSON = new JSONObject(jsonarrayString);
					
					if(exist == false) {
						getKey(innerJSON, key);
					}
					}
				}{
				
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		 }
	 }else {
		
	}
	 parseObj( json,key);
		 
	}
	
	public static void parseObj(JSONObject json, String key) {
		System.out.println(json.has(key));
		System.out.println(json.get(key));
	}
	
	@DataProvider(name="ReadDP")
	String [][] getData(Method m) throws IOException{
		String path= System.getProperty("user.dir")+ "/src/test/resources/excel/JobsTestData.xlsx";
		String sheetName = m.getName();
		int rownum = excelUtil.getRowCount(path, sheetName);
		int colcount =  excelUtil.getCellCount(path, sheetName, 1);
		String empData[][] = new String[rownum][colcount];
		for(int i=1; i<=rownum; i++) {
			for(int j=0; j<colcount; j++) {
				empData[i - 1][j] = excelUtil.getCellData(path, sheetName, i, j);
				
			}
		}
		return(empData);
	}
	
}




