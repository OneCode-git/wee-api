/**
 * 
 */
package com.wee.controller;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.LoggerFactory;
//github.com/ChaitanyaBudapaneti/wee-api.git
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import eu.bitwalker.useragentutils.UserAgent;
import com.wee.entity.Url;
import com.wee.service.UrlClickService;
import com.wee.service.UrlService;
import com.wee.util.Commons;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author chaitu
 *
 */
@RestController
@RequestMapping("/")
public class UrlController {
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(UrlController.class);
	@Autowired UrlService urlService;
	@Autowired UrlClickService urlClickService;
		
	@GetMapping("{hash}")
	void redirect(@PathVariable("hash") String hash,HttpServletRequest request, HttpServletResponse httpServletResponse,@RequestHeader("User-Agent") String userAgentString) {
		LOGGER.info("Redirect request recieved for hash:  "+hash+ " with user-Agent: "+userAgentString);
		Optional<Url> oUrl = urlService.findByHash(hash);
//		urlClickService.save(userAgent, hash);
		String ipAddress = urlClickService.getIpAddress(request);
		UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
		List<String> userAgentDerivatives = urlClickService.getValuesFromUserAgent(userAgent);
		urlClickService.saveInUrlClick(userAgentString, hash, ipAddress, userAgentDerivatives );
		oUrl.ifPresent(url->{
		    httpServletResponse.setHeader("Location", url.getOriginalUrl());
		    httpServletResponse.setStatus(302);
		    LOGGER.info("Redirected request for hash:  "+hash+ " with user-Agent: "+userAgent);
		});
	}
	
	
	@GetMapping("c/{hash}")
	void redirectWithClickId(@PathVariable("hash") String hash,HttpServletRequest request,HttpServletResponse httpServletResponse,@RequestHeader("User-Agent") String userAgentString) {
		LOGGER.info("Redirect request recieved to store clickID for hash:  "+hash+ " with user-Agent: "+userAgentString);
		Optional<Url> oUrl = urlService.findByHash(hash);
//		urlClickService.save(userAgent, hash);
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
		    LOGGER.info("Redirected request to store clickID for hash:  "+hash+ " with user-Agent: "+userAgent);
		});

	}
	
	@GetMapping("details/{hash}")
	Optional<Url> findById(@PathVariable("hash") String hash) {
		return urlService.findByHash(hash);
		
	}
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(path= "", consumes = "application/json", produces = "text/plain")
	ResponseEntity<String> create(@RequestBody Url request) {
		LOGGER.info("Create request recieved for url:  "+request);
		if(Commons.isValidURL(request.getOriginalUrl())) {
			if(request.getOriginalUrl().length() > 2000)
				return new ResponseEntity<String>("max length exceeded", HttpStatus.BAD_REQUEST);
			String shortURL = urlService.create(request,request.getMetadata());
			LOGGER.info("Create request received for url:  "+request+" processed successfully");
			return new ResponseEntity<String>(shortURL, HttpStatus.CREATED);
		}
		return new ResponseEntity<String>("invalid URL or meta data", HttpStatus.BAD_REQUEST);
	}
	
}
