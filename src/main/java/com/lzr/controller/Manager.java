package com.lzr.controller;

import com.alibaba.fastjson.JSONObject;
import com.lzr.Entity.Point;
import com.lzr.Entity.Task;
import com.lzr.mapper.PointMapper;
import com.lzr.mapper.TaskMapper;
import com.lzr.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import static com.lzr.constant.StatusConstant.INVALID_USER_LOGIN;

/**
 * @author lzr
 * @date 2021.08.11
 * @version 1.0
 * 管理员
 */
@RequestMapping("/wx")
@RestController
public class Manager {
    /**
     * 微信小程序appid
     */
    private static final String appid="wx6b7db53e1aab53f7";
    /**
     * 微信小程序标识符
     */
    private static final String str="9250a9c1fb6ccea5a3858e06c41be4d0";

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    PointMapper pointMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserMapper userMapper;
    /**
     * 用于保存任务
     * @date 2021.08.12
     * @param task 任务详情
     * @return 成功
     */
    @PostMapping("/releaseTask")
    public String releaseTask(@RequestBody Task task){//@Todo 文件校验（考虑是前端还是后端）
        try{
            //存入数据库
            taskMapper.addTask(task);
        }catch (Exception e){
            //@Todo 日志记录
            e.printStackTrace();
            //@Todo 抛一个运行异常
        }
        return "success";
    }

    /**
     * @date 2021.08.12
     *  保存采集的打卡点
     * @param point 打卡点信息
     * @param id uuid,保存数据库的Key
     * @return 成功
     */
    @PostMapping("/addPoint")
    public String addCollectCardPoints(@RequestBody Point point, @RequestParam("id")String id){
        try {
            //获取采集人的信息
            String openid= (String) redisTemplate.opsForValue().get(id);
            //如果openid不存在说明当前用户未登录
            if (openid.isEmpty()){
                return INVALID_USER_LOGIN;
            }
            // @Todo 权限判断
            userMapper.getUser(openid).
            pointMapper.addPoint(point);
        } catch (Exception e) {
            //@Todo 抛异常
            e.printStackTrace();
        }
        return "success";
    }
}