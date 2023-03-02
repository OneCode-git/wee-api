/**
 * 
 */
package com.wee.controller;

import java.util.Date;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.UUID;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
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
	void redirect(@PathVariable("hash") String hash, HttpServletRequest request, HttpServletResponse httpServletResponse,@RequestHeader("User-Agent") String userAgentString) {
		Optional<Url> oUrl = urlService.findByHash(hash);
		String ipAddress = "";
		
        if (request != null) {
        	ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null || "".equals(ipAddress)) {
            	ipAddress = request.getRemoteAddr();
            }
        }

        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
     
        Browser browser = userAgent.getBrowser();  // To get the Browser
        String browserName = browser.getName();
        System.out.println("Browser: " + browserName);

        
        Version browserVersion = userAgent.getBrowserVersion();  // To get the Browser Version
        String version = browserVersion.getVersion();
        System.out.println("Version: " + version);

        
        DeviceType deviceType = os.getDeviceType();  // To get the Device Type
        String deviceTypeName = deviceType.getName();
        System.out.println("Device Type: " + deviceTypeName);

        
		     urlClickService.saveInUrlClick(userAgentString, hash, ipAddress);
		     
		oUrl.ifPresent(url->{
		    httpServletResponse.setHeader("Location", url.getOriginalUrl());
		    httpServletResponse.setStatus(302);
		});
		
	}
	
	
	@GetMapping("c/{hash}")
	void redirectWithClickId(@PathVariable("hash") String hash, HttpServletResponse httpServletResponse,@RequestHeader("User-Agent") String userAgent) {
		Optional<Url> oUrl = urlService.findByHash(hash);
    //	urlClickService.save(userAgent, hash);
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
	
}
