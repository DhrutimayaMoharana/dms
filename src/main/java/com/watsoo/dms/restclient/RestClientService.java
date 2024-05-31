package com.watsoo.dms.restclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClientService {

	RestTemplate restTemplate = new RestTemplate();

	@Value("${external.service.username}")
	private String username;

	@Value("${external.service.password}")
	private String password;

	@Value("${external.service.baseurl.event}")
	private String baseUrl;

	public String fetchEventDataFromExternalService(int deviceId, String type, String fromTime, String toTime) {

		ResponseEntity<String> responseEntity = null;
		try {
			String eventUrl=baseUrl + "/api/reports/events";
			String url = buildUrl(eventUrl, deviceId, type, fromTime, toTime);
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setBasicAuth(username, password);
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("cache-control", "no-cache");

			HttpEntity<String> entity = new HttpEntity<>(headers);
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			if (!responseEntity.getStatusCode().is2xxSuccessful()) {
				return null;
			} else {
				return responseEntity.getBody();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String buildUrl(String baseUrl, int deviceId, String type, String fromTime, String toTime)
			throws UnsupportedEncodingException {
		StringBuilder url = new StringBuilder(baseUrl);
		url.append("?");
		url.append("deviceId=").append(URLEncoder.encode(String.valueOf(deviceId), StandardCharsets.UTF_8.toString()));
		url.append("&type=").append(URLEncoder.encode(type, StandardCharsets.UTF_8.toString()));
		url.append("&from=").append(fromTime); // Do not encode again
		url.append("&to=").append(toTime); // Do not encode again
		return url.toString();
	}

	public String getPositions(List<Long> positionIdList) {
		try {
			ResponseEntity<String> responseEntity = null;
			String url = constructUrl(positionIdList);

			HttpHeaders headers = new HttpHeaders();
			headers.setBasicAuth(username, password);
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("cache-control", "no-cache");

			HttpEntity<String> entity = new HttpEntity<>(headers);
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			if (!responseEntity.getStatusCode().is2xxSuccessful()) {
				return null;
			} else {
				return responseEntity.getBody();
			}
		} catch (Exception e) {
			return null;
		}

	}

	private String constructUrl(List<Long> positionIdList) {
		if (positionIdList == null || positionIdList.isEmpty()) {

			return baseUrl + "/api/positions";
		}

		// Construct the URL dynamically based on positionIdList
		StringBuilder urlBuilder = new StringBuilder(baseUrl + "/api/positions?id=");
		for (int i = 0; i < positionIdList.size(); i++) {
			urlBuilder.append(positionIdList.get(i));
			// Append "&id=" for all elements except the last one
			if (i < positionIdList.size() - 1) {
				urlBuilder.append("&id=");
			}
		}

		return urlBuilder.toString();
	}

}
