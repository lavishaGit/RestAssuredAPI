package JobsAPI;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

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

public class JobsPost {
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

	public JobsPost() {
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

	@Test(enabled = true, priority = 1, dataProvider = "ReadDP")
	public void createNewJob(String JobId, String JobTitle, String JobCompanyName, String JobLocation, String JobType,
			String JobPostedtime, String JobDescription) throws IOException {

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

		requestSpec.header("Content-Type", "application/json").queryParam("Job Id", JobId)
				.queryParam("Job Title", JobTitle).queryParam("Job Company Name", JobCompanyName)
				.queryParam("Job Location", JobLocation).queryParam("Job Type", JobType)
				.queryParam("Job Description", JobDescription).queryParam("Job Posted time", JobPostedtime);
		requestSpec.body(reqParams.toJSONString());
		Response response = requestSpec.post(path);
		response.prettyPrint();
		String jsonString = response.asString();

		// JsonPath jpath = response.jsonPath();
		// jobId = jpath.get("JobId");

		// JSONObject inputJsonObject = new JSONObject(jsonString);
		// System.out.println("JobId from JSON Response" + inputJsonObject.get(JobId));

		int actualStatus = response.getStatusCode();

		if (actualStatus == 200) {
			Assert.assertEquals(actualStatus, 200);
			String responseBody = response.getBody().asString();
			Assert.assertEquals(responseBody.contains(JobId), true);
			assertThat("Json Schema", responseBody.replaceAll("NaN", "null"),matchesJsonSchemaInClasspath("JobsSchema.json"));

		} else {
			String responseBody = response.getBody().asString();
			//Assert.fail(response.getBody().jsonPath().getString("message"));
			System.out.println(response.getBody().jsonPath().getString("message"));
		}
		
		Assert.assertEquals(actualStatus, 200);
	}

	@DataProvider(name = "ReadDP")
	String[][] getData(Method m) throws IOException {
		String path = System.getProperty("user.dir") + "/src/test/resources/excel/JobsTestData.xlsx";
		String sheetName = m.getName();
		int rownum = excelUtil.getRowCount(path, sheetName);
		int colcount = excelUtil.getCellCount(path, sheetName, 1);
		String empData[][] = new String[rownum][colcount];
		for (int i = 1; i <= rownum; i++) {
			for (int j = 0; j < colcount; j++) {
				empData[i - 1][j] = excelUtil.getCellData(path, sheetName, i, j);

			}
		}
		return (empData);
	}

}
