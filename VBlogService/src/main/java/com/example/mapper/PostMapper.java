package com.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.common.vo.PostVo;
import com.example.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ChangQi
 * @since 2022-08-18
 */
public interface PostMapper extends BaseMapper<Post> {

    PostVo selectOnePost(@Param(Constants.WRAPPER) QueryWrapper<Post> wrapper);
}
