package com.events.UserEvents.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Role {
	
	@Id
	@GeneratedValue
	
	long id ; 
	
	String name;
	
	/*
	@ManyToMany(mappedBy="roles")
	List<User> secureUsers = new ArrayList<User>();
*/
	
	
	
	protected Role() {
		
	}
	
	public Role(String name) {
		
		this.name = name;
	}

	public Role(int id , String name) {
		
		this.id = id ;
		this.name = name;
	}
	

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
