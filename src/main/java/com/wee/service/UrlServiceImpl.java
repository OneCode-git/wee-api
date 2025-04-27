/**
 * 
 */
package com.wee.service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wee.entity.EventsLogHelper;
import com.wee.entity.UrlClick;
import com.wee.util.Constants;
import in.zet.commons.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.wee.entity.Url;
import com.wee.mybatis.mapper.UrlMapper;
import com.wee.repo.UrlRepo;
import com.wee.util.Commons;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author chaitu
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService{
	@Autowired
	UrlRepo urlRepo;

	private final UrlMapper urlMapper;
	@Autowired
	private ObjectMapper mapper;
	@Value("${wee.base.url}")
	String weeBaseUrl;

	@Autowired
	EventsLogHelper eventsLogHelper;
	@Autowired
	private UrlClickService urlClickService;

	/* (non-Javadoc)
	 * @see com.wee.service.UrlService#findByHash()
	 */
	@Override
	public Optional<Url> findByHash(String hash) {
		String redisKey = Constants.REDIS_HASH_KEY + hash;
		if(RedisUtils.exists(redisKey)){
			Url url = getHashFromRedis(redisKey);
			if(url != null){
				return Optional.of(url);
			}
		}
		Optional<Url> url = urlRepo.findById(hash);
		if(url.isPresent()){
			setRedisData(redisKey, url.get());
		}
		return url;
	}

	/* (non-Javadoc)
	 * @see com.wee.service.UrlService#create(com.wee.entity.Url)
	 */
	@Override
	public String create(Url url, String metadata) {
		String hash = generateTinyUrl(url,metadata);
		if (url.getGenClickId() != null && url.getGenClickId() == true) {
			return weeBaseUrl+ "c/" + hash;
		}
		return weeBaseUrl+ "c/" + hash;
	}

	@Override
	public String createV2(Url url, String metadata) {
		String hash = generateTinyUrlV2(url,metadata);
		if (url.getGenClickId() != null && url.getGenClickId() == true) {
			return weeBaseUrl+ "s/" + hash;
		}
		return weeBaseUrl+ "s/" + hash;
	}

	String convertIntoJsonString(String metaData){

		if(Objects.nonNull(metaData) && !metaData.isEmpty()) {
			// Replace equal sign with colon
			String jsonString = metaData.replaceAll("=", ":");

			// Create a JSONObject from the string
			JSONObject jsonObject = new JSONObject(jsonString);

			// Convert the JSONObject to a JSON string
			String newMetaData = jsonObject.toString();
			return newMetaData;
		}

		JSONObject json = new JSONObject("{}");
		return json.toString();
	}

	String generateTinyUrl(Url url , String metaData) {
		String hash = Commons.genHash(url.getOriginalUrl());
		Timestamp created_at  = new Timestamp(System.currentTimeMillis());
		if (isCollisionDetected(hash)){
			return generateTinyUrl(url,metaData);
		}
		String jsonMetaData = convertIntoJsonString(metaData);
		url.setHash(hash);
		url.setCreatedTs(created_at);
		url.setMetadata(jsonMetaData);
		try {
			urlRepo.save(url);
			log.info("Saved tiny url for url: {} successfully", url);
		}catch (DataIntegrityViolationException e) {
			log.error("Failed to save tiny url for url: {}, error : {}", url, e.getMessage(), e);
			return generateTinyUrl(url,metaData);
		}
		return hash;
	}

	String generateTinyUrlV2(Url url , String metaData) {
		String hash = Commons.genShortCode();
		Timestamp createdAt = new Timestamp(System.currentTimeMillis());

		String jsonMetaData = convertIntoJsonString(metaData);
		url.setHash(hash);
		url.setCreatedTs(createdAt);
		url.setMetadata(jsonMetaData);

		try {
			urlRepo.save(url);
			log.info("Saved tiny url for url: {} successfully", url);
		}catch (DataIntegrityViolationException e) {
			log.error("Failed to save tiny url for url: {}, error : {}", url, e.getMessage(), e);
			return generateTinyUrlV2(url,metaData);
		}
		return hash;
	}

	public  boolean isCollisionDetected(String hash) {
		Optional<Url> url = urlRepo.findById(hash);
		if (url.isPresent()) {
			return true;
		}
		return false;
	}

	private Url getHashFromRedis(String redisKey){
		String redisValue = RedisUtils.get(redisKey);
		Url url = null;
		try{
			url = mapper.readValue(redisValue, Url.class);
		} catch (Exception e){
			log.error("Failed to get data from redis  : {}", e.getMessage(), e);
		}
		return url;
	}

	private void setRedisData(String redisKey, Url url){
		try {
			int seconds = (int) TimeUnit.DAYS.toSeconds(1);
			RedisUtils.setex(redisKey, seconds, mapper.writeValueAsString(url));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}

	public void updateUrlClickDb() {
		Set<String> keySet = RedisUtils.getKeys(Constants.REDIS_URL_CLICK + "*");
		if (CollectionUtils.isEmpty(keySet)) {
			return;
		}

		List<String> keyList = new ArrayList<>(keySet);
		int startIndex = 0, batchSize = 500;

		IntStream.iterate(startIndex, curr -> curr < keyList.size(), curr -> curr + batchSize)
				.mapToObj(start -> keyList.subList(start, Math.min(keyList.size(), start + batchSize)))
				.forEach(batch -> {
					List<UrlClick> urlClickList = new ArrayList<>();
					for (String key : batch) {
						String redisValue = RedisUtils.get(key);
						if (!StringUtils.hasText(redisValue)) {
							continue;
						}
						try {
							UrlClick urlClick = mapper.readValue(redisValue, UrlClick.class);
							urlClickList.add(urlClick);
						} catch (Exception e) {
							log.error("Failed to read data from redis", e);
						}
					}
					log.info("Url click list for update count: {}", urlClickList.size());
					urlMapper.saveInUrlClickBulk(urlClickList);
					batch.forEach(RedisUtils::del);
				});
	}

//	@Async("actionExecutor")
//	public void updateEventAndSaveUrlClick(JSONObject metaData, String userAgentString, String hash, String ipAddress, List<String> userAgentDerivatives){
//		eventsLogHelper.addAgentEvent(metaData);
//		urlClickService.saveInUrlClick(userAgentString, hash, ipAddress, userAgentDerivatives );
//	}

}
