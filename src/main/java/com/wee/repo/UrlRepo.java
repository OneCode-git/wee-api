/**
 * 
 */
package com.wee.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wee.entity.Url;

/**
 * @author chaitu
 *
 */
@Repository
public interface UrlRepo extends JpaRepository<Url, String>{
	
}
