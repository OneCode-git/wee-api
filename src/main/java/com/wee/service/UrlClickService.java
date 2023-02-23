/**
 * 
 */
package com.wee.service;

import com.wee.entity.UrlClick;

/**
 * @author chaitu
 *
 */
public interface UrlClickService {
	
	UrlClick save(String userAgent, String Urlid);
}
