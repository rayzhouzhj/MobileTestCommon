package com.github.testcommon.service.web.request;

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
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractRequest {

	public String host = "";
	public String scheme = "";

	private boolean hideDetails = true;
	private HttpClient httpClient;
	private HttpRequestBase httpRequest;
	private HttpClientContext httpContext;
	private HttpResponse httpResponse;
	private String path = "";
	private List<NameValuePair> postParameters;
	private List<NameValuePair> uriParameters;

	public AbstractRequest(String scheme, String host, String path)
	{
		this.scheme = scheme;
		this.host = host;
		this.setHttpClient(null);
		this.setHttpContext(null);

		this.setPath(path);
		this.postParameters = new ArrayList<NameValuePair>();
		this.uriParameters = new ArrayList<NameValuePair>();
	}

	public AbstractRequest(String scheme, String host, String path, AbstractRequest lastRequest)
	{
		this.scheme = scheme;
		this.host = host;
		
		this.setPath(path);
		this.postParameters = new ArrayList<NameValuePair>();
		this.uriParameters = new ArrayList<NameValuePair>();

		if(lastRequest == null)
		{
			CookieStore cookieStore = new BasicCookieStore();
			// Create local HTTP context
			httpContext = new HttpClientContext();
			// Bind custom cookie store to the local context
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

			httpClient = HttpClientBuilder.create().build();
		}
		else
		{
			this.setHttpClient(lastRequest.getHttpClient());
			this.setHttpContext(lastRequest.getHttpContext());
		}
	}

	public void hideDetails(boolean flag)
	{
		hideDetails = flag;
	}

	public abstract void initRequest();

	private URI buildURI() throws URISyntaxException
	{
		URIBuilder builder = new URIBuilder();
		builder.setScheme(scheme).setHost(host).setPath(path);

		if(this.getURIParameters().size() > 0)
		{
			builder.addParameters(this.uriParameters);

			if(!this.hideDetails)
			{
				System.out.println("URI Parameters:");
				System.out.println("---------------------------------------");
				System.out.println("Request Parameters:<br>\n" + this.getURIParameters().toString());
				System.out.println("---------------------------------------");
			}
		}

		return builder.build();
	}

	public AbstractRequest buildPostRequest()
	{
		HttpPost request = null;
		try
		{
			URI uri = this.buildURI();

			// Create a method instance.
			request = new HttpPost(uri);
			request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.addHeader("Content-Type", "application/x-www-form-urlencoded");
			request.addHeader("Connection", "keep-alive");
			request.addHeader("Accept-Encoding", "gzip, deflate, br");
			request.addHeader("Accept-Language", "en-US,en;q=0.5");
			request.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:55.0) Gecko/20100101 Firefox/55.0");
			request.setEntity(new UrlEncodedFormEntity(this.getPostParameters()));

			this.setRequest(request);

		} 
		catch (UnsupportedEncodingException | URISyntaxException e) 
		{
			e.printStackTrace();
		}

		return this;
	}

	public AbstractRequest buildGetRequest() 
	{
		HttpGet request = null;
		try 
		{
			URI uri = this.buildURI();

			// Create a method instance.
			request = new HttpGet(uri);
			request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.addHeader("Content-Type", "application/x-www-form-urlencoded");
			request.addHeader("Connection", "keep-alive");
			request.addHeader("Accept-Encoding", "gzip, deflate, br");
			request.addHeader("Accept-Language", "en-US,en;q=0.5");
			request.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:55.0) Gecko/20100101 Firefox/55.0");
			this.setRequest(request);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return this;
	}

	public String sendRequest()
	{
		String responseString = "";

		if(this.httpRequest == null)
		{
			throw new RuntimeException("Please build the request before sending out!");
		}

		System.out.println("Sending Request: " + httpRequest.getURI().toString());


		if(this.getPostParameters().size() > 0)
		{
			StringBuilder strBuilder = new StringBuilder();
			this.getPostParameters().forEach(
					a -> 
					strBuilder.append(a.getName() + ":" + a.getValue()).append("\n")
					);

			if(!this.hideDetails)
			{
				System.out.println("Request Parameters:");
				System.out.println("---------------------------------------");
				System.out.println("Request Parameters:<br>\n" + strBuilder.toString());
				System.out.println("---------------------------------------");
			}
		}
		else
		{
			System.out.println("Request Parameters: N/A");
		}

		try 
		{
			// Execute the method.
			httpResponse = httpClient.execute(this.httpRequest, this.httpContext);
			System.out.println("HTTP Response Code: " + httpResponse.getStatusLine().getStatusCode());
			responseString = getResponse();

			while (httpResponse.getStatusLine().getStatusCode() == 302 || httpResponse.getStatusLine().getStatusCode() == 304)
			{
				String redirectURL = httpResponse.getFirstHeader("Location").getValue();
				System.out.println("Page Redirect To: " + redirectURL);

				// no auto-redirecting at client side, need manual send the request.
				httpRequest = new HttpGet(redirectURL);
				httpResponse = httpClient.execute(this.httpRequest, this.httpContext);
				System.out.println("HTTP Response Code: " + httpResponse.getStatusLine().getStatusCode());

				responseString = getResponse();
			}
			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) 
			{
				System.err.println("Method failed: " + httpResponse.getStatusLine());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("Fatal transport error: " + e.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return responseString.toString();
	}

	private String getResponse() throws UnsupportedOperationException, IOException
	{
		BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

		String line = "";
		StringBuilder responseString = new StringBuilder();
		while ((line = rd.readLine()) != null) 
		{
			responseString.append(line);
		}

		if(!hideDetails)
		{
			System.out.println("Response: " + responseString.toString());
		}

		System.out.flush();

		EntityUtils.consume(httpResponse.getEntity());

		return responseString.toString();
	}

	public <T> T sendRequest(Class<T> responseType)
	{
		String responseString = this.sendRequest();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		T response = gson.fromJson(responseString, responseType);

		return response;
	}

	public HttpRequestBase getRequest() {
		return httpRequest;
	}

	public void setRequest(HttpRequestBase request) {
		this.httpRequest = request;
	}

	public void setPath(String path)
	{
		this.path = path;
	}
	public String getPath()
	{
		return this.path;
	}

	public List<NameValuePair> getURIParameters() {
		return this.uriParameters;
	}

	public void setURIParameters(List<NameValuePair> parameters) {
		this.uriParameters = parameters;
	}

	public List<NameValuePair> getPostParameters() {
		return postParameters;
	}

	public void setPostParameters(List<NameValuePair> parameters) {
		this.postParameters = parameters;
	}

	public void addURIParameter(String key, String value)
	{
		this.uriParameters.add(new BasicNameValuePair(key, value));
	}

	public void addPostParameter(String key, String value)
	{
		this.addPostParameter(new BasicNameValuePair(key, value));
	}

	public void addPostParameter(NameValuePair parameter)
	{
		boolean isExisted = false;
		int index = 0;
		for(; index < this.getPostParameters().size(); index++)
		{
			NameValuePair param = this.getPostParameters().get(index);
			if(param.getName().equals(parameter.getName()))
			{
				parameter = new BasicNameValuePair(param.getName(), parameter.getValue());
				isExisted = true;
				break;
			}
		}

		if(isExisted)
		{
			this.getPostParameters().set(index, parameter);
		}
		else
		{
			postParameters.add(parameter);
		}
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
		if(this.httpClient == null) this.httpClient = HttpClientBuilder.create().build();
	}

	public HttpClient createClient() {
		return HttpClientBuilder.create().build();
	}

	public void setHttpContext(HttpClientContext httpContext) {
		this.httpContext = httpContext;
	}

	public HttpClientContext getHttpContext() {
		return this.httpContext;
	}

	public HttpResponse getLastHttpResponse()
	{
		return this.httpResponse;
	}

	public HttpRequestBase getLastHttpRequest()
	{
		return this.httpRequest;
	}
}
