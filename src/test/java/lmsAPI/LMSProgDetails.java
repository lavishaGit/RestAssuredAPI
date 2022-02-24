package lmsAPI;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.poi.ss.usermodel.CellType;
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

public class LMSProgDetails {

	static int progId;
	static String flag;
	String path;
	String sheetName;
	String sheetPost;
	String sheetPut;
	String sheetDelete;
	excelUtil excelUtil;
	Properties properties;

	public LMSProgDetails() {
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

	/*
	public void requestSpec() {
		RestAssured.baseURI = properties.getProperty("base_uri");
		requestSpec = RestAssured.given().auth()
				.basic(properties.getProperty("username"), properties.getProperty("password")).log().all();
	}*/
	

	@Test(enabled = true, priority = 0)
	public void getAllDetails() {

		//requestSpec();
		RestAssured.baseURI = properties.getProperty("lmsbase_uri");
		RequestSpecification requestSpec = RestAssured.given().auth()
				.basic(properties.getProperty("username"), properties.getProperty("password")).log().all();
		path = properties.getProperty("endpoint");
		Response response = requestSpec.get(path).then().assertThat().statusCode(200).extract().response();
		response.prettyPrint();

	}

	@Test(enabled = true, priority = 1, dataProvider="ReadDP")
	public void createNewProgram(String pname, String pdesc, String online) throws IOException {
		flag = "post";
		RestAssured.baseURI = properties.getProperty("lmsbase_uri");
		RequestSpecification requestSpec = RestAssured.given().auth()
				.basic(properties.getProperty("username"), properties.getProperty("password")).log().all();
		path = properties.getProperty("endpoint");
		//requestSpec = RestAssured.given();
		//requestSpec();
		
		JSONObject reqParams = new JSONObject();
		reqParams.put("programName", pname);
		reqParams.put("programDescription", pdesc);
		reqParams.put("online", online);
		System.out.println("excel program name : " + pname);
		requestSpec.header("Content-Type", "application/json");
		requestSpec.body(reqParams.toJSONString());
		Response response = requestSpec.post(path);
		response.prettyPrint();
		JsonPath jpath = response.jsonPath();
		progId = jpath.get("programId");
		System.out.println("Newly created ProgramId: " + progId);
		String responseBody = response.getBody().asString();
	
		int actualStatus = response.getStatusCode();
		Assert.assertEquals(actualStatus, 200);
		if (actualStatus == 200) {
			setProgramId();
		}
		
		//Assert.assertEquals(responseBody.contains("Selenium"),true);
	}
	
	@Test(enabled = true, priority = 2, dataProvider="ReadDP")
	public void updateProgram(String programId, String pname, String pdesc, String online) throws IOException {
		flag = "put";
		RestAssured.baseURI = properties.getProperty("lmsbase_uri");
		RequestSpecification requestSpec = RestAssured.given().auth()
				.basic(properties.getProperty("username"), properties.getProperty("password")).log().all();
		String updatepath = properties.getProperty("endpointPD") + programId;
		//requestSpec = RestAssured.given();
		//requestSpec();
		System.out.println("Path to be printed:" + updatepath);
		
		JSONObject reqParams = new JSONObject();
		reqParams.put("programId", programId);
		reqParams.put("programName", pname);
		reqParams.put("programDescription", pdesc);
		reqParams.put("online", online);
		
		requestSpec.header("Content-Type", "application/json");
		requestSpec.body(reqParams.toJSONString());
		Response response = requestSpec.put(updatepath);
		response.prettyPrint();
		JsonPath jpath = response.jsonPath();
		progId = jpath.get("programId");
		System.out.println("Updated programID: " + progId);
		String responseBody = response.getBody().asString();
	
		int actualStatus = response.getStatusCode();
		Assert.assertEquals(actualStatus, 200);

		if (actualStatus == 200) {
			setUpdateProgramId();
			//excelUtil = new excelUtil();
			//String path= System.getProperty("user.dir")+ "/src/test/resources/excel/TestData.xlsx";
			//System.out.println("Delete excel sheet : " + sheetDelete + " programId : " + progId);
			//excelUtil.WriteData_Excel(path,sheetDelete, progId, 1, 0, CellType.NUMERIC);
		}
		
		Assert.assertEquals(responseBody.contains("Jmeter"),true);
	} 
	
	@Test(enabled = true, priority = 3)
//	public void deleteProgram(String programId, String pname, String pdesc, String online) throws IOException {
	public void deleteProgram() {
		RestAssured.baseURI = properties.getProperty("lmsbase_uri");
		RequestSpecification requestSpec = RestAssured.given().auth()
				.basic(properties.getProperty("username"), properties.getProperty("password")).log().all();
		String updatepath = properties.getProperty("endpointPD") + progId;
		Response response = requestSpec.delete(updatepath);
		response.prettyPrint();
	}

	public void setProgramId() throws IOException {
		String path= System.getProperty("user.dir")+ "/src/test/resources/excel/TestData.xlsx";
		//System.out.println("set the Program id: " + progId);		
		excelUtil.WriteData_Excel(path,sheetPut, progId, 1, 0, CellType.NUMERIC);
		
		//excelUtil.WriteData_Excel(path,sheetDelete, progId, 1, 0, CellType.NUMERIC);
		
	}
	
	public void setUpdateProgramId() throws IOException {
		String path= System.getProperty("user.dir")+ "/src/test/resources/excel/TestData.xlsx";
		//System.out.println("set the Program id: " + progId);		
		excelUtil.WriteData_Excel(path,sheetDelete, progId, 1, 0, CellType.NUMERIC);
		
		//excelUtil.WriteData_Excel(path,sheetDelete, progId, 1, 0, CellType.NUMERIC);
		
	}
	
	@DataProvider(name="ReadDP")
	String [][] getData(Method m) throws IOException{
		String path= System.getProperty("user.dir")+ "/src/test/resources/excel/TestData.xlsx";
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
