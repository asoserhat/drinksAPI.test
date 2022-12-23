package stepdefinition;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import configreader.FileReaderManager;
import generic.RestAssuredExtension;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.config.ConnectionConfig;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import io.restassured.specification.RequestSpecification;
import utilities.ExcelReaderWriter;

public class CommonSteps {
	public ResponseOptions<Response> response;
	public RequestSpecification req;
	public RestAssuredExtension rse;
	public String token = "";
	Map<String, String> headers = new HashMap<String, String>();
	Map<String, String> params = new HashMap<String, String>();

	ExcelReaderWriter erw = new ExcelReaderWriter();

	Map<Object, Object> newValues = new HashMap<Object, Object>();
	Map<String, Object> newValues1 = new HashMap<String, Object>();
	String JsonString = null, nameOfFile = null, current = null;

	
	//U can hardcode it here 
	String baseURL = FileReaderManager.getInstance().getConfigReader().baseURLPath();
	
	@Given("^I make a GET call to \"([^\"]*)\" to \"([^\"]*)\"$")
	public void I_make_a_get_call_to_EndPoint_with(String api, String endpoint) {

		rse = new RestAssuredExtension(api.concat(endpoint), "GET", null);
		
	
		response = rse.ExecuteWithOutBodyAndParams();

		int code = response.getStatusCode();

		System.out.println("Satus Code for step 1 " + code);

	}
	
	
	@Then("^I verify that status code is equal to (\\d+)$")
	public void verify_that_status_code(int arg1) throws InterruptedException {

		int code = response.getStatusCode();

		System.out.println("Satus Code for step 2 " + code);

		Assert.assertEquals(code, arg1);

		response.getBody().prettyPrint();

	}
	
	@Then("I verify below assets from the jasonpaths: {string} are exist and as required")
	public void i_verify_below_assets_from_the_jasonpaths_are_exist_and_as_required(String str, io.cucumber.datatable.DataTable dt) {
	    // seyda
		Map<String, String> data = dt.asMap(String.class, String.class);
		String responseBody = response.getBody().prettyPrint().toString();
		
		for (String key : data.keySet()) {

			Object valueCheck = JsonPath.read(responseBody, str+key);
			
			if(valueCheck!=null) {
			String value = JsonPath.read(responseBody, str+key).toString();
			String classValue = JsonPath.read(responseBody, str+key).getClass().toString();

			boolean  expected = true;
			boolean  actual = !value.isEmpty();
			Assert.assertEquals(actual, expected);
			
			String  expectedClass = data.get(key);
			boolean  actualClass = classValue.contains(expectedClass);
			Assert.assertEquals(actualClass, expected);
			}
		}
	}
	
	@Then("I verify consistency between assets: {string} and {string}")
	public void i_verify_consistency_between_assets_and(String alcohol, String abv) {
		String responseBody = response.getBody().prettyPrint().toString();
		
		Object expected = null;
		Object actual = null;
		
			String isAlcoholic = JsonPath.read(responseBody, alcohol).toString();
			Object abvValue = JsonPath.read(responseBody, abv);

			if (isAlcoholic.contains("Yes")) {
				expected = true;
				actual = abvValue!=null;
			}else if (isAlcoholic.contains("No")) {
				actual = null;
			}
			
			
			Assert.assertEquals(actual, expected);

	}
	
	
	ArrayList<String> drinksFailures = new ArrayList<String>();
	
	@Then("I verify below schema in {string} from the jasonarray: {string} is exist and as required")
	public void i_verify_below_schema_from_the_jasonarray_are_exist_and_as_required(String endPoint, String arrayPath, io.cucumber.datatable.DataTable dt) throws IOException {
		
		Map<String, String> data = dt.asMap(String.class, String.class);
		String responseBody = response.getBody().prettyPrint().toString();

		Object drinkisExist = JsonPath.read(responseBody, arrayPath);
		if(drinkisExist!=null) {
			
			String jSArrayString = JsonPath.read(responseBody, arrayPath).toString();
			JSONArray jsonArray = new JSONArray(jSArrayString);
			int arraySize = jsonArray.length();
			System.out.println(arraySize + " Drinks Array Size");
			
			for(int i =0; i<arraySize; i++) {
			for (String key : data.keySet()) {
				if(data.get(key).equalsIgnoreCase("notNullable")) {
		
					boolean expectedClass= true;
					boolean actualClass= false;
					
					try {
						String classValue = JsonPath.read(responseBody, arrayPath+"["+i+"]."+key).getClass().toString();
						expectedClass = true;
						actualClass = classValue.contains("String");
						System.out.println("classValue " + classValue);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						
						System.out.println("Value for: "+ arrayPath+"["+i+"]."+key +" is NULL! This is Not Expected");
						System.out.println("Assertion skipped tests are continuing");
						
						drinksFailures.add(arrayPath+"["+i+"]."+key);
						
						actualClass = true;
					}
					
					
					
					Assert.assertEquals(actualClass, expectedClass);
			       
			
				}else if (data.get(key).equalsIgnoreCase("Nullable")) {
					
					System.out.println(arrayPath+"[0]."+key +" bu NULL path   "+data.get(key));
			    		boolean nulableexpected_1 = true;
						boolean nulableActual_1 = JsonPath.read(responseBody, arrayPath+"["+i+"]."+key) ==null;
						
						if(nulableActual_1) {
							Assert.assertEquals(nulableActual_1, nulableexpected_1);
						}else {
							String classValue = JsonPath.read(responseBody, arrayPath+"["+i+"]."+key).getClass().toString();
							boolean  expectedClass = true;
							boolean  actualClass = classValue.contains("String");

							Assert.assertEquals(actualClass, expectedClass);
						}
				}

			}
			}	
		}else {
			System.out.println("No such a drink in the database");
			
			boolean noDrinkexpected = true;
			boolean noDrinkActual = JsonPath.read(responseBody, arrayPath) ==null;
			
			Assert.assertEquals(noDrinkActual, noDrinkexpected);
			
		}
		
		System.out.println("Values for below objects found NULL for endpoint:"+endPoint+" which is not expected!!!");
		for(String obs : drinksFailures) {
			System.out.println(" !!! "+ obs +" !!! ");
		}
		
		
		if(!drinksFailures.isEmpty()) {
	     	String excelPath = System.getProperty("user.dir") + "\\src\\test\\resources\\excels\\" + endPoint.substring(endPoint.indexOf("=")) + ".xlsx";
		    erw.writeExcel(excelPath,"Sheet1",drinksFailures);
		}
	}
	
	
	

	
}