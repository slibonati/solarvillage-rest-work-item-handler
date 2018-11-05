package com.solarvillage.permit.workitemhandler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solarvillage.permit.model.Permit;


public class SubmitPermitRequestWorkItemHandler implements WorkItemHandler, Cacheable {

	private static final String INPUT_PARAM_NEW_ORDER = "inPermitRequest";
	private static final String INPUT_PARAM_URL = "inPermitUrl";

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Permit permitRequest = (Permit) workItem.getParameter(INPUT_PARAM_NEW_ORDER);
		String url = (String) workItem.getParameter(INPUT_PARAM_URL);

		Map<String, Object> results = new HashMap<String, Object>();

		Permit permitResponse = permitRequest(permitRequest, url);
		System.out.println("submit permit request " + permitRequest.getType().getString() + " permit response: " + permitResponse);

		results.put(permitRequest.getType().getString(), permitResponse);

		manager.completeWorkItem(workItem.getId(), results);
	}

	private Permit permitRequest(Permit permitRequest, String url) {

		String jsonBody = getJsonBody(permitRequest);
		System.out.println("submit permit request url: " + url);

		CloseableHttpResponse response = null;
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

		Permit permitResponse = null;

		try {
			httpPost.setEntity(new StringEntity(jsonBody));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			response = client.execute(httpPost);
			System.out.println(response.toString());
			String jsonResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper();
			permitResponse = mapper.readValue(jsonResponse, Permit.class);

			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return permitResponse;

	}

	private String getJsonBody(Permit permitRequest) {
		ObjectMapper objectMapper = new ObjectMapper();

		String json = null;
		try {
			json = objectMapper.writeValueAsString(permitRequest);
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		return json;
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	@Override
	public void close() {

	}

}
