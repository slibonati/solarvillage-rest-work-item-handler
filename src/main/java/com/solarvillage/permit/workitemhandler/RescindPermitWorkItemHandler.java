package com.solarvillage.permit.workitemhandler;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;

import com.solarvillage.permit.model.Permit;

public class RescindPermitWorkItemHandler implements WorkItemHandler, Cacheable {

	private static final String INPUT_PARAM_PERMIT = "inPermitRescind";
	private static final String INPUT_PARAM_URL = "inPermitUrl";

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		System.out.println("RescindPermitWorkItemHandler invoked ...");

		Permit permit = (Permit) workItem.getParameter(INPUT_PARAM_PERMIT);
		String url = (String) workItem.getParameter(INPUT_PARAM_URL);

		rescindPermit(permit.getId(), url);

		manager.completeWorkItem(workItem.getId(), null);
	}

	private void rescindPermit(Long id, String url) {

		CloseableHttpResponse response = null;
		CloseableHttpClient client = HttpClients.createDefault();
		HttpDelete httpDelete = new HttpDelete(url + "/" + id);

		try {

			httpDelete.setHeader("Accept", "application/json");
			httpDelete.setHeader("Content-type", "application/json");

			response = client.execute(httpDelete);
			System.out.println(response.toString());

			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}

	@Override
	public void close() {

	}

}
