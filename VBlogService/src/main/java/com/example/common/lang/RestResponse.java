package com.example.common.lang;
/*
 *  @author changqi
 *  @date 2022/8/17 22:57
 *  @description
 *  @Version V1.0
 */

import lombok.Data;

import java.io.Serializable;

@Data
public class RestResponse implements Serializable {

    private String code;
    private String mess;
    private Object data;

    public static RestResponse success(Object data) {
        RestResponse response = new RestResponse();
        response.setCode("200");
        response.setMess("success");
        response.setData(data);
        return response;
    }

    public static RestResponse success(String mess, Object data) {
        RestResponse response = new RestResponse();
        response.setCode("200");
        response.setMess(mess);
        response.setData(data);
        return response;
    }

    public static RestResponse fail(String mess) {
        RestResponse response = new RestResponse();
        response.setCode("400");
        response.setMess(mess);
        return response;
    }

    public static RestResponse fail(String mess, Object data) {
        RestResponse response = new RestResponse();
        response.setCode("400");
        response.setMess(mess);
        response.setData(data);
        return response;
    }

}
