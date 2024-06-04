package com.watsoo.dms.restclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.watsoo.dms.entity.Event;

@Component
public class RestClientService {

	RestTemplate restTemplate = new RestTemplate();

	@Value("${external.service.username}")
	private String username;

	@Value("${external.service.password}")
	private String password;

	@Value("${external.service.baseurl.event}")
	private String baseUrl;

	@Value("${file.dawnload.url}")
	String fileUrl;

	public String fetchEventDataFromExternalService(List<Integer> deviceId, String type, String fromTime,
			String toTime) {

		ResponseEntity<String> responseEntity = null;
		try {
			String eventUrl = baseUrl + "/api/reports/events";
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

	private String buildUrl(String baseUrl, List<Integer> deviceIds, String type, String fromTime, String toTime)
			throws UnsupportedEncodingException {
		StringBuilder url = new StringBuilder(baseUrl);
		url.append("?");

		for (int i = 0; i < deviceIds.size(); i++) {
			url.append("deviceId=");
			url.append(URLEncoder.encode(String.valueOf(deviceIds.get(i)), StandardCharsets.UTF_8.toString()));
			if (i < deviceIds.size() - 1) {
				url.append("&");
			}
		}
		url.append("&type=").append(URLEncoder.encode(type, StandardCharsets.UTF_8.toString()));
		url.append("&from=").append(fromTime);
		url.append("&to=").append(toTime);

		return url.toString();
	}

	public String getPositions(List<Long> positionIdList) {
		try {
			ResponseEntity<String> responseEntity = null;
			String positionsEndpoint = "/api/positions?id=";
			String url = constructUrl(positionIdList, positionsEndpoint);

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

	private String constructUrl(List<Long> positionIdList, String endPoint) {
		if (positionIdList == null || positionIdList.isEmpty()) {

			return baseUrl + endPoint;
		}

		// Construct the URL dynamically based on positionIdList
		StringBuilder urlBuilder = new StringBuilder(baseUrl + endPoint);
		for (int i = 0; i < positionIdList.size(); i++) {
			urlBuilder.append(positionIdList.get(i));
			// Append "&id=" for all elements except the last one
			if (i < positionIdList.size() - 1) {
				urlBuilder.append("&id=");
			}
		}

		return urlBuilder.toString();
	}

	public String getDeviceInformation(Set<Long> allDeviceID) {

		List<Long> deviceIDList = new ArrayList<>(allDeviceID);
		try {
			ResponseEntity<String> responseEntity = null;
			String positionsEndpoint = "/api/devices?id=";
			String url = constructUrl(deviceIDList, positionsEndpoint);

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

	public String getFilePresentOrNot(String fileName) {

		try {
			ResponseEntity<String> responseEntity = null;
			String positionsEndpoint = "/check?file=";
			String url = fileUrl + positionsEndpoint + fileName;

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

}
