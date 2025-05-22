/**
 * 
 */
package com.wee.entity;

import java.io.Serializable;


import org.hibernate.Hibernate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.voodoodyne.jackson.jsog.JSOGGenerator;

import lombok.Data;

import jakarta.persistence.MappedSuperclass;

/**
 * @author chaitu
 *
 */
@MappedSuperclass
@Data
//@JsonIdentityInfo(generator=JSOGGenerator.class)
public class BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;


}
