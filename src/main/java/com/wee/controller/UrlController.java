/**
 * 
 */
package com.wee.controller;

import java.io.*;
import java.util.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//github.com/ChaitanyaBudapaneti/wee-api.git
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wee.entity.Url;
import com.wee.service.UrlClickService;
import com.wee.service.UrlService;
import com.wee.util.Commons;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.bitwalker.useragentutils.UserAgent;


/**
 * @author chaitu
 *
 */
@RestController
@RequestMapping("/")
public class UrlController {
	
	@Autowired UrlService urlService;
	@Autowired UrlClickService urlClickService;
	
	
	@GetMapping("{hash}")
	void redirect(@PathVariable("hash") String hash, HttpServletRequest request,
			HttpServletResponse httpServletResponse, @RequestHeader("User-Agent") String userAgentString) {
		Optional<Url> oUrl = urlService.findByHash(hash);
		String ipAddress = urlClickService.getIpAddress(request);
		UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
		List<String> userAgentDerivatives = urlClickService.getValuesFromUserAgent(userAgent);
		urlClickService.saveInUrlClick(userAgentString, hash, ipAddress, userAgentDerivatives );
		oUrl.ifPresent(url -> {
			httpServletResponse.setHeader("Location", url.getOriginalUrl());
			httpServletResponse.setStatus(302);
		});

	}
	
	
	@GetMapping("c/{hash}")
	void redirectWithClickId(@PathVariable("hash") String hash,HttpServletRequest request, HttpServletResponse httpServletResponse,@RequestHeader("User-Agent") String userAgentString) {
		Optional<Url> oUrl = urlService.findByHash(hash);
		String ipAddress = urlClickService.getIpAddress(request);
		UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
		List<String> userAgentDerivatives = urlClickService.getValuesFromUserAgent(userAgent);
		urlClickService.saveInUrlClick(userAgentString, hash, ipAddress, userAgentDerivatives );
		oUrl.ifPresent(url->{
			String templateURL = url.getOriginalUrl();
			String finalURL = templateURL.replace("%7Bclick_id%7D", UUID.randomUUID().toString());
			finalURL = finalURL.replace("%7Bepoch%7D", new Date().getTime()+ "");
		    httpServletResponse.setHeader("Location", finalURL);
		    httpServletResponse.setStatus(302);
		});
		
	}
	
	@GetMapping("details/{hash}")
	Optional<Url> findById(@PathVariable("hash") String hash) {
		return urlService.findByHash(hash);
		
	}
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(path= "", consumes = "application/json", produces = "text/plain")
	ResponseEntity<String> create(@RequestBody Url url) {
		if(Commons.isValidURL(url.getOriginalUrl())) {
			if(url.getOriginalUrl().length() > 2000)
				return new ResponseEntity<String>("max length exceeded", HttpStatus.BAD_REQUEST);
			String shortURL = urlService.create(url);
			return new ResponseEntity<String>(shortURL, HttpStatus.CREATED);
		}
		return new ResponseEntity<String>("invalid URL", HttpStatus.BAD_REQUEST);		
	}

	@GetMapping("/test")
	public JSONObject test() throws Exception {
	    File file = new File("/Users/bhaskar/Downloads/oncode_projects/wee-api/src/main/resources/data.json");
        		List<String> urlData;
        		try(InputStream in = new FileInputStream(file)){
        			String dataString = IOUtils.toString(in);
        			JSONArray array = new JSONArray(dataString);
        			 urlData = IntStream.range(0, array.length())
        					.mapToObj(i -> array.getJSONObject(i))
        					.map(obj -> obj.optString("url"))
        					.collect(Collectors.toList());
        		}
		Map<String, String> shortUrls = urlService.getShortUrls(urlData);
		Gson gson  = new Gson();
		gson.toJson(shortUrls, new FileWriter("/Users/bhaskar/Downloads/oncode_projects/wee-api/src/main/resources/result1.json"));
		return new JSONObject(shortUrls);
	}
	
}
