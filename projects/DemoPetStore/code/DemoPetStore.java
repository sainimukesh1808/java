/*
(DemoPetStore) - REST API Automation.
Introduction:
You have to implement the following API automated checks over our DEMO PET STORE: https://petstore.swagger.io/.

	1. Get "available" pets. Assert expected result.
	2. Post a new available pet to the store. Assert new pet added.
	3. Update this pet status to "sold". Assert status updated.
	4. Delete this pet. Assert deletion.
*/
package com.qa.pet;

import org.testng.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
public class DemoPetStore {
	int id;
	String url;
	RequestSpecification httprequest;
	Response response;
	
	public DemoPetStore(String url, int id){
		
		this.id = id;
		this.url = url;	
		RestAssured.baseURI = url;
		httprequest = RestAssured.given();
		
	}
	
	public JSONObject createPetData(String cat_name, String main_name, String photo_url, String status) {
		JSONObject requestParams = new JSONObject();
		requestParams.put("id", id);
	    
	    Map<String, Object> category = new LinkedHashMap<String, Object>(); 
	    category.put("id",id);
	    category.put("name",cat_name);
		requestParams.put("category",category);
		
		requestParams.put("name", main_name);
		
		ArrayList<String> photo_urls = new ArrayList<String>();
		photo_urls.add(photo_url);
		requestParams.put("photoUrls", photo_urls);
		
		ArrayList<JSONObject> tags_array = new ArrayList<JSONObject>();
		JSONObject tags = new JSONObject();
		tags.put("id", id);
		tags.put("name", "string");
		tags_array.add(tags);
		requestParams.put("tags", tags_array);
		
		requestParams.put("status", status);
		return requestParams;
		
	}
	
	public void getAvailablePets(String get_parameter){
		response = httprequest.request(Method.GET,get_parameter);
		String responseBody = response.getBody().asString();
		JSONArray array = new JSONArray(responseBody);
		for(int i=0; i < array.length(); i++) {
	         JSONObject object = array.getJSONObject(i);
	         Assert.assertEquals(object.getString("status"), "available", "Status is not availabe");
	      }
		int status_code = response.getStatusCode();
		if(status_code == 200) {
			System.out.println("Information of pets with status availabe has been fectched successfully with response code: " + status_code);
		}
		
		}
	
	public void postAvailabePet(String post_parameter, String cat_name, String main_name, String photo_url, String old_status) {	
		JSONObject object = createPetData(cat_name, main_name, photo_url, old_status);
		httprequest.header("Content-Type", "application/json");
		httprequest.body(object.toString());
		Response response = httprequest.request(Method.POST, post_parameter);
		int status_code = response.getStatusCode();
		Assert.assertEquals(status_code, 200, "Failed to Post new Pet.");
		System.out.println("New Pet with status as available has been posted successfully with response code is " + status_code);
		}
	
	public void updateStatus(String update_parameter, String cat_name, String main_name, String photo_url, String new_status) {
		response = httprequest.request(Method.GET, update_parameter + "/" + id);
		if(response.getStatusCode() == 200) {
			System.out.println("Pet is avaliable with id: "+ id);
			JSONObject object = createPetData(cat_name, main_name, photo_url, new_status);
			httprequest.header("Content-Type", "application/json");
	 		httprequest.body(object.toString());
	 		Response response = httprequest.request(Method.PUT,update_parameter);
	 		int status_code = response.getStatusCode();
	 		Assert.assertEquals(status_code, 200, "Status is not updated for given id pet." + id);
	 		System.out.println("Status of pet with id " + id + " has been updated as " + new_status + " with response code is " + status_code);
		}else {
			System.out.println("Oh! Pet is not avaliable with id: "+ id);
		}
		
		
				}
	        
	public void deletePet(String delete_parameter) {
		response = httprequest.request(Method.GET,delete_parameter);
		if(response.getStatusCode() == 200) {
			System.out.println("Pet is avaliable with id: "+ id);
			response = httprequest.request(Method.DELETE, delete_parameter);
			int status_code = response.getStatusCode();
			Assert.assertEquals(status_code, 200, "Pet is not deleted with: " + id);
			System.out.println("Pet has been deleted successfully with response code is: " + status_code);
		}else {
			System.out.println("Oh! Pet is not avaliable with id: "+ id);
		}
		
	}
		
	

	public static void main(String[] args) {
		int id = 110011111;
		String base_url = "https://petstore.swagger.io";
		String parameter = "/v2/pet";
		String get_parameter = parameter + "/findByStatus?status=available";
		String post_parameter= parameter;
		String update_parameter = parameter;
		String delete_parameter = parameter + "/" +id;
		String cat_name = "string";
		String main_name = "doggie";
		String photo_url = "string";
		String old_status = "available";
		String new_status = "sold";
		DemoPetStore petObj = new DemoPetStore(base_url, id);
		petObj.getAvailablePets(get_parameter);
		petObj.postAvailabePet(post_parameter, cat_name, main_name, photo_url, old_status);
		petObj.updateStatus(update_parameter, cat_name, main_name, photo_url, new_status);
		petObj.deletePet(delete_parameter);
	}

}
