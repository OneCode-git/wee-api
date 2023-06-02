/**
 * 
 */
package com.wee.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.wee.entity.Url;
import com.wee.entity.UrlClick;
import com.wee.repo.UrlClickRepo;
import com.wee.util.Commons;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chaitu
 *
 */
@Slf4j
@Service
public class UrlClickServiceImpl implements UrlClickService{

	@Autowired
	UrlClickRepo urlClickRepo;
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
			
		} catch (IOException | ParseException e) {
			log.error("unable to save urlClick ", e);
		}
		return urlClick;
	}

}
