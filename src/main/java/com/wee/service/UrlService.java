/**
 * 
 */
package com.wee.service;

import java.util.Optional;

import com.wee.entity.Url;

/**
 * @author chaitu
 *
 */
public interface UrlService {
	Optional<Url> findByHash(String hash);
	String create(Url url, String metadata);
}
