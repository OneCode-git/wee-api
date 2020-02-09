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
}
