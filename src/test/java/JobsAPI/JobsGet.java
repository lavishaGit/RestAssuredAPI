package JobsAPI;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hamcrest.core.Is;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

import Utilities.excelUtil;
import Utilities.propertyReader;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class JobsGet {

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

	public JobsGet() {
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


	@Test(enabled = true, priority = 0)
	public void getAllDetails() {

		RequestSpecification requestSpec = RestAssured.given().auth()
				.basic(properties.getProperty("username"), properties.getProperty("password")).log().all();
		path = properties.getProperty("jobsbase_uri");
		

		Response response = requestSpec.get(path).then().assertThat().statusCode(200).extract().response();
	
		response.prettyPrint();
		//Response response = requestSpec.get(path);
		
		String responseStr = response.getBody().asString();
		assertThat("Json Schema" , responseStr.replaceAll("NaN", "null"), matchesJsonSchemaInClasspath("JobsSchema.json"));
		
		
		//assertThat("Json Schema" , responseStr, matchesJsonSchemaInClasspath("JobsSchema.json"));
		//JSONObject inputJsonObject = new JSONObject(responseStr);
		
		/*Map jsonResponseMap =  requestSpec.get(path).as(Map.class);
		
		Map<String, String> jobIdMap = (Map<String, String>)jsonResponseMap.get("Job Id");
		System.out.println(jobIdMap.get("1"));
		
		*/
		//response.then().assertThat().body("data.'Job Title[1]'", Is.is("SDET"));
		
		//JsonPath jpath = response.jsonPath();
		
		//jobComp = jpath.getString("data.'Job Company Name'");
		//System.out.println("Company from Body : " + jobComp);
		
		//List<String> allCompNameList = jpath.getList("data.'Job Company Name'");
		//System.out.println("Job Company Name : " + allCompNameList);
		
		//String responseBody = response.asString();
		//Assert.assertEquals(responseBody.contains("Selenium"),true);
		
		//Map<String, Object> JSONresponseMap = requestSpec.get(path).as(new TypeRef<Map<String, Object>>(){});
		//String mapJobID =  (String) JSONresponseMap.get("Job Title");
		//System.out.println(mapJobID);
		//JSONresponseMap.keySet().forEach(k -> System.out.println(k));
		
		
	}
	
	  @Test(enabled = false, priority = 1)
	    public void testJsonSchema() {
		  RestAssured.given().auth()
			.basic(properties.getProperty("username"), properties.getProperty("password"))
	            .get(properties.getProperty("jobsbase_uri"))
	            .then()
	            .assertThat()
	            .body(matchesJsonSchemaInClasspath("JobsSchema.json"));
	 
	    }
}



