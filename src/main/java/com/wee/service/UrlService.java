/**
 * 
 */
package com.wee.service;


import java.util.List;
import java.util.Optional;
import com.wee.entity.Url;
import org.json.JSONObject;


/**
 * @author chaitu
 *
 */
public interface UrlService {
	Optional<Url> findByHash(String hash);
	Optional<Url> findByHashV2(String hash);
	String create(Url url, String metadata);

	String createV2(Url url, String metadata);

	//void updateEventAndSaveUrlClick(JSONObject metaData, String userAgentString, String hash, String ipAddress, List<String> userAgentDerivatives);
	void updateUrlClickDb();
}
