/**
 * 
 */
package com.wee.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wee.dto.UrlBulkDto;
import com.wee.entity.EventsLogHelper;
import com.wee.entity.Url;
import com.wee.service.UrlClickService;
import com.wee.service.UrlService;
import com.wee.util.Commons;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import static com.wee.util.Constants.REDIRECTION_PATH;

/**
 * @author chaitu
 *
 */
@Slf4j
@RestController
@RequestMapping("/")
public class UrlController {
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(UrlController.class);
	@Autowired UrlService urlService;
	@Autowired
	EventsLogHelper eventsLogHelper;
	@Autowired
	private UrlClickService urlClickService;

	@Value("${wee.base.url}")
	String weeBaseUrl;

	@Value("${redirection.base.url}")
	String redirectionBaseUrl;

	private Executor executor;

	@PostConstruct
	public void init(){
		this.executor = getExecutor();
	}

	private Executor getExecutor(){
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setQueueCapacity(100);
		taskExecutor.setCorePoolSize(15);
		taskExecutor.setMaxPoolSize(20);
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		taskExecutor.initialize();
		return taskExecutor;
	}

	@GetMapping("{hash}")
	void redirect(@PathVariable("hash") String hash,HttpServletRequest request, HttpServletResponse httpServletResponse,@RequestHeader("User-Agent") String userAgentString) {
		LOGGER.info("Redirect request recieved for hash:  "+hash+ " with user-Agent: "+userAgentString);
		Optional<Url> oUrl = urlService.findByHashV2(hash);
		String ipAddress = urlClickService.getIpAddress(request);
		UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
		List<String> userAgentDerivatives = urlClickService.getValuesFromUserAgent(userAgent);
		if(oUrl.isPresent()){
//			JSONObject metaData = Objects.isNull(oUrl.get().getMetadata()) ? new JSONObject() : new JSONObject(oUrl.get().getMetadata());
//			metaData.put("Browser",userAgentDerivatives.get(0));
//			metaData.put("BrowserMajorVersion",userAgentDerivatives.get(1));
//			metaData.put("DeviceType",userAgentDerivatives.get(2));
//			metaData.put("ipAddress",ipAddress);
//			String Url="";
//			if(oUrl.get().getGenClickId()!=null && oUrl.get().getGenClickId()){
//				Url = weeBaseUrl+ "c/" + hash;
//			}
//			else
//				Url = weeBaseUrl+ "c/" + hash;
//			metaData.put("Url",Url);

//			urlService.updateEventAndSaveUrlClick(metaData,userAgentString, hash, ipAddress, userAgentDerivatives);
//			eventsLogHelper.addAgentEvent(metaData);
			Date startDate = new Date();
			urlClickService.saveInUrlClick(userAgentString, hash, ipAddress, userAgent);
			Date endDate = new Date();
			LOGGER.info("time taken to complete save url click process : " + (endDate.getTime() - startDate.getTime()));
		}

		oUrl.ifPresent(url->{
					httpServletResponse.setHeader("Location", url.getOriginalUrl());
		    httpServletResponse.setStatus(302);
		    LOGGER.info("Redirected request for hash:  "+hash+ " with user-Agent: "+userAgent);
		});
	}

	@GetMapping("c/{hash}")
	void redirectToLoadingScreen(@PathVariable("hash") String hash, HttpServletRequest request, HttpServletResponse response, @RequestHeader("User-Agent") String userAgentString) {
		LOGGER.info("Redirection request to loading screen for hash: {}", hash);
		String redirectionUrl = redirectionBaseUrl + REDIRECTION_PATH + hash;
		response.setHeader("Location", redirectionUrl);
		response.setStatus(302);
	}
	
	
	@GetMapping("r/{hash}")
	void redirectWithClickId(@PathVariable("hash") String hash,HttpServletRequest request,HttpServletResponse httpServletResponse,@RequestHeader("User-Agent") String userAgentString) {
		LOGGER.info("Redirect request recieved to store clickID for hash:  "+hash+ " with user-Agent: "+userAgentString);
		Optional<Url> oUrl = urlService.findByHash(hash);
//		urlClickService.save(userAgent, hash);
		String ipAddress = urlClickService.getIpAddress(request);
		UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
		List<String> userAgentDerivatives = urlClickService.getValuesFromUserAgent(userAgent);
		if(oUrl.isPresent()){
//			JSONObject metaData = Objects.isNull(oUrl.get().getMetadata()) ? new JSONObject() : new JSONObject(oUrl.get().getMetadata());
//			metaData.put("Browser",userAgentDerivatives.get(0));
//			metaData.put("BrowserMajorVersion",userAgentDerivatives.get(1));
//			metaData.put("DeviceType",userAgentDerivatives.get(2));
//			metaData.put("ipAddress",ipAddress);
//			String Url="";
//			if(oUrl.get().getGenClickId()!=null && oUrl.get().getGenClickId()){
//				Url = weeBaseUrl+ "c/" + hash;
//			}
//			else
//				Url = weeBaseUrl+ "c/" + hash;
//			metaData.put("Url",Url);

//			urlService.updateEventAndSaveUrlClick(metaData,userAgentString, hash, ipAddress, userAgentDerivatives);
//			eventsLogHelper.addAgentEvent(metaData);
			Date startDate = new Date();
			urlClickService.saveInUrlClick(userAgentString, hash, ipAddress, userAgent );
			Date endDate = new Date();
			LOGGER.info("time taken to complete save url click process : " + (endDate.getTime() - startDate.getTime()));
		}
//		urlClickService.saveInUrlClick(userAgentString, hash, ipAddress, userAgentDerivatives );
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
	ResponseEntity<String> create(@RequestBody Url request) throws JsonProcessingException {
		LOGGER.info("Create request recieved for url:  "+request);
		if(Commons.isValidURL(request.getOriginalUrl())) {
			if(request.getOriginalUrl().length() > 2000)
				return new ResponseEntity<String>("max length exceeded", HttpStatus.BAD_REQUEST);
			LOGGER.info("url request in create method :  "+request);
			String shortURL = urlService.create(request, request.getMetadata());
			LOGGER.info("Create request received for url:  "+request+" processed successfully");
			return new ResponseEntity<String>(shortURL, HttpStatus.CREATED);
		}
		return new ResponseEntity<String>("invalid URL or meta data", HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/updateUrlClickDb")
	void updateUrlClickDb() {
		LOGGER.info("Request came form cron");
		try {
			urlService.updateUrlClickDb();
		}
		catch(Exception e){
			LOGGER.error("Request failed",e);
		}

	}

	@PostMapping(path= "/createBulk", consumes = "application/json", produces = "application/json")
	ResponseEntity<List<UrlBulkDto>> create(@RequestBody List<UrlBulkDto> request) {

		List<UrlBulkDto> responseList = new ArrayList<>();
		HashMap<String, CompletableFuture<String>> completableFutureList = new HashMap<>();

		for(UrlBulkDto url : request){
			if (!Commons.isValidURL(url.getOriginalUrl()) || url.getOriginalUrl().length() > 2000) {
				UrlBulkDto response = new UrlBulkDto();
				response.setOriginalUrl(url.getOriginalUrl());
				response.setErrorMessage("invalid URL or metadata");
				responseList.add(response);
				continue;
			}

			CompletableFuture<String> shortUrlFuture = CompletableFuture.supplyAsync(
				() -> urlService.create(Url.from(url), null), executor);
			completableFutureList.put(url.getOriginalUrl(), shortUrlFuture);
		}

		completableFutureList.forEach((longUrl, shortUrlFuture) -> {
			try {
				UrlBulkDto response = new UrlBulkDto();
				response.setOriginalUrl(longUrl);
				response.setShortUrl(shortUrlFuture.get());
				responseList.add(response);
			} catch (Exception e) {
				// Handle exceptions
				LOGGER.error("Error processing URL: " + longUrl + ", " + e.getMessage());
			}
		});

		return new ResponseEntity<>(responseList, HttpStatus.OK);
	}

	@PostMapping(path= "/v2/", consumes = "application/json", produces = "text/plain")
	ResponseEntity<String> createV2(@RequestBody Url request) throws JsonProcessingException {
		log.info("Create request received for url:  {}", request);
		if(Commons.isValidURL(request.getOriginalUrl())) {
			if(request.getOriginalUrl().length() > 2000)
				return new ResponseEntity<String>("max length exceeded", HttpStatus.BAD_REQUEST);
			log.info("url request in create method :  {}", request);
			String shortURL = urlService.createV2(request, request.getMetadata());
			log.info("Create request received for url: {} processed successfully", request);
			return new ResponseEntity<String>(shortURL, HttpStatus.CREATED);
		}
		return new ResponseEntity<String>("invalid URL or meta data", HttpStatus.BAD_REQUEST);
	}

	@GetMapping("s/{hash}")
	public ResponseEntity<String> redirectView(@PathVariable("hash") String hash, HttpServletResponse response) throws IOException {

		Optional<Url> urlOpt = urlService.findByHashV2(hash);

		if (urlOpt.isPresent()) {
			Url url = urlOpt.get();

			// Generate HTML for app detection and redirection
			String htmlContent = "<html>" +
					"<head>" +
					"<meta http-equiv=\"refresh\" content=\"0; url=" + url.getOriginalUrl() + "\" />" +
					"<script>window.location.href='" + url.getOriginalUrl() + "';</script>" +
					"</head>" +
					"<body>" +
					"Redirecting..." +
					"</body>" +
					"</html>";

			// Set content type and write HTML
			return ResponseEntity.ok()
					.header("Content-Type", "text/html")
					.body(htmlContent);
		} else {
			return ResponseEntity.internalServerError().body("Something went wrong");
		}

	}
}
