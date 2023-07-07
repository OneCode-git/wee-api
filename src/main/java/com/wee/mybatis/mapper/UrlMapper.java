/**
 *
 */
package com.wee.mybatis.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.wee.entity.UrlClick;
import com.wee.entity.Url;

/**
 * @author chaitu
 *
 */
@Mapper
public interface UrlMapper {
	@Insert("insert into url (hash, original_url) values(#{hash}, #{originalUrl})")
	void save(Url url);

	@Select("select hash, original_url from url where hash = #{hash}")
	Url findById(String hash);

	@Insert("INSERT INTO url_click (user_ip, url_id, browser, browser_type, browser_major_version, device_type, platform, platform_version, created_ts) VALUES (#{userIp}, #{urlId}, #{browser}, #{browserType}, #{browserMajorversion}, #{deviceType}, #{platform}, #{platformVersion}, #{createdTs})")
	void saveInUrlClick(UrlClick urlClick);

}
