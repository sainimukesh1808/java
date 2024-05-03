import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class RestAssuredPostWithPojo {
  public static void main(String[] a) throws JsonProcessingException {
    RestAssured.baseURI="";

    Map<String,String> headers = new HashMap<>();
    headers.put("","");

    POJOUser body = new POJOUser();
    body.setEmail("test@adobe.com");
    body.setUsername("Mukesh Saini");

    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(body);
    Response response = RestAssured.given().contentType(ContentType.JSON).headers(headers).body(requestBody).post("/");
    System.out.println(response.getStatusCode());
    System.out.println(response.getBody().asString());
  }
}
