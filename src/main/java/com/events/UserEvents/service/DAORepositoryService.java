package com.events.UserEvents.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;

//import com.events.UserEvents.UserRepositoryCommandLineRunner;
import com.events.UserEvents.entity.Event;
import com.events.UserEvents.entity.Message;
import com.events.UserEvents.entity.User;

@Repository
//@Transactional
public class DAORepositoryService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	EntityManager em;

	//private static final Logger log = LoggerFactory.getLogger(UserRepositoryCommandLineRunner.class);

	private Map mapEvents;

	private Map eventWallMap;

	@Transactional
	public long addUser(String firstName, String lastName, String email, String location, String state,
			String password) {

		User user = new User(firstName, lastName, email, location, state, password);

		user = saveUser(user);

		return user.getId();
	}

	@Transactional
	public User saveUser(User user) {

		user = userRepository.save(user);
		return user;

	}

	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Optional<User> findUser(Long id) {

		return userRepository.findById(id);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public Event findEventById(long eventId) {
		return eventRepository.findById(eventId).get();
	}

	public Map getEventsMap() {
		return mapEvents;
	}

	/**
	 * eventcreated in Event table
	 * 
	 * @param userId
	 * @param event
	 */
	@Transactional
	public Event addEvent(long userId, Event event) {
		Optional<User> userByIdOne = findUser(userId);

		User userById = userByIdOne.get();

		event.setUserCreated(userById);
		event = eventRepository.save(event);

		mapEvents = getAllEvents(userId);
		return event;

	}

	/**
	 * Add to User_Event join table for which users joining which events
	 * 
	 * @param userId
	 * @param eventId
	 */

	@Transactional
	public void addJoiningEvent(long userId, long eventId) {

		Optional<User> u = findUser(userId);
		User user = u.get();

		Optional<Event> e = eventRepository.findById(eventId);
		Event event = e.get();

		user.addEventJoining(event);
		event.addUser(user);  //ManytoMany
		
		userRepository.save(user);

		//mapEvents = getAllEvents(userId);

		//return mapEvents;
	}

	@Transactional
	public Map deleteJoiningEvent(long userId, long eventId) {

		Optional<User> u = findUser(userId);
		User user = u.get();

		
		Optional<Event> e = eventRepository.findById(eventId);
		Event event = e.get();

		// Try Load using join fetch to avoid lazy fetch exception error if @Transactional
		// does not work

		/*
		 * Query query = em.
		 * createQuery("select u from User u left join fetch u.events WHERE u.id = ?1 "
		 * ); query.setParameter(1, user.getId() ); user.getEvents() ;
		 */

		user.getEvents().remove(event);

		event.removeUser(user);

		userRepository.save(user);

		mapEvents = getAllEvents(userId);

		return mapEvents;

	}

	@Transactional
	public Map getAllEvents(long userId) {

		System.out.println("User : " + userId);

		HashMap map = new HashMap();

		Optional<User> u = findUser(userId);

		User user = u.get();

		String userState = user.getState();

		System.out.println("state: " + userState);

		//

		List<Event> eventsCreatedInMyState = eventRepository.findByUserCreatedAndStateIgnoreCase(user, userState);

		System.out.println(" " + eventsCreatedInMyState.size());
		for (Object result : eventsCreatedInMyState) {
			Event event = (Event) result;
			System.out.println(event.getEventName());
		}

		map.put("createdInState", eventsCreatedInMyState);

		// step 2 - Now, fetch events from join table, by userid and mystate : UI status
		// "Joining"

		Query query = em.createQuery("Select e  from Event e JOIN e.users u WHERE u.id = ?1 AND e.state = ?2");
		query.setParameter(1, userId);
		query.setParameter(2, userState);

		List resultList = query.getResultList();

		map.put("joiningInState", resultList);

		// step3: Fetch from events table in My state AND NOT IN USER_EVENT table Not
		// joined yet AND other created : UI link Join

		Query toJoinQuery = em.createQuery(" Select e  from Event e join e.userCreated u where u.id <> ?1 "
				+ " AND e.state = ?2 " + " AND NOT EXISTS ( SELECT u from e.users u WHERE u.id = ?3   )  ");
		toJoinQuery.setParameter(1, userId);
		toJoinQuery.setParameter(2, userState);
		toJoinQuery.setParameter(3, userId);

		List toJoinResultList = toJoinQuery.getResultList();

		map.put("toJoinInState", toJoinResultList);

		// Other states fetch data

		List<Event> eventsCreatedNotInMyState = eventRepository.findByUserCreatedAndStateNotIgnoreCase(user,
				user.getState());

		map.put("createdOtherStates", eventsCreatedNotInMyState);

		//

		Query joiningOtherStatesQuery = em
				.createQuery("Select e  from Event e JOIN e.users u WHERE u.id = ?1 AND e.state <> ?2");
		joiningOtherStatesQuery.setParameter(1, userId);
		joiningOtherStatesQuery.setParameter(2, userState);

		List joiningOtherStateResultList = joiningOtherStatesQuery.getResultList();

		map.put("joiningOtherStates", joiningOtherStateResultList);

		//

		Query toJoinOtherStatesQuery = em.createQuery(" Select e  from Event e join e.userCreated u where u.id <> ?1 "
				+ " AND e.state <> ?2 " + " AND NOT EXISTS ( SELECT u from e.users u WHERE u.id = ?3   )  ");
		toJoinOtherStatesQuery.setParameter(1, userId);
		toJoinOtherStatesQuery.setParameter(2, userState);
		toJoinOtherStatesQuery.setParameter(3, userId);

		List toJoinOtherStatesResultList = toJoinOtherStatesQuery.getResultList();

		map.put("toJoinOtherStates", toJoinOtherStatesResultList);
		
		map.put("currentUser", user.getFirstName());

		return map;

	}

	@Transactional
	public Map editEvent(long eventId, String name, String date, String location, String state, long userId) throws Exception {

		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		java.util.Date eventdate = format.parse(date);

		Event event = eventRepository.findById(eventId).get();
		System.out.println(event.getEventLocation());

		event.setEventName(name);
		event.setEventDate(eventdate);
		event.setEventLocation(location);
		event.setState(state);

		eventRepository.save(event);

		mapEvents = getAllEvents(userId);

		return mapEvents;
	}

	@Transactional
	public void deleteEvent(long eventId, long userId) {

		// Use native query to delete in bulk instead of single queries as not a good
		// idea to use Cascade type DELETE

		// remove all associations
		Query q = em.createNativeQuery("DELETE FROM USER_EVENT  WHERE events_id = ? ");
		q.setParameter(1, eventId);
		q.executeUpdate();

		/*
		   Optional<Event> e = eventRepository.findById(eventId); 
		   Event event = e.get();
		   
		   for( User user : event.getUsers() ) { 
		   		user.getEvents().remove(event); 
		   }
		 */

		eventRepository.deleteById(eventId);

		mapEvents = getAllEvents(userId);

	}

	@Transactional
	public Map checkEventCreatedByLoggedInUser(long userId, String eventId) {

		Map map = new HashMap();
		
		System.out.println("Event ID : " + eventId);

		Event event = findEventById(Long.parseLong(eventId));

		// This is to avoid data base work or business logic in controller
		if (userId == event.getUserCreated().getId()) {

			map.put("eventCreatedLoggedUser", true);
			map.put("eventId", eventId);
			map.put("eventName", event.getEventName());
			map.put("date", event.getEventDate());
			map.put("location", event.getEventLocation());
			map.put("state", event.getState());

		}

		return map;
	}

	public Map getEventWallMap() {
		return eventWallMap;
	}

	@Transactional

	public Message addMessage(long userId, long eventId, String message) {

		Timestamp t = new Timestamp(System.currentTimeMillis());

		Message msg = new Message(t, message);

		Optional<User> user = findUser(userId);

		User userById = user.get();

		Optional<Event> event = eventRepository.findById(eventId);

		Event e = event.get();

		msg.setEvent(e); // Store the eventID in table

		msg.setUserSent(userById);

		msg = messageRepository.save(msg);

		eventWallMap = getEventWall(userId, eventId);

		return msg;

	}

	@Transactional
	public List<Message> getEventMessages(long userId, long eventId) {

		// Select e from Event e join e.userCreated u where u.id <> ?1

		String queryString = " SELECT   m " + "FROM  Message m " + "JOIN   m.userSent   u " + "JOIN   m.eventMessage e "
				+ "WHERE  " + "e.id = ?1  AND " + " TIMESTAMPDIFF(SECOND, m.message_time , " + "'"
				+ new Timestamp(System.currentTimeMillis()) + "' ) <= 300 " + "  order by  m.message_time ";

		System.out.println(queryString);

		Query query = em.createQuery(queryString);

		query.setParameter(1, eventId);

		List resultList = query.getResultList();

		/*for (Object o : resultList) {
			Message m = (Message) o;
			System.out.println(m.getMessage() + " " + m.getUserSent().getFirstName());
		}*/

		return resultList;

	}

	@Transactional

	public Map getUsersJoiningEvent(long userId, long eventId) {

		Map dataMap = new HashMap();

		// Get users

		String queryString = " Select u  from Event e JOIN e.users u WHERE e.id = ?1 ";
		Query query = em.createQuery(queryString);
		query.setParameter(1, eventId);

		List resultList = query.getResultList();

		Optional<Event> obj = eventRepository.findById(eventId);

		Event event = obj.get();

		// Get count

		queryString = " Select count(*)  from Event e JOIN e.users u WHERE e.id = ?1   ";

		Query countQuery = em.createQuery(queryString);
		countQuery.setParameter(1, eventId);

		long count = (Long) countQuery.getSingleResult();

		// Put data in map so controller does not do seperate session for fetching data

		dataMap.put("joiningUsers", resultList);
		dataMap.put("countJoiners", count);
		dataMap.put("eventData", event);

		return dataMap;

	}

	@Transactional
	public Map getEventWall(long userId, long eventId) {

		Map data = getUsersJoiningEvent(userId, eventId);

		data.put("wallmessages", getEventMessages(userId, eventId));

		return data;
	}

}
