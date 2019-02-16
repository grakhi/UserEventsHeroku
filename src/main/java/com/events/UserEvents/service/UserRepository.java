package com.events.UserEvents.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.events.UserEvents.entity.Event;
import com.events.UserEvents.entity.User;


@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByEmail(String email);
	
	
}
