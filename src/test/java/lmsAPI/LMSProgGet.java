package lmsAPI;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.util.List;
import java.util.Properties;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Utilities.excelUtil;
import Utilities.propertyReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
public class LMSProgGet {
	
	static int progId;
	static String flag;
	String path;
	String sheetName;
	String sheetPost;
	String sheetPut;
	String sheetDelete;
	excelUtil excelUtil;
	Properties properties;

	public LMSProgGet() {
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
	public void getAllDetails() throws JsonMappingException, JsonProcessingException {

		RestAssured.baseURI = properties.getProperty("lmsbase_uri");
		RequestSpecification requestSpec = RestAssured.given().auth()
				.basic(properties.getProperty("username"), properties.getProperty("password")).log().all();
		path = properties.getProperty("endpoint");
		Response response = requestSpec.get(path).then().assertThat().statusCode(200).extract().response();
		//response.prettyPrint();
		String jsonprog =  response.prettyPrint();
		
		ObjectMapper objectMapper = new ObjectMapper();
			
		//List<Program> programList = objectMapper.readValue(jsonprog, new TypeReference<List<Program>>(){});
		
		//System.out.println("Program List ->" + programList);
		
	     Program[] programArray = 	objectMapper.readValue(jsonprog, Program[].class);
	     
	     System.out.println("Program Array : ");
	     for(Program prog : programArray) {
	    	 System.out.println(prog);
	     }
	     
	     System.out.println("Total Number of Programs: " + programArray.length);
		
	}
	
	  @Test(enabled = false, priority = 1)
	    public void testJsonSchema() {
		  RestAssured.given().auth()
			.basic(properties.getProperty("username"), properties.getProperty("password"))
	            .get(path = properties.getProperty("endpoint"))
	            .then()
	            .assertThat()
	            .body(matchesJsonSchemaInClasspath("LMSSchema.json"));
	 
	    }
	  
	
}
