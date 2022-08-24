package com.example.service.impl;

import com.example.entity.UserMessage;
import com.example.mapper.UserMessageMapper;
import com.example.service.UserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ChangQi
 * @since 2022-08-18
 */
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

}
