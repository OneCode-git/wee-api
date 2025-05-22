/**
 * 
 */
package com.wee.entity;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import com.blueconic.browscap.Capabilities;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
	@JdbcTypeCode(SqlTypes.VARCHAR)
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

	public UrlClick(UserAgent userAgent) {
		browser = Objects.nonNull(userAgent.getBrowser()) ? userAgent.getBrowser().getName() : null;
		browserMajorversion = Objects.nonNull(userAgent.getBrowserVersion()) ? userAgent.getBrowserVersion().getVersion() : null;
		browserType = Objects.nonNull(userAgent.getBrowser()) ? userAgent.getBrowser().getBrowserType().getName() : null;
		deviceType = Objects.nonNull(userAgent.getOperatingSystem()) ? userAgent.getOperatingSystem().getDeviceType().getName() : null;
		platform = Objects.nonNull(userAgent.getOperatingSystem()) ? userAgent.getOperatingSystem().getName() : null;
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
