package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
// will fetch all contact using contact repository

	// will implement method here for pagination
	@Query("from Contact as c where c.user.id =:userId")
	// page info will have
	// 1 currenPage - page
	// contact per page- pageś
	public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable); // here we are the user the
																							// contact user id
// search method 
	// we are creating a custom finder method
	// it will search from keyword but will use and for conditions
	// it will search in logged in user

	public List<Contact> findByNameContainingAndUser(String name, User user);
}
