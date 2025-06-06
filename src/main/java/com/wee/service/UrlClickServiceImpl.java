/**
 * 
 */
package com.wee.service;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wee.entity.UrlClick;
import com.wee.mybatis.mapper.UrlMapper;
import com.wee.repo.UrlClickRepo;
import com.wee.util.Commons;
import com.wee.util.Constants;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import in.zet.commons.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author chaitu
 *
 */
@Slf4j
@Service
public class UrlClickServiceImpl implements UrlClickService{
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UrlClickServiceImpl.class);
	
	@Autowired
	UrlClickRepo urlClickRepo;
	
	@Autowired
	UrlMapper urlMapper;

	@Autowired
	private ObjectMapper mapper;

	/* (non-Javadoc)
	 * @see com.wee.service.UrlClickService#save(com.wee.entity.UrlClick)
	 */
	@Override
	public UrlClick save(String userAgent, String urlId) {
		UrlClick urlClick = null;
		try {
			Capabilities capabilities = Commons.parseUserAgent(userAgent);

			urlClick = new UrlClick(capabilities);
//			Url url = new Url();
//			url.setHash(urlId);
			urlClick.setUrlId(urlId);
			urlClick.setCreatedTs(new Timestamp(new Date().getTime()));
			urlClick.setId(UUID.randomUUID());
			urlClick = urlClickRepo.save(urlClick);
			logger.info("saved url click data for urlId: "+urlId+" successfully");
			
		} catch (IOException | ParseException e) {
			logger.error("unable to save urlClick ", e);
		}
		return urlClick;
	}

	public void saveInUrlClick(String userAgentString, String urlId, String ipData, UserAgent userAgent) {
		UrlClick urlClick = null;
		try {

			urlClick = new UrlClick(userAgent);
			urlClick.setUrlId(urlId);
			urlClick.setCreatedTs(new Timestamp(new Date().getTime()));
			urlClick.setUserIp(ipData);

			String redisKey = Constants.REDIS_URL_CLICK + urlId + "_" + Instant.now().toEpochMilli();
			int seconds = (int) TimeUnit.DAYS.toSeconds(1);
			RedisUtils.setex(redisKey, seconds, mapper.writeValueAsString((urlClick)));
//			urlMapper.saveInUrlClick(urlClick);

		} catch (Exception e) {
			logger.error("unable to save urlClick ", e);
			logger.info("LeaderBoard Controller Exception" + e.getMessage());
		}

	}

	public List<String> getValuesFromUserAgent(UserAgent userAgent) {
		String browserName = null;
		String version = null;
		String deviceTypeName = null;
		List<String> values = new ArrayList<String>();
		Browser browser = userAgent.getBrowser(); // To get the Browser
		if (browser != null)
			browserName = browser.getName();

		Version browserVersion = userAgent.getBrowserVersion(); // To get the Browser Version
		if (browserVersion != null)
			version = browserVersion.getVersion();

		OperatingSystem deviceType = userAgent.getOperatingSystem();
		if (deviceType != null) // To get the Device Type
			deviceTypeName = deviceType.getName();

		values.add(browserName);
		values.add(version);
		values.add(deviceTypeName);

		return values;

	}

	public String getIpAddress(HttpServletRequest request) {
		String ipAddress = "";
		if (request != null) {
			ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null || "".equals(ipAddress)) {
				ipAddress = request.getRemoteAddr();
			}
		}
		return ipAddress;
	}

}
