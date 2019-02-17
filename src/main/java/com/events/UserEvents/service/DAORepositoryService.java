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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.events.UserEvents.entity.Event;
import com.events.UserEvents.entity.Message;
import com.events.UserEvents.entity.User;

@Repository

public class DAORepositoryService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	EntityManager em;

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

	/**
	 * @param userId
	 * @param event
	 */
	@Transactional
	public Event addEvent(long userId, Event event) {
		Optional<User> userByIdOne = findUser(userId);

		User userById = userByIdOne.get();

		event.setUserCreated(userById);
		event = eventRepository.save(event);

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
		event.addUser(user); // ManytoMany

		userRepository.save(user);

	}

	@Transactional
	public void deleteJoiningEvent(long userId, long eventId) {

		Optional<User> u = findUser(userId);
		User user = u.get();

		Optional<Event> e = eventRepository.findById(eventId);
		Event event = e.get();

		user.getEvents().remove(event);

		event.removeUser(user);

		userRepository.save(user);

	}

	@Transactional
	public Map getAllEvents(long userId) {

		

		HashMap map = new HashMap();

		Optional<User> u = findUser(userId);

		User user = u.get();

		String userState = user.getState();

		List<Event> eventsCreatedInMyState = eventRepository.findByUserCreatedAndStateIgnoreCase(user, userState);

		map.put("createdInState", eventsCreatedInMyState);

		Query query = em.createQuery("Select e  from Event e JOIN e.users u WHERE u.id = ?1 AND e.state = ?2");
		query.setParameter(1, userId);
		query.setParameter(2, userState);

		List resultList = query.getResultList();

		map.put("joiningInState", resultList);

		Query toJoinQuery = em.createQuery(" Select e  from Event e join e.userCreated u where u.id <> ?1 "
				+ " AND e.state = ?2 " + " AND NOT EXISTS ( SELECT u from e.users u WHERE u.id = ?3   )  ");
		toJoinQuery.setParameter(1, userId);
		toJoinQuery.setParameter(2, userState);
		toJoinQuery.setParameter(3, userId);

		List toJoinResultList = toJoinQuery.getResultList();

		map.put("toJoinInState", toJoinResultList);

		List<Event> eventsCreatedNotInMyState = eventRepository.findByUserCreatedAndStateNotIgnoreCase(user,
				user.getState());

		map.put("createdOtherStates", eventsCreatedNotInMyState);

		

		Query joiningOtherStatesQuery = em
				.createQuery("Select e  from Event e JOIN e.users u WHERE u.id = ?1 AND e.state <> ?2");
		joiningOtherStatesQuery.setParameter(1, userId);
		joiningOtherStatesQuery.setParameter(2, userState);

		List joiningOtherStateResultList = joiningOtherStatesQuery.getResultList();

		map.put("joiningOtherStates", joiningOtherStateResultList);

		

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
	public void editEvent(long eventId, String name, String date, String location, String state, long userId)
			throws Exception {

		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		java.util.Date eventdate = format.parse(date);

		Event event = eventRepository.findById(eventId).get();
		

		event.setEventName(name);
		event.setEventDate(eventdate);
		event.setEventLocation(location);
		event.setState(state);

		eventRepository.save(event);

	}

	@Transactional
	public void deleteEvent(long eventId, long userId) {

		// Use native query to delete in bulk instead of single queries as not a good
		// idea to use Cascade type DELETE

		// remove all associations
		Query q = em.createNativeQuery("DELETE FROM USER_EVENT  WHERE events_id = ? ");
		q.setParameter(1, eventId);
		q.executeUpdate();
		
		//Delete messages
		q = em.createNativeQuery("DELETE FROM MESSAge  WHERE event_message_id = ? ");
		q.setParameter(1, eventId);
		q.executeUpdate();

		eventRepository.deleteById(eventId);

	}

	@Transactional
	public Map checkEventCreatedByLoggedInUser(long userId, String eventId) {

		Map map = new HashMap();

		

		Event event = findEventById(Long.parseLong(eventId));

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

		msg.setEvent(e);

		msg.setUserSent(userById);

		msg = messageRepository.save(msg);

		eventWallMap = getEventWall(userId, eventId);

		return msg;

	}

	@Transactional
	public List<Message> getEventMessages(long userId, long eventId) {

	

		String queryString = " SELECT   m " + "FROM  Message m " + "JOIN   m.userSent   u " + "JOIN   m.eventMessage e "
				+ "WHERE  " + "e.id = ?1  AND " + " TIMESTAMPDIFF(SECOND, m.message_time , " + "'"
				+ new Timestamp(System.currentTimeMillis()) + "' ) <= 300 " + "  order by  m.message_time ";

		

		Query query = em.createQuery(queryString);

		query.setParameter(1, eventId);

		List resultList = query.getResultList();

		return resultList;

	}

	@Transactional

	public Map getUsersJoiningEvent(long userId, long eventId) {

		Map dataMap = new HashMap();

		
		Optional<Event> obj = eventRepository.findById(eventId);

		Event event = obj.get();
		
		List<User> resultList = event.getUsers();

		// Get count

		
		
		long count = resultList.size();

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
