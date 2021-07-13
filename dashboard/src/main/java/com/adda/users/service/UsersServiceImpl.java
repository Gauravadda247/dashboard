package com.adda.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;


import org.springframework.kafka.annotation.KafkaListener;
import com.adda.users.data.*;
import com.adda.users.shared.UserDto;


@Service
@Transactional
public class UsersServiceImpl implements UsersService {
	
	UsersRepository usersRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	Environment environment;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public UsersServiceImpl(UsersRepository usersRepository, 
			BCryptPasswordEncoder bCryptPasswordEncoder,
			Environment environment)
	{
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.environment = environment;
	}
 
//	@Override
//	public UserDto createUser(UserDto userDetails) {
//		
//		userDetails.setUserId(UUID.randomUUID().toString());
//		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
//		
//     	System.out.println(userDetails.getPassword());
//     	System.out.println(userDetails.getEncryptedPassword());		
//		
//		ModelMapper modelMapper = new ModelMapper(); 
//		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//		
//		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
//
//		usersRepository.save(userEntity);
//		
//		UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
// 
//		return returnValue;
//	}
	
	
	@KafkaListener(groupId = "createUser", topics = "createUser", containerFactory = "kafkaListenerContainerFactory")
	public UserDto createUser(UserDto userDetails) {

		System.out.println(userDetails.getEmail());
	//	
//		userDetails.setUserId(UUID.randomUUID().toString());
//		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
	//	
//	 	System.out.println(userDetails.getPassword());
//	 	System.out.println(userDetails.getEncryptedPassword());		
	//	
//		ModelMapper modelMapper = new ModelMapper(); 
//		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	//	
//		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
	//
//		usersRepository.save(userEntity);
	//	
//		UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
	//
		return userDetails;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = usersRepository.findByEmail(username);
		
		if(userEntity == null) throw new UsernameNotFoundException(username);	
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) { 
		UserEntity userEntity = usersRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		
		return new ModelMapper().map(userEntity, UserDto.class);
	}

	@Override
	public UserDto getUserByUserId(String userId) {

		
        UserEntity userEntity = usersRepository.findByUserId(userId);     
        if(userEntity == null) throw new UsernameNotFoundException("User not found");
        
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        
		return userDto;
	}
	
	
	@Override
	public  List<UserDto> getAllUser() {
		 var it = usersRepository.findAll();
		 List<UserDto> list = new ArrayList<UserDto>();
		 ModelMapper modelMapper = new ModelMapper(); 
		 modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
			
		 for(UserEntity user:it)
	        {
			   UserDto userDto = modelMapper.map(user, UserDto.class);
			   list.add(userDto);
	        }
		 
	     return list;
	}
	
	@Override
	public UserDto updateUser(UserDto userDetails) {
		
		ModelMapper modelMapper = new ModelMapper(); 
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
		
		UserEntity dataEntity=usersRepository.findByUserId(userDetails.getUserId());
		userEntity.setId(dataEntity.getId());		
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		usersRepository.save(userEntity);	
		UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
		
		return userDetails;
	}
	
	@Override
	public void deleteById(String userId) {
	    
		Long id=usersRepository.deleteByUserId(userId);
	    
	}
	
	

	

}
