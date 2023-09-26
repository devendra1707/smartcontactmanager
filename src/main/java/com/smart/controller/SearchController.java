package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

// we are making it JSON
// it will conrtoller same as String
@Controller
@RequestMapping(value = "/search")
public class SearchController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	@GetMapping("/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		System.out.println(query); 
		
     User user= this.userRepository.getUserbyUserName(principal.getName());
	List<Contact> contacts= this.contactRepository.findByNameContainingAndUser(query, user);
	return ResponseEntity.ok(contacts);
	}
}
