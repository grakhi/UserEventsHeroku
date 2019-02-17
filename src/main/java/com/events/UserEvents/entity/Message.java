package com.events.UserEvents.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.events.UserEvents.entity.User;

@Entity
public class Message {

		@Id
		@GeneratedValue
		long id ;
		
		Timestamp message_time ;
		
		String message ;
		

		
		
		@ManyToOne
		private Event eventMessage;

		@ManyToOne
		private User userSent;
		
		
		
		
		public User getUserSent() {
			return userSent;
		}

		public void setUserSent(User user) {
			this.userSent = user;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		protected Message() {
			
		}
		
		public Message(Timestamp time, String message) {
			
			this.message_time = time;
			this.message = message;
			
		}

		
		
		public Event getEvent() {
			return eventMessage;
		}


		public void setEvent(Event event) {
			this.eventMessage = event;
		}


		public Message(String message, Timestamp time) {
			
			this.message = message;
			this.message_time = time;
		}

		
		public long getId() {
			return id;
		}

		public String getMessage() {
			return message;
		}

		public Timestamp getTime() {
			return message_time;
		}

		
		
	
	
}
