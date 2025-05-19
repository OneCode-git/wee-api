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
import com.wee.util.HeaderUtils;
import com.wee.util.UrlValidator;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

/**
 * @author chaitu
 *
 */
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

	private static final Pattern CRLF_PATTERN = Pattern.compile("[\\r\\n]");

	private void setLocationHeaderSafely(HttpServletResponse response, String value, String hash) {
		try {
			if (value == null || CRLF_PATTERN.matcher(value).find()) {
				LOGGER.error("CRLF injection attempt in header value for hash: {}", hash);
				throw new SecurityException("Invalid header value detected");
			}
			
			// Encode value before setting header
			String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
			response.setHeader("Location", encodedValue);
			LOGGER.debug("Header set successfully for hash: {}", hash);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Error encoding URL for hash: {}", hash, e);
			throw new SecurityException("Error setting header value", e);
		}
	}

	@GetMapping("{hash}")
	void redirect(@PathVariable("hash") String hash,HttpServletRequest request, HttpServletResponse httpServletResponse,@RequestHeader("User-Agent") String userAgentString) {
		LOGGER.info("Redirected request for hash:  "+hash+ " with user-Agent: "+userAgentString+" with httprequest: "+request +" with http-response: "+httpServletResponse );
		Optional<Url> oUrl = urlService.findByHash(hash);
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

		oUrl.ifPresent(url -> {
			try {
				String originalUrl = url.getOriginalUrl();
				// Validate and sanitize URL
				if (originalUrl == null || originalUrl.contains("\r") || originalUrl.contains("\n")) {
					LOGGER.error("CRLF injection attempt detected for hash: {}", hash);
					throw new IllegalArgumentException("Invalid URL characters detected");
				}
		
				String sanitizedUrl = UrlValidator.sanitizeUrl(originalUrl);
				String encodedUrl = UriUtils.encodePath(sanitizedUrl, StandardCharsets.UTF_8);
		
				// Final header value validation
				if (encodedUrl.contains("\r") || encodedUrl.contains("\n")) {
					LOGGER.error("CRLF detected in encoded URL for hash: {}", hash);
					throw new IllegalArgumentException("Invalid header value");
				}
		
				setLocationHeaderSafely(httpServletResponse, encodedUrl, hash);
				httpServletResponse.setStatus(302);
				LOGGER.info("Redirected request for hash: {}", hash);
			} catch (IllegalArgumentException e) {
				LOGGER.error("Security violation - Invalid header value for hash: {}", hash, e);
				throw new SecurityException("Invalid redirect URL", e);
			}
		});
	}

	@GetMapping("c/{hash}")
	public ResponseEntity<Void> redirect(@PathVariable("hash") String hash) {
		if (hash.contains("\r") || hash.contains("\n")) {
			return ResponseEntity.badRequest().build();
		}
		URI uri = URI.create("https://develop.d1mkcl7qyojtwo.amplifyapp.com/redirection/" + URLEncoder.encode(hash, StandardCharsets.UTF_8));
		return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
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
        oUrl.ifPresent(url -> {
            try {
                // Extract and validate URL
                String templateURL = url.getOriginalUrl();
                if (templateURL == null || templateURL.contains("\r") || templateURL.contains("\n")) {
                    LOGGER.error("CRLF injection attempt detected for hash: {}", hash);
                    throw new IllegalArgumentException("Invalid URL characters detected");
                }

                // Process URL
                String finalURL = templateURL.replace("%7Bclick_id%7D", UUID.randomUUID().toString())
                                           .replace("%7Bepoch%7D", String.valueOf(new Date().getTime()));
                
                // Sanitize and encode
                String sanitizedUrl = UrlValidator.sanitizeUrl(finalURL);
                String encodedUrl = UriUtils.encodePath(sanitizedUrl, StandardCharsets.UTF_8);
                
                // Set headers safely
                setLocationHeaderSafely(httpServletResponse, encodedUrl, hash);
                httpServletResponse.setStatus(302);
                LOGGER.info("Redirected request for hash: {} with validated URL", hash);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Security violation - URL validation failed for hash: {}", hash, e);
                throw new SecurityException("Invalid URL detected", e);
            }
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

	@GetMapping("t/{hash}")
	public ResponseEntity<String> redirectView(@PathVariable("hash") String hash, HttpServletResponse response) throws IOException {

		Optional<Url> urlOpt = urlService.findByHash(hash);

		if (urlOpt.isPresent()) {
			Url url = urlOpt.get();

			// Generate HTML for app detection and redirection
			String htmlContent = "<html>" +
					"<head>" +
					"<meta http-equiv=\"refresh\" content=\"0; url=" + StringEscapeUtils.escapeHtml4(url.getOriginalUrl()) + "\" />" +
					"<script>window.location.href='" + StringEscapeUtils.escapeHtml4(url.getOriginalUrl()) + "';</script>" +
					"</head>" +
					"<body>" +
					"Redirecting..." +
					"</body>" +
					"</html>";

			// Set content type and write HTML
			return ResponseEntity.ok()
					.header("Content-Type", "text/html")
					.body(StringEscapeUtils.escapeHtml4(htmlContent));
		} else {
			return ResponseEntity.internalServerError().body("Something went wrong");
		}

	}
}
