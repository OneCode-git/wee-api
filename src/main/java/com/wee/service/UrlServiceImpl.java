/**
 * 
 */
package com.wee.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.wee.entity.Url;
import com.wee.mybatis.mapper.UrlMapper;
import com.wee.repo.UrlRepo;
import com.wee.util.Commons;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author chaitu
 *
 */
@Service
public class UrlServiceImpl implements UrlService{
	private static final Logger logger = LoggerFactory.getLogger(UrlClickServiceImpl.class);
	@Autowired
	UrlRepo urlRepo;
	@Autowired UrlMapper urlMapper;
	@Value("${wee.base.url}")
	String weeBaseUrl;
	
	/* (non-Javadoc)
	 * @see com.wee.service.UrlService#findByHash()
	 */
	@Override
	public Optional<Url> findByHash(String hash) {
		return urlRepo.findById(hash);
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
		return weeBaseUrl+hash;
	}
	
	String generateTinyUrl(Url url , String metadata) {
		String hash = Commons.genHash(url.getOriginalUrl());
		Timestamp created_at  = new Timestamp(System.currentTimeMillis());
		if (isCollisionDetected(hash)){
			generateTinyUrl(url,metadata);
		}
		url.setHash(hash);
		url.setCreatedTs(created_at);
		url.setMetadata(metadata);
		try {
			urlRepo.save(url);
			logger.info("Saved tiny url for url: "+url+" successfully");
		}catch (DataIntegrityViolationException e) {
			logger.error("Failed to save tiny url for url: "+url);
			return generateTinyUrl(url,metadata);
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

}
