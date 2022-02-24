package JobsAPI;
import static org.hamcrest.CoreMatchers.is;

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
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;


public class JobsDelete {

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

	public JobsDelete() {
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
	public void deleteJob(String JobId) {
		RestAssured.baseURI = properties.getProperty("jobsbase_uri");
		RequestSpecification requestSpec = RestAssured.given().auth()
				.basic(properties.getProperty("username"), properties.getProperty("password")).log().all();
		JSONObject reqParams = new JSONObject();
		reqParams.put("JobId", JobId);
		//String updatepath = properties.getProperty("jobsbase_uri");
		requestSpec.header("Content-Type", "application/json")
		.queryParam("Job Id", JobId);
		requestSpec.body(reqParams.toJSONString());
		ValidatableResponse response = requestSpec.delete().then().statusCode(200);
		
		
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
