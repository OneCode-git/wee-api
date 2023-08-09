/**
 * 
 */
package com.wee.service;

import com.wee.entity.UrlClick;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.wee.mybatis.mapper.UrlMapper;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * @author chaitu
 *
 */
public interface UrlClickService {
	UrlClick save(String userAgent, String Urlid);

	List<String> getValuesFromUserAgent(UserAgent userAgent);

	String getIpAddress(HttpServletRequest request);

	void saveInUrlClick(String userAgent, String Urlid, String IpData, List<String> userAgentDerivatives);
}
