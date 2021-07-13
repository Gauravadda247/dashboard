package com.adda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.adda.users.data.UserEntity;
import com.adda.users.ui.model.UserResponseModel;


@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableCaching
public class DashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(DashboardApplication.class, args);
		System.out.println("running");
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
	   RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("localhost", 6379);
	   return new JedisConnectionFactory(redisStandaloneConfiguration);
	}
	
	@Bean
	RedisTemplate<String, UserResponseModel> redisTemplate() {
	    RedisTemplate<String, UserResponseModel> redisTemplate = new RedisTemplate<>();
	    redisTemplate.setConnectionFactory(jedisConnectionFactory());
	    return redisTemplate;
	 }
	
//	@Bean
//	public RedisCacheManager redisCacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
//	    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//	            .disableCachingNullValues()
//	            .entryTtl(Duration.ofHours(1))
//	            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
//	    redisCacheConfiguration.usePrefix();
//
//	   return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(lettuceConnectionFactory)
//	                    .cacheDefaults(redisCacheConfiguration).build();
//
//	}    
	
	
}
