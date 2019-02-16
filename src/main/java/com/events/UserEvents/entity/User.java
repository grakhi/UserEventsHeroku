package com.events.UserEvents.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import javax.persistence.CascadeType;

import com.events.UserEvents.entity.Event;

@Entity
public class User {

	
	
	@Id
	@GeneratedValue
	private long id;
	
	private String firstName ;
	
	private String lastName ;
	
	private String email ; 
	
	private String location ; 
	
	private String state;
	
	private String password ;
	
	//-----------------------------------------------
	
	@OneToMany(mappedBy="userCreated")
	private List<Event> eventsCreated = new ArrayList();
	
	/** User is owner 
	 * Used the Default which is Lazy fetch, 
	 * cascade=CascadeType.REMOVE OR cascade ALL should not be done as inaccurate results and performance issues
	 */
	
	@ManyToMany( fetch = FetchType.LAZY  ) 
	@JoinTable(name="user_event" /*, 
	             joinColumns = @joinColumn (name="user_id"),
	
	            inverseJoinColumns = @JoinColumn(name="user_id") */)
	
	private List<Event> events = new ArrayList();  //Events joining
	
	
	@OneToMany(mappedBy="userSent")
	private List<Message> messages=new ArrayList();
	
	
	//-----------------------------------------------
	
	
	public List<Message> getMessages() {
		return messages;
	}

	public void addMessage(Message message) {
		this.messages.add ( message );
	}


	@ManyToMany
	private List<Role> roles = new ArrayList();
	
	
	public List<Event> getEvents() {    //Get Events joining
		return events;
	}

	public List<Event> getEventsCreated() {
		return eventsCreated;
	}

	private void printEvents() {
		System.out.println("printing events : " );
		for(Object result: events) {
			Event event = (Event)result ;
			System.out.println(event.getEventName());
		}
	}
	
	public void addEventJoining(Event e) {
		
		//this.events.add(e);
		
		this.getEvents().add(e);
		
	}
	
	
	public void removeEventJoining(Event e) {
		
		this.getEvents().remove(e);
	}
	
	public void addEvent( Event event) { 
		  this.eventsCreated.add(event);
		}

	public void removeEvent(Event event)
	{
		this.eventsCreated.remove(event);
	}


	protected User() {
		
		
	}
	
	
	public User(String firstName, String lastName, String email, String location, String state, String password) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.location = location;
		this.state = state;
		this.password = password;
	}


	public long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	public String getEmail() {
		return email;
	}

	public String getLocation() {
		return location;
	}

	public String getState() {
		return state;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		
		this.password = password ;
	}

	public List<Role> getRoles() {
		return roles;
	}
	
	
	public void addRole(Role role) {
		this.roles.add(role);
	}
	
}
