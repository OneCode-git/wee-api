/**
 * 
 */
package com.wee.service;

import com.wee.entity.UrlClick;
import com.wee.mybatis.mapper.UrlMapper;

/**
 * @author chaitu
 *
 */
public interface UrlClickService {
	
	UrlClick save(String userAgent, String Urlid);
	
	void saveInUrlClick(String userAgent, String Urlid, String IpData);
	
	//UrlClick saveIP(String IpData, String Urlid);
}
