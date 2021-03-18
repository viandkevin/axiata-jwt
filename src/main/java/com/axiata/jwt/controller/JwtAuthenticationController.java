package com.axiata.jwt.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.axiata.jwt.config.JwtTokenUtil;
import com.axiata.jwt.dao.UserDao;
import com.axiata.jwt.model.DAOUser;
import com.axiata.jwt.model.JwtRequest;
import com.axiata.jwt.model.JwtResponse;
import com.axiata.jwt.model.UserDTO;
import com.axiata.jwt.service.JwtUserDetailsService;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token));
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
		
		return ResponseEntity.ok(userDetailsService.save(user));
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ResponseEntity<?> changePassword(@RequestParam String id, @RequestBody UserDTO user) throws Exception {
		String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
		DAOUser saveUser = userDao.findByUsername(id);
		saveUser.setUsername(id);
		saveUser.setPassword(encodedPassword);
		return ResponseEntity.ok(userDao.save(saveUser));
	}
	
	@RequestMapping(value = "/findByUsername", method = RequestMethod.GET)
	public ResponseEntity<?> findByEmail(@RequestParam String username) throws Exception {
		return ResponseEntity.ok(userDao.findByUsername(username));
	}
	
	@RequestMapping(value = "/findByEmployeeNumber", method = RequestMethod.GET)
	public ResponseEntity<?> findByEmployeeNumber(@RequestParam String employeeNumber) throws Exception {
		return ResponseEntity.ok(userDao.findByEmployeeNumber(Integer.valueOf(employeeNumber)));
	}
	
	@RequestMapping(value = "/pageUser", method = RequestMethod.GET)
	public ResponseEntity<?> findByPage(@RequestParam Integer size, @RequestParam String sortingBy) throws Exception {
		int pageSize = 5;
		
		Pageable pageable = PageRequest.of(pageSize-1, Integer.valueOf(size), Sort.by(sortingBy));
		
		return ResponseEntity.ok(userDao.findAll(pageable));
	}
	
	@RequestMapping(value = "/editProfile", method = RequestMethod.PUT)
	public ResponseEntity<?> editProfile(@RequestParam String id, @RequestBody UserDTO userDTO) throws Exception {
		DAOUser daoUser = userDao.findByUsername(id);
		daoUser.setEmployeeNumber(Integer.valueOf(userDTO.getEmployeeNumber()));
		daoUser.setBirthPlace(userDTO.getBirthPlace());
	    Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(userDTO.getBirthDate());  
		daoUser.setBirthDate(date1);
		return ResponseEntity.ok(userDao.save(daoUser));
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}