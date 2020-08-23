package com.adobe.aem.accelerator.program.core.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service=Servlet.class,
		property= {
				Constants.SERVICE_DESCRIPTION + "= Covid 19 Updates",
				"sling.servlet.methods=" + HttpConstants.METHOD_GET,
				"sling.servlet.paths="+ "/bin/covidupdates"
})
public class Covid19Servlet extends SlingAllMethodsServlet {
	
	private final Logger LOG = LoggerFactory.getLogger(Covid19Servlet.class);
	
	protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws IOException {
		
		try {
			URL requestURLObj = new URL("https://api.rootnet.in/covid19-in/stats/latest");
			
			HttpURLConnection connection = (HttpURLConnection) requestURLObj.openConnection();
			
			connection.setRequestMethod("GET");
			
			LOG.error("Covid19Servlet Response Code is "+connection.getResponseCode());
			LOG.error("Covid19Servlet Response Message is "+connection.getResponseMessage());
			
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				List<String> locList = null ;
				try {
					JSONObject obj = new JSONObject(response.toString());
					LOG.error("Covid19Servlet Status "+obj.get("success"));
					LOG.error("Covid19Servlet Total case "+((JSONObject) ((JSONObject) obj.get("data")).get("summary")).get("total"));
					
					JSONObject data = (JSONObject) obj.get("data");
					JSONArray regionalData = (JSONArray) data.get("regional");
					locList = new ArrayList<>();
						for(int i=0; i<regionalData.length(); i++) {
						JSONObject itemObj = regionalData.getJSONObject(i);
						LOG.error("LOCTION Is -----"+ itemObj.get("loc"));
						locList.add( itemObj.get("loc").toString());
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}

				response.append(String.valueOf(locList));
				// print result
				resp.getWriter().write(response.toString());
				LOG.error("Covid19Servlet Response " + response.toString());
			} else {
				LOG.error("Covid19Servlet GET request not worked");
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
