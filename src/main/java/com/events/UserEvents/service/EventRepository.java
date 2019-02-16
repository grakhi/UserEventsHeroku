package com.events.UserEvents.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.events.UserEvents.entity.Event;
import com.events.UserEvents.entity.User;


@Transactional
public interface EventRepository extends JpaRepository<Event, Long> {
	 
	
	List<Event> findByStateIgnoreCase(String state);
	
	List<Event>  findByUserCreated(User user);
	
	List<Event> findByUserCreatedAndStateIgnoreCase( User user,String state );
	
	/*@Query("SELECT e.event_name, e.event_state, 1 AS status FROM Event e where e.user_create_id = ?1 AND e.state = ?2")
	List<Event> findByUserCreatedAndState( long userId,String state );
	*/
	
	List<Event> findByUserCreatedNotAndStateIgnoreCase(User user, String state );
	
	
	List<Event> findByUserCreatedAndStateNotIgnoreCase(User user, String state);
	
	List<Event> findByUserCreatedNotAndStateNotIgnoreCase(User user, String state);
	
	
}
