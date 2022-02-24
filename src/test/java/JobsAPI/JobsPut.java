package JobsAPI;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import org.apache.poi.ss.usermodel.CellType;
import org.hamcrest.core.Is;
import org.json.simple.JSONObject;
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

public class JobsPut {

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

	public JobsPut() {
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
	public void updateJob(String JobId, String JobTitle, String JobCompanyName, String JobLocation, String JobType, 
			String JobPostedtime, String JobDescription ) throws IOException {
		//flag = "put";
		RestAssured.baseURI = properties.getProperty("jobsbase_uri");
		RequestSpecification requestSpec = RestAssured.given().auth()
				.basic(properties.getProperty("username"), properties.getProperty("password")).log().all();

		JSONObject reqParams = new JSONObject();
		reqParams.put("JobId", JobId);
		reqParams.put("JobTitle", JobTitle);
		reqParams.put("JobCompanyName", JobCompanyName);
		/*
		reqParams.put("JobLocation", JobLocation);
		reqParams.put("JobType", JobType);
		reqParams.put("JobPostedtime", JobPostedtime);
		reqParams.put("JobDescription", JobDescription);
		*/
		
		
		//System.out.println("excel program name : " + pname);
		requestSpec.header("Content-Type", "application/json")
		//.queryParams(JobId, reqParams, null)
		//.queryParam(JobId, null)
		.queryParam("Job Id" , JobId)
		.queryParam("Job Title" , JobTitle)
		.queryParam("Job Company Name" , JobCompanyName);
		
		requestSpec.body(reqParams.toJSONString());
		Response response = requestSpec.put();
		String responseBody = response.prettyPrint();
		
		int actualStatus = response.getStatusCode();
		Assert.assertEquals(actualStatus, 200);
		Assert.assertEquals(responseBody.contains(JobTitle),true);
		Assert.assertEquals(responseBody.contains(JobCompanyName),true);
		assertThat("Json Schema", responseBody.replaceAll("NaN", "null"),matchesJsonSchemaInClasspath("JobsSchema.json"));
		
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
