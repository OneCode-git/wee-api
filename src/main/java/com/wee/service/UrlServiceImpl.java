/**
 * 
 */
package com.wee.service;

import java.sql.Timestamp;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.wee.entity.Url;
import com.wee.mybatis.mapper.UrlMapper;
import com.wee.repo.UrlRepo;
import com.wee.util.Commons;

/**
 * @author chaitu
 *
 */
@Service
public class UrlServiceImpl implements UrlService{

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
	public String create(Url url) {
		String hash = generateTinyUrl(url);
		if (url.getGenClickId() != null && url.getGenClickId() == true) {
			return weeBaseUrl+ "c/" + hash;
		}
		return weeBaseUrl+hash;
	}
	
	String generateTinyUrl(Url url) {
		String hash = Commons.genHash(url.getOriginalUrl());
		Timestamp created_at  = new Timestamp(System.currentTimeMillis());
		url.setHash(hash);
		url.setCreatedTs(created_at);
		try {
			urlRepo.save(url);
		}catch (DataIntegrityViolationException e) {
			return generateTinyUrl(url);
		}
		return hash;
	}

	@Override
	public Map<String, String> getShortUrls(List<String> dataList){
		Map<String, String> data = new HashMap<>();
		List<Url> urlsList = new ArrayList<>();
		for(String str : dataList){
			String hash = Commons.genHash(str);
			data.put(str,weeBaseUrl + hash);
			urlsList.add(Url.builder().createdTs(new Timestamp(System.currentTimeMillis())).originalUrl(str).hash(hash).build());
		}
		urlRepo.saveAll(urlsList);
		return data;
	}

}
