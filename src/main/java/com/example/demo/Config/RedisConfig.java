package com.example.demo.Config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lenovo
 * @version 1.0
 * @Date 2021/7/15 10:06
 * @Description
 */
@Configuration
@EnableCaching //开启缓存支持
public class RedisConfig {

    @Resource
    private LettuceConnectionFactory lettuceConnectionFactory;

    /**
     * 自定义缓存key的生成策略,默认的生成策略是看不懂(乱码内容) 通过Spring 的依赖注入特性进行了自定义的配置注入并且此类是一个配置类
     * 根据类名+所有参数的值生成的唯一的一个key
     * @return
     */
    @Bean
    public KeyGenerator keyGenerator()
    {
        return new KeyGenerator(){

            @Override
            public Object generate(Object target, Method method, Object... objects) {
                StringBuffer sb=new StringBuffer();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for(Object object:objects)
                {
                    sb.append(object.toString());
                }
                return sb.toString();
            }
        };
    }


    //缓存管理器
    @Bean
    public CacheManager cacheManager()
    {
        RedisCacheManager.RedisCacheManagerBuilder builder=
                RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(lettuceConnectionFactory);
        @SuppressWarnings("serial")
        Set<String> cacheNames=new HashSet<String>(){

            {
                add("codeNameCache");
            }
        };
        builder.initialCacheNames(cacheNames);
        return builder.build();
    }


    /**
     * 配置Jackson2JsonRedisSerializer 序列化器,在配置 redisTemplate需要用来做k,v的序列化器
     *
     * @param lettuceConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory)
    {
        //设置序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer=new Jackson2JsonRedisSerializer<Object>(Object.class);

        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        //配置redisTemplate
        RedisTemplate<String,Object> redisTemplate=new RedisTemplate<String,Object>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        RedisSerializer<?> stringredisSerializer=new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringredisSerializer);//key序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);//value的序列化
        redisTemplate.setHashKeySerializer(stringredisSerializer); //Hash key序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);//Hash value序列化
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
