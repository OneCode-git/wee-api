/**
 * 
 */
package com.wee.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.wee.entity.Url;

/**
 * @author chaitu
 *
 */
public interface UrlService {
	Optional<Url> findByHash(String hash);
	String create(Url url);

	Map<String, String> getShortUrls(List<String> dataList);
}
