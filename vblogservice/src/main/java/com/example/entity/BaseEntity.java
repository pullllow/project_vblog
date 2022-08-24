package com.example.entity;
/*
 *  @author changqi
 *  @date 2022/8/17 22:17
 *  @description
 *  @Version V1.0
 */


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {

    @TableId(type = IdType.AUTO, value = "id")
    private Long id;
    private Date created;
    private Date modified;
}
