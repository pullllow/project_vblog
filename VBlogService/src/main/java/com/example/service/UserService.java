package com.example.service;

import com.example.common.lang.RestResponse;
import com.example.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ChangQi
 * @since 2022-08-18
 */
public interface UserService extends IService<User> {

    RestResponse register(User user);

}
