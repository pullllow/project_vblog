package com.example.common.vo;
/*
 *  @author changqi
 *  @date 2022/8/22 22:19
 *  @description
 *  @Version V1.0
 */

import com.example.entity.Post;
import lombok.Data;

@Data
public class PostVo extends Post {

    private Long authorId;
    private String authorName;
    private String authorAvatar;

    private String categoryName;
}
