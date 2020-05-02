package com.example.redisdemo.config;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import com.example.redisdemo.consumer.CustomerInfoSubscriber;
import com.example.redisdemo.producer.CustomerInfoPublisher;
import com.example.redisdemo.producer.RedisCustomerInfoPublisher;

@Configuration
@ComponentScan("com.example.redis")
public class RedisConfig {

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
    	JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
    	jedisConnectionFactory.setHostName("127.0.0.1");
    	jedisConnectionFactory.setPort(6379);
      return jedisConnectionFactory;
    }
     
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
      final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
      template.setConnectionFactory(jedisConnectionFactory());
      template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
      return template;
    }
     
    @Bean
    MessageListenerAdapter messageListener() {
      return new MessageListenerAdapter(new CustomerInfoSubscriber());
    }
     
    @Bean
    RedisMessageListenerContainer redisContainer() {
      final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
      container.setConnectionFactory(jedisConnectionFactory());
      container.addMessageListener(messageListener(), topic());
      container.setTaskExecutor(Executors.newFixedThreadPool(4));
      return container;
    }	
    
    @Bean
    CustomerInfoPublisher redisPublisher() {
      CustomerInfoPublisher customerInfoPublisher = new RedisCustomerInfoPublisher(redisTemplate(), topic());
      return customerInfoPublisher;
    } 
     
    @Bean
    ChannelTopic topic() {
      return new ChannelTopic("pubsub:jsa-channel");
    }
}
