/**
 * 
 */
package com.wee.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wee.entity.UrlClick;

/**
 * @author chaitu
 *
 */
public interface UrlClickRepo extends JpaRepository<UrlClick, String>{

}
