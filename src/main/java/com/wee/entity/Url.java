/**
 * 
 */
package com.wee.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chaitu
 *
 */
@Entity
@Table(name="url")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Url extends BaseEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="hash")
	String hash;
	
	@Column(name="original_url")
	String originalUrl;
	
	@Column(name="created_ts")
	Timestamp createdTs;
	

	@Column(name="expires_on")
	Timestamp expiresOn;
	
	@Column(name="metadata")
	String metadata;
	
	@Transient
	Boolean genClickId;

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public Timestamp getCreatedTs() {
		return createdTs;
	}

	public void setCreatedTs(Timestamp createdTs) {
		this.createdTs = createdTs;
	}

	public Timestamp getExpiresOn() {
		return expiresOn;
	}
	
	public String getMetadata() {
		return metadata;
	}
	
	public void setMetadat(String metadata) {
		this.metadata = metadata;
	}

	public void setExpiresOn(Timestamp expiresOn) {
		this.expiresOn = expiresOn;
	}

	public Boolean getGenClickId() {
		return genClickId;
	}

	public void setGenClickId(Boolean genClickId) {
		this.genClickId = genClickId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	
}
