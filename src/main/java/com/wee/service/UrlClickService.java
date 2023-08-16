/**
 * 
 */
package com.wee.service;

import com.wee.entity.UrlClick;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import eu.bitwalker.useragentutils.UserAgent;

/**
 * @author chaitu
 *
 */
public interface UrlClickService {
	UrlClick save(String userAgent, String Urlid);

	List<String> getValuesFromUserAgent(UserAgent userAgent);

	String getIpAddress(HttpServletRequest request);

	void saveInUrlClick(String userAgentString, String Urlid, String IpData, UserAgent userAgent);
}
