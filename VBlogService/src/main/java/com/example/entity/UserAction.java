package com.example.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ChangQi
 * @since 2022-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserAction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 动作类型
     */
    private String action;

    /**
     * 得分
     */
    private Integer point;

    /**
     * 关联的帖子ID
     */
    private String postId;

    /**
     * 关联的评论ID
     */
    private String commentId;


}
