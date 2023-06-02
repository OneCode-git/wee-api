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
	@org.hibernate.annotations.Type(type="uuid-char")
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
	
	@Column(name="user_ip")
	String userIp;
	
	public UrlClick(Capabilities capabilities) {
		browser = capabilities.getBrowser();
		browserMajorversion = (capabilities.getBrowserMajorVersion());
		browserType = (capabilities.getBrowserType());
		deviceType = (capabilities.getDeviceType());
		platform = (capabilities.getPlatform());
		platformVersion = (capabilities.getPlatformVersion());
	}
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUrlId() {
		return urlId;
	}

	public void setUrlId(String urlId) {
		this.urlId = urlId;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getBrowserType() {
		return browserType;
	}

	public void setBrowserType(String browserType) {
		this.browserType = browserType;
	}

	public String getBrowserMajorversion() {
		return browserMajorversion;
	}

	public void setBrowserMajorversion(String browserMajorversion) {
		this.browserMajorversion = browserMajorversion;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getPlatformVersion() {
		return platformVersion;
	}

	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}

	public Timestamp getCreatedTs() {
		return createdTs;
	}

	public void setCreatedTs(Timestamp createdTs) {
		this.createdTs = createdTs;
	}
	
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getUserIp() {
		return userIp;
	}
}
