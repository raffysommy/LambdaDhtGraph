package it.polito.iot.graph.dht;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import redis.clients.jedis.Jedis;



public class LambdaFunctionHandler implements RequestStreamHandler {
	private Jedis jedis = new Jedis("159.122.181.42",31265);

	/*
	**
    * The main entry point for our application.
    *
    * Read the docs!
    * http://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-create-api-as-simple-proxy-for-lambda.html#api-gateway-proxy-integration-lambda-function-java
    *
    * @param inputStream Input to the AWS Lambda function (forwarded by API Gateway).
    * @param outputStream Output. Should be JSON with a "statusCode" and "body" element. Optionally, include a
    *                    "headers" element as well.
    * @param context The application context.
    * @throws IOException When there's a problem reading/writing the stream.
    */
    @Override
   public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
       JSONObject requestJson = new JSONObject(new JSONTokener(inputStream));
       JSONObject responseJson = new JSONObject();
       JSONObject responseBody = new JSONObject();

       // Headers
       JSONObject headerJson = new JSONObject();
       headerJson.put("Content-Type", "application/json");
       headerJson.put("Access-Control-Allow-Origin", "*");

       try {
           JSONObject params = requestJson.getJSONObject("queryStringParameters");
           Long start=params.getLong("start");
           Long end=params.getLong("end");
           String cache=jedis.get(start.toString()+end.toString());
	 	   if(cache!=null&&!cache.isEmpty()&&!cache.equals("{}")&&!cache.equals("[]")) {
	 			  
	 	   }else {
	           AthenaClient ac=new AthenaClient();           
	           ObjectWriter ow = new ObjectMapper().writer();
	           cache = ow.writeValueAsString(ac.search(start, end));
	           jedis.set(start.toString()+end.toString(), cache);
	           ac.dispose();
	 	   }
	 	   jedis.close();
           responseBody.put("result", cache);
           responseJson.put("statusCode", "200");           
       } catch (JSONException e) {
           responseBody.put("status", "error");
           responseBody.put("message", "You must define start timestamp and end timestamp.");
           responseJson.put("statusCode", "400");
       }

       // Assemble the response.
       responseJson.put("body", responseBody.toString());
       responseJson.put("headers", headerJson);

       OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
       writer.write(responseJson.toString());
       writer.close();
   }
}

