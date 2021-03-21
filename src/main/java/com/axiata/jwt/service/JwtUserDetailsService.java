package com.axiata.jwt.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.axiata.jwt.dao.UserDao;
import com.axiata.jwt.model.DAOUser;
import com.axiata.jwt.model.UserDTO;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		DAOUser user = userDao.findByUsername(username);
		if (user.getActive().equals("N")) {
			user = null;
		}
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}
	
	public DAOUser save(UserDTO user) {
		DAOUser newUser = new DAOUser();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date birthDate = null;
		try {
			birthDate = df.parse(user.getBirthDate());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		newUser.setBirthDate(birthDate);
		newUser.setActive("Y");
		String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
		newUser.setUsername(user.getUsername());
		newUser.setPassword(encodedPassword);
		return userDao.save(newUser);
	}
}