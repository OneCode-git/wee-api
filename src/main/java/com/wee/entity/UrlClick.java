/**
 * 
 */
package com.wee.entity;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.blueconic.browscap.Capabilities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chaitu
 *
 */
@Entity
@Table(name="url_click")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlClick {
	@Id
	@Column(name="id")
	@org.hibernate.annotations.Type(type="org.hibernate.type.PostgresUUIDType")
	UUID id;
	
	@Column(name="url_id")
	String urlId;
	
	@Column(name="browser")
	String browser;
	
	@Column(name="browser_type")
	String browserType;
	
	@Column(name="browser_major_version")
	String browserMajorversion;
	
	@Column(name="device_type")
	String deviceType;
	
	@Column(name="platform")
	String platform;
	
	@Column(name="platform_version")
	String platformVersion;
	
	@Column(name="created_ts")
	Timestamp createdTs;
	
	public UrlClick(Capabilities capabilities) {
		browser = capabilities.getBrowser();
		browserMajorversion = (capabilities.getBrowserMajorVersion());
		browserType = (capabilities.getBrowserType());
		deviceType = (capabilities.getDeviceType());
		platform = (capabilities.getPlatform());
		platformVersion = (capabilities.getPlatformVersion());
	}
}
