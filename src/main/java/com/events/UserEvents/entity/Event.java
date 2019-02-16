package com.events.UserEvents.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CascadeType;

import com.events.UserEvents.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Event {


	@Id
	@GeneratedValue
	private long id;
	
	private String eventName ;
	
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	private Date eventDate ;
	
	private String eventLocation ;
	
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	public void setState(String state) {
		this.state = state;
	}

	private String state ;
	
	
	//-----------------------------------------------
	@ManyToOne
	
	private User userCreated;
	
	
	/**
	 * Used the Default which is Lazy fetch, 
	 * cascade=CascadeType.REMOVE OR cascade ALL should not be done as inaccurate results and performance issues
	 */
	
	@ManyToMany(mappedBy="events")   //Users joining 
	private List<User> users = new ArrayList();


	@OneToMany(mappedBy="eventMessage") //owner is Message
	private List<Message> messages=new ArrayList();
	
	
	//-----------------------------------------------

	public List<Message> getMessages() {
		return messages;
	}

	protected Event() {
		
	}
	
	public Event(String eventName, Date eventDate, String eventLocation, String state) {
		super();
		this.eventName = eventName;
		this.eventDate = eventDate;
		this.eventLocation = eventLocation;
		this.state = state;
	}

	
	public List<User> getUsers() {   //Users joining
		return users;
	}
	
	public void addUser(User user) {
		this.users.add(user);
	}

	
	public void removeUser(User user) {
		this.users.remove(user);
	}
	
	 
	
	
	
	public long getId() {
		return id;
	}

	public String getEventName() {
		return eventName;
	}

	public Date getEventDate() {
		return eventDate;
	}

	
	public String getEventLocation() {
		return eventLocation;
	}

	public String getState() {
		return state;
	}

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
	} 
	
	public void addReview( Message message) { 
		  this.messages.add(message);
	}

		

	
}
