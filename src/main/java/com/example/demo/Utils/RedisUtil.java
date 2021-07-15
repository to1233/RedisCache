package com.example.demo.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author lenovo
 * @version 1.0
 * @Date 2021/7/15 19:36
 * @Description
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String,Object> redisTemplatel;

    public void setRedisTemplate(RedisTemplate<String,Object> redisTemplate)
    {
        this.redisTemplatel=redisTemplate;
    }

    /**
     * 指定缓存失效时间
     */

    public boolean expire(String key,long time)
    {
        try
        {
            if(time>0)
            {
                redisTemplatel.expire(key,time, TimeUnit.SECONDS);
            }
            return true;
        }catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public boolean  hasKey(String key)
    {
        try{
            return redisTemplatel.hasKey(key);
        }catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key  可以传入一个值或者多个
     */
    @SuppressWarnings("unchecked")
    public void del(String...key)
    {

        if(key!=null&&key.length>0)
        {
            if(key.length==1)
            {
                redisTemplatel.delete(key[0]);
            }else{
                redisTemplatel.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }
    //String

    /**
     *
     * @param key key的值
     * @return 值
     */
    public Object get(String key)
    {
        return key==null? null:redisTemplatel.opsForValue().get(key);
    }

    /**
     * 普通的缓存放入
     * @param key  键
     * @param value  值
     * @return  true表示成功 false失败
     */
    public boolean set(String key,Object value)
    {
        try
        {
            redisTemplatel.opsForValue().set(key,value);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     *  递增的实现
     * @param key
     * @param delta
     * @return
     */
    public long incr(String key,long delta)
    {
        if(delta<0)
        {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplatel.opsForValue().increment(key,delta);
    }

    /**
     * 递减的实现
     * @param key
     * @param delta
     * @return
     */
    public long decr(String key,long delta)
    {
        if(delta<0)
        {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplatel.opsForValue().decrement(key,-delta);
    }

    /**
     *  HashSet
     * @param key
     * @param item
     * @return
     */
    public Object hget(String key,String item)
    {
        return  redisTemplatel.opsForHash().get(key,item);
    }

    /**
     * 获取hashkey对应的所有键值  key +map对象value
     * @param key
     * @return
     */
    public Map<Object,Object> hmget(String key){
        return redisTemplatel.opsForHash().entries(key);
    }

    /**
     * HashSet
     * @param key
     * @param map
     * @return
     */
    public boolean hmset(String key,Map<String,Object> map)
    {
        try{
            redisTemplatel.opsForHash().putAll(key,map);
            return true;
        }catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet并设置时间
     * @param key
     * @param map
     * @param time
     * @return
     */
    public boolean hmset(String key,Map<String,Object> map,long time)
    {
        try{
            redisTemplatel.opsForHash().putAll(key,map);
            if(time>0)
            {
                expire(key,time);
            }
            return true;
        }catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在就创建  map中是否有这个键值对,有的话就更新,没有就创建
     * @param key
     * @param item
     * @param value
     * @return
     */

    public boolean hset(String key,String item,Object value)
    {

        try{
            redisTemplatel.opsForHash().put(key,item,value);
            return true;
        }catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向Map 添加键值对如果不存在就创建并设置过期时间
     * @param key
     * @param item
     * @param value
     * @param time 如果已经存在hash表的事件,则这里将会替换原有的时间
     * @return
     */
    public boolean hset(String key,String item,Object value,long time)
    {
        try
        {
            redisTemplatel.opsForHash().put(key,item,value);
            if(time>0)
            {
                expire(key,time);
            }
            return true;
        }catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key
     * @param item
     */
    public void hdel(String key,Object ...item)
    {
         redisTemplatel.opsForHash().delete(key,item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key
     * @param item
     * @return
     */
    public boolean hHashKey(String key,String item)
    {
        return redisTemplatel.opsForHash().hasKey(key,item);
    }

    /**
     * hash递增 如果存在就换创建一个并把新增后的值返回
     * @param key
     * @param item
     * @param by
     * @return
     */
    public double hincr(String key,String item,double by)
    {
        return redisTemplatel.opsForHash().increment(key,item,by);
    }

    /**
     * hash递减
     * @param key
     * @param item
     * @param by
     * @return
     */
    public double hdecr(String key,String item,double by)
    {
        return redisTemplatel.opsForHash().increment(key,item,-by);
    }

    //set

    /**
     * 根据key获取set中所有的值
     * @param key
     * @return
     */
    public Set<Object> sSet(String key)
    {
        try{
            return redisTemplatel.opsForSet().members(key);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key
     * @param value
     * @return
     */
    public boolean sHashKey(String key,Object value)
    {
        try {
            return redisTemplatel.opsForSet().isMember(key,value);
        }catch (Exception e)
        {
            return false;
        }
    }

    /**
     *  将set数据放入缓存中
     * @param key
     * @param values
     * @return
     */

    public long sSet(String key,Object...values)
    {
        try{
            return redisTemplatel.opsForSet().add(key,values);
        }catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * @param key
     * @return
     */
    public long sGetSetSize(String key)
    {
        try
        {
            return redisTemplatel.opsForSet().size(key);
        }catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     * @param key
     * @param values  values的值可以为多个
     * @return
     */
    public long setRemove(String key,Object...values)
    {
        try{
            Long count=redisTemplatel.opsForSet().remove(key,values);
            return count;
        }catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取list缓存的内容
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Object> lGet(String key, long start, long end)
    {
        try{
            return redisTemplatel.opsForList().range(key,start,end);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key
     * @return
     */
    public long lGetListSize(String key)
    {
        try{
            return redisTemplatel.opsForList().size(key);
        }catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key
     * @param index
     * @return
     */
    public Object  lGetIndex(String key,long index)
    {
        try{
            return redisTemplatel.opsForList().index(key,index);
        }catch (Exception e)
        {
              e.printStackTrace();
              return  null;
        }
    }

    /**
     * 将list放入缓存中
     * @param key
     * @param value
     * @return
     */
    public boolean lSet(String key,Object value)
    {
        try
        {
            redisTemplatel.opsForList().rightPush(key,value);
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key
     * @param value
     * @param time
     * @return
     */
    public boolean  lSet(String key,Object value,long time)
    {
        try {
            redisTemplatel.opsForList().rightPush(key,value);
            if(time>0)
                expire(key,time);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 将list放入缓存 并设置过期时间
     * @param key
     * @param value
     * @param time
     * @return
     */
    public boolean lSet(String key,List<Object> value,long time)
    {
        try{
            redisTemplatel.opsForList().rightPushAll(key,value);
            if(time>0)
                expire(key,time);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改List中的某条数据
     * @param key
     * @param index
     * @param value
     * @return
     */
    public boolean lUpdateIndex(String key,long index,Object value){
        try {
            redisTemplatel.opsForList().set(key,index,value);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key
     * @param count
     * @param value
     * @return
     */
    public long LRemove(String key,long count,Object value)
    {
        try{
            Long remove=redisTemplatel.opsForList().remove(key,count,value);
            return remove;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }





}
