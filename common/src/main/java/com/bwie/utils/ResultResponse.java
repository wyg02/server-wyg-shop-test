package com.bwie.utils;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 冰烆
 * @version 1.0
 * @description: 统一返回结构封装
 * @date 2022/10/28 19:13
 */
@Data
public class ResultResponse {

    private ResultResponse(){}


    private Boolean success;


    private Integer code;


    private String message;


    private Map<String, Object> data = new HashMap<String, Object>(16);

    /**
     * @description: 成功
     * @return: com.bwie.utils.ResultResponse
     * @author: 冰烆
     * @date: 2022/10/21 20:18
     */
    public static ResultResponse SUCCESS() {
        ResultResponse r = new ResultResponse();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }

    /**
     * @description: 失败
     * @return: com.bwie.utils.ResultResponse
     * @author: 冰烆
     * @date: 2022/10/21 20:19
     */
    public static ResultResponse FAILED() {
        ResultResponse r = new ResultResponse();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }

    /**
     * @description: 之所以返回this（R对象），是因为想使用链式编程
     * @param: success
     * @return: com.bwie.utils.ResultResponse
     * @author: 冰烆
     * @date: 2022/10/21 20:18
     */
    public ResultResponse success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public ResultResponse message(String message) {
        this.setMessage(message);
        return this;
    }

    public ResultResponse code(Integer code) {
        this.setCode(code);
        return this;
    }

    public ResultResponse data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public ResultResponse data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }
}
