package JobsAPI;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpRequest;
import org.json.simple.JSONObject;
//import org.json.JSONObject;
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

public class JobsPostPath {
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

	public JobsPostPath() {
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
		
				
		JSONObject reqParams = new JSONObject();
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
		requestSpec.body(reqParams.toJSONString());
		
		Response response = requestSpec.post(path);
		response.prettyPrint();

		
		if(response.getStatusCode() == 200) {
			Response getresponse = requestSpec.get(path);
			JsonPath path = JsonPath.from(getresponse.getBody().asString().replaceAll("NaN", "null"));
			
			Map<String,String> jobids =	path.get("data.\"Job Id\"");
		
	
		System.out.println("JobIds with JsonPath: " + jobids);

		String jobIdKey = null;
		for(String key: jobids.keySet()) {
			
			//getting key for the actual jobId value
			String ids = jobids.get(key);
			
			if(ids != null) {
				if(jobids.get(key).equals(JobId)) {
					jobIdKey = key;
					break;
				}
			}
			
		} 
		String actualJobTitle = path.get("data.\"Job Title\"."+jobIdKey+"");
		String actualJobLoc = path.get("data.\"Job Location\"."+jobIdKey+"");
		String actualJobComp = path.get("data.\"Job Company Name\"."+jobIdKey+"");
		String actualJobType = path.get("data.\"Job Type\"."+jobIdKey+"");
		String actualJobPostedTime = path.get("data.\"Job Posted time\"."+jobIdKey+"");
		String actualJobDesc = path.get("data.\"Job Description\"."+jobIdKey+"");
		
		Assert.assertEquals(actualJobTitle, JobTitle);
		Assert.assertEquals(actualJobLoc, JobLocation);
		Assert.assertEquals(actualJobComp, JobCompanyName);
		Assert.assertEquals(actualJobType, JobType);
		Assert.assertEquals(actualJobPostedTime, JobPostedtime);
		Assert.assertEquals(actualJobDesc, JobDescription);
		
		}
	}
	
	@DataProvider(name="ReadDP")
	String [][] getData(Method m) throws IOException{
		String path= System.getProperty("user.dir")+ "/src/test/resources/excel/JobsJsonPath.xlsx";
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



