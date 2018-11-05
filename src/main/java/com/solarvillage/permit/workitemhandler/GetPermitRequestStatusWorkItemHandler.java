package com.solarvillage.permit.workitemhandler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solarvillage.permit.model.Permit;

public class GetPermitRequestStatusWorkItemHandler implements WorkItemHandler, Cacheable {

	private static final String INPUT_PARAM_NEW_ORDER = "inPermitRequest";
	private static final String INPUT_PARAM_URL = "inPermitUrl";

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Permit permitRequest = (Permit) workItem.getParameter(INPUT_PARAM_NEW_ORDER);
		String url = (String) workItem.getParameter(INPUT_PARAM_URL);

		Map<String, Object> results = new HashMap<String, Object>();

		Permit permitResponse = permitRequest(permitRequest.getId(), url);
		System.out.println("get permit request " + permitRequest.getType().getString() + " permit request: " + permitResponse);

		results.put(permitRequest.getType().getString(), permitResponse);

		manager.completeWorkItem(workItem.getId(), results);
	}

	private Permit permitRequest(Long id, String url) {

		CloseableHttpResponse response = null;
		CloseableHttpClient client = HttpClients.createDefault();
		
		url += "/" + id;
		System.out.println("get permit request url: " + url);
		
		HttpGet httpGet = new HttpGet(url);
		
		Permit permitRequest = null;

		try {

			httpGet.setHeader("Accept", "application/json");
			httpGet.setHeader("Content-type", "application/json");

			response = client.execute(httpGet);
			System.out.println(response.toString());

			String jsonResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper();
			permitRequest = mapper.readValue(jsonResponse, Permit.class);

			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return permitRequest;
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	@Override
	public void close() {

	}

}
