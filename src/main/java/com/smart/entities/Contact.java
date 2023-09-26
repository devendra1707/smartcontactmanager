package com.smart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity 
@Table(name="CONTACT")
public class Contact {
	/*
	 * @Override public String toString() { return "Contact [cId=" + cId + ", name="
	 * + name + ", secondName=" + secondName + ", work=" + work + ", email=" + email
	 * + ", phone=" + phone + ", image=" + image + ", description=" + description +
	 * ", user=" + user + "]"; }
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
private int cId;
private String name;
private String secondName;
private String work;
private String email;
private String phone;
private String image;
@Column(length= 5000)
private String description;
@ManyToOne
@JsonIgnore
private User user; // here we are stopping user from geting serialized
public User getUser() {
	return user;
}
public void setUser(User user) {
	this.user = user;
}
public int getcId() {
	return cId;
}
public void setcId(int cId) {
	this.cId = cId;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getSecondName() {
	return secondName;
}
public void setSecondName(String secondName) {
	this.secondName = secondName;
}
public String getWork() {
	return work;
}
public void setWork(String work) {
	this.work = work;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getPhone() {
	return phone;
}
public void setPhone(String phone) {
	this.phone = phone;
}
public String getImage() {
	return image;
}
public void setImage(String image) {
	this.image = image;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}
// here this i contact id and type caste the object is type casted if it equal 
// internallt called by jvm and if it matches any id then it will be removed from db
@Override
public boolean equals(Object obj) {
	return this.cId==((Contact)obj).getcId();
}
}
