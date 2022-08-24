package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.vo.PostVo;
import com.example.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ChangQi
 * @since 2022-08-18
 */
public interface PostService extends IService<Post> {

    PostVo selectOnePost(QueryWrapper<Post> eq);
}
