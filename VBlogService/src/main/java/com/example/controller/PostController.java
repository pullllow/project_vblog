package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.lang.RestResponse;
import com.example.common.vo.PostVo;
import com.example.entity.Post;
import com.example.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ChangQi
 * @since 2022-08-18
 */
@RestController
public class PostController {


    @Autowired
    PostService postService;

    @GetMapping("/category/{id:\\d*}")
    public RestResponse category(Integer pageSize, @PathVariable("id") Long id, HttpServletRequest request) {
        return RestResponse.success(null);
    }

    @PostMapping("/post/{id:\\d*}")
    public RestResponse getPost(@PathVariable("id") Long id) {
        PostVo vo = null;

        try {
            vo = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id", id));
            Assert.notNull(vo, "文章已被删除");
            //postService.putViewCount(vo);

        } catch (Exception e) {

        }

        return RestResponse.success(vo);
    }

}
