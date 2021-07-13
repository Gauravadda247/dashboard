package com.adda.users.ui.controllers;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

import com.adda.users.data.UserEntity;
import com.adda.users.service.*;
import com.adda.users.shared.*;
import com.adda.users.ui.model.CreateUserRequestModel;
import com.adda.users.ui.model.CreateUserResponseModel;
import com.adda.users.ui.model.UserResponseModel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.cache.annotation.EnableCaching;

@RestController
@RequestMapping("/users")
@EnableCaching
public class UsersController {

	@Autowired
	private Environment env;

	@Autowired
	UsersService usersService;
	

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	private String topic = "createUser";


	@GetMapping
	public String status() {
		return "Working on port " + env.getProperty("local.server.port") + ", with token = "
				+ env.getProperty("token.secret");
	}

	@GetMapping(value = "/{userId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@Cacheable(cacheNames = "userResponseModel", key = "#userId")
	public UserResponseModel getUser(@PathVariable("userId") String userId) {

		UserDto userDto = usersService.getUserByUserId(userId);
		UserResponseModel returnValue = new ModelMapper().map(userDto, UserResponseModel.class);

		return returnValue;
	}

	@Scheduled(fixedRate = 60 * 60 * 1000)
	@GetMapping(value = "/leaderBoard", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<List<UserResponseModel>> leaderBoard() {

		List<UserDto> data = usersService.getAllUser();
		List<UserResponseModel> list = new ArrayList<UserResponseModel>();

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		for (UserDto userDto : data) {
			UserResponseModel userResponseModel = modelMapper.map(userDto, UserResponseModel.class);
			list.add(userResponseModel);
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
//	@PostMapping(
//    	    consumes={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}, 
//    	    produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE} )
//	public ResponseEntity<CreateUserResponseModel> createUser(@RequestBody CreateUserRequestModel userDetails)
//	{
//		ModelMapper modelMapper = new ModelMapper(); 
//		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//		
//		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
//		
//		UserDto createdUser = usersService.createUser(userDto);
//		
//		CreateUserResponseModel returnValue = modelMapper.map(createdUser, CreateUserResponseModel.class);
//		
//		return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
//	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<CreateUserRequestModel> createUser(@RequestBody CreateUserRequestModel userDetails) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
//	
		kafkaTemplate.send(topic, userDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(userDetails);

	}

	@PutMapping(value = "/{userId}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	@CachePut(key = "#userId", value = "userResponseModel")
	public UserResponseModel updateUser(@PathVariable String userId, @RequestBody UserDto userDetails) {

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		userDetails.setUserId(userId);

		UserDto createdUser = usersService.updateUser(userDetails);

		UserResponseModel returnValue = modelMapper.map(createdUser, UserResponseModel.class);

		return returnValue;

	}

	@DeleteMapping(value = "/{userId}")
	@CacheEvict(key = "#userId", value = "userResponseModel")
	public void deleteById(@PathVariable("userId") String userId) {
		usersService.deleteById(userId);
	}

}
