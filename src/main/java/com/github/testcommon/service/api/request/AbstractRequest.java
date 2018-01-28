package com.github.testcommon.service.api.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractRequest {

	public String scheme = "";

	private HttpRequestBase request;
	private String path = "";
	private String host = "";
	private List<NameValuePair> parameters;
	private StringEntity StringParams;
	private String token = "";

	public AbstractRequest(String scheme, String host, String path, String token)
	{
		this.scheme = scheme;
		this.host = host;
		this.path = path;
		this.token = token;
		this.parameters = new ArrayList<NameValuePair>();
	}

	public abstract void initRequest();

	public AbstractRequest buildPostRequest() {
		HttpPost request = null;
		try {
			URIBuilder builder = new URIBuilder();
			builder.setScheme(scheme).setHost(host).setPath(path);

			URI uri = builder.build();

			// Create a method instance.
			request = new HttpPost(uri);
			request.addHeader("Authorization", token);
			request.addHeader("Content-Type", "application/json");

			request.setEntity(StringParams);

			this.setRequest(request);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return this;
	}

	public AbstractRequest buildGetRequest() {
		HttpGet request = null;
		try {
			URIBuilder builder = new URIBuilder();
			builder.setScheme(scheme).setHost(host).setPath(path);
			URI uri = builder.build();

			// Create a method instance.
			request = new HttpGet(uri);
			request.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			request.addHeader("Content-Type", "application/json");

			this.setRequest(request);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return this;
	}
	
	private String sendRequest()
	{
		StringBuffer responseString = new StringBuffer();

		if(this.request == null)
		{
			throw new RuntimeException("Please build the request before sending out!");
		}

		System.out.println("Sending Request: " + request.getURI().toString());
		if(this.getParameters().size() > 0)
		{
			System.out.println("Request Parameters:");
			System.out.println("---------------------------------------");
			StringBuilder strBuilder = new StringBuilder();
			this.getParameters().forEach(
					a -> 
					strBuilder.append(a.getName() + ":" + a.getValue()).append("<br>\n")
					);
			System.out.println("Request Parameters:<br>\n" + strBuilder.toString());
			System.out.println("---------------------------------------");
		}
		else
		{
			System.out.println("Request Parameters: N/A");
		}

		try {
			// Create an instance of HttpClient.
			HttpClient httpClient = HttpClientBuilder.create().build();
			// Execute the method.
			HttpResponse httpResponse = httpClient.execute(this.request);

			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				System.out.println("Method failed: " + httpResponse.getStatusLine());
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				responseString.append(line);
			}

			System.out.println("Response: " + responseString.toString());
		}
		catch (IOException e) {
			System.out.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally {
			// Release the connection.
			request.releaseConnection();
		}

		return responseString.toString();
	}

	public <T> T sendRequest(Class<T> responseType)
	{
		String responseString = this.sendRequest();

		Gson gson = new GsonBuilder().create();
		T response = gson.fromJson(responseString, responseType);
		
		return response;
	}

	public HttpRequestBase getRequest() {
		return request;
	}

	public void setRequest(HttpRequestBase request) {
		this.request = request;
	}

	public void setPath(String path)
	{
		this.path = path;
	}
	
	public String getPath()
	{
		return this.path;
	}

	public List<NameValuePair> getParameters() {
		return parameters;
	}

	public void setParameters(List<NameValuePair> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(String key, String value)
	{
		this.addParameter(new BasicNameValuePair(key, value));
	}

	public void addParameter(NameValuePair parameter)
	{
		boolean isExisted = false;
		int index = 0;
		for(; index < this.getParameters().size(); index++)
		{
			NameValuePair param = this.getParameters().get(index);
			if(param.getName().equals(parameter.getName()))
			{
				parameter = new BasicNameValuePair(param.getName(), parameter.getValue());
				isExisted = true;
				break;
			}
		}

		if(isExisted)
		{
			this.getParameters().set(index, parameter);
		}
		else
		{
			this.parameters.add(parameter);
		}
	}

	public void setRequestBody(String requestBody)
	{
		try 
		{
			this.StringParams = new StringEntity(requestBody);
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
}
