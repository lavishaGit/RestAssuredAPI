package lmsAPI;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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

public class LMSProgPost {

	static int progId;
	boolean onlineVal;
	String progNameActualString;
	static String flag;
	String path;
	String sheetName;
	String sheetPost;
	String sheetPut;
	String sheetDelete;
	excelUtil excelUtil;
	Properties properties;

	public LMSProgPost() {
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
	public void createNewProgram(String pname, String pdesc, String online) throws IOException {
		flag = "post";
		RestAssured.baseURI = properties.getProperty("lmsbase_uri");
		RequestSpecification requestSpec = RestAssured.given().auth()
				.basic(properties.getProperty("username"), properties.getProperty("password")).log().all();
		path = properties.getProperty("endpoint");

		JSONObject reqParams = new JSONObject();
		reqParams.put("programName", pname);
		reqParams.put("programDescription", pdesc);
		reqParams.put("online", online);
		System.out.println("excel program name : " + pname);
		requestSpec.header("Content-Type", "application/json");
		requestSpec.body(reqParams.toJSONString());
		Response response = requestSpec.post(path);

		int actualStatus = response.getStatusCode();
		response.prettyPrint();
		JsonPath jpath = response.jsonPath();

		if (actualStatus == 200) {
			progId = jpath.get("programId");
			progNameActualString = jpath.get("programName");
			System.out.println("Newly created ProgramId: " + progId);

			setProgramId();
			response.then().body("programName", equalTo(pname))
						   .body("programDescription", equalTo(pdesc))
					       .body("online", equalTo(Boolean.parseBoolean(online.toLowerCase())));
		}else {
			String error = jpath.get("error");
			Integer status = jpath.get("status");
			System.out.println("Status Code: " + status + " Error message: " + error);
		}
		
		Assert.assertEquals(actualStatus, 200);
		String responseBody = response.getBody().asString();

		assertThat("Json Schema", responseBody, matchesJsonSchemaInClasspath("LMSPost.json"));
	}

	public void setProgramId() throws IOException {
		String path = System.getProperty("user.dir") + "/src/test/resources/excel/TestData.xlsx";
		// System.out.println("set the Program id: " + progId);
		excelUtil.WriteData_Excel(path, sheetPut, progId, 1, 0, CellType.NUMERIC);

	}

	@DataProvider(name = "ReadDP")
	String[][] getData(Method m) throws IOException {
		String path = System.getProperty("user.dir") + "/src/test/resources/excel/TestData.xlsx";
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
