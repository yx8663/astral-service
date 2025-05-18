package com.astral.common.result;

import com.astral.common.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -6098323041897792633L;

    private Integer code;
    private boolean success;
    private String message;
    private T result;

    public Result() {
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static<T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.SC_OK_200);
        result.setSuccess(true);
        return result;
    }

    public static<T> Result<T> success(String message) {
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.SC_OK_200);
        result.setSuccess(true);
        result.setMessage(message);
        return result;
    }

    public static<T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.SC_OK_200);
        result.setSuccess(true);
        result.setResult(data);
        return result;
    }

    public static<T> Result<T> success(Boolean flag) {
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.SC_OK_200);
        result.setSuccess(flag);
        return result;
    }

    public static<T> Result<T> success(String message, T data) {
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.SC_OK_200);
        result.setSuccess(true);
        result.setMessage(message);
        result.setResult(data);
        return result;
    }

    public static<T> Result<T> error(String message) {
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.SC_ERROR_500);
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static<T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static<T> Result<T> error(String message, T data) {
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.SC_ERROR_500);
        result.setSuccess(false);
        result.setMessage(message);
        result.setResult(data);
        return result;
    }

    public static<T> Result<T> error401(String message) {
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.SC_ERROR_401);
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static<T> Result<T> error401(String message, T data) {
        Result<T> result = new Result<T>();
        result.setCode(CommonConstant.SC_ERROR_401);
        result.setSuccess(false);
        result.setMessage(message);
        result.setResult(data);
        return result;
    }

    /**
     * 无权限访问返回结果
     */
    public static<T> Result<T> noauth(String msg) {
        return error(CommonConstant.SC_JEECG_NO_AUTHZ, msg);
    }

    public static<T> Result<T> toAjax(Boolean flag) {
        Result<T> result = new Result<T>();
        if (flag) {
            result.setCode(CommonConstant.SC_OK_200);
            result.setSuccess(true);
        } else {
            result.setCode(CommonConstant.SC_ERROR_500);
            result.setSuccess(false);
        }
        return result;
    }

    public static<T> Result<T> toAjax(int row) {
        Result<T> result = new Result<T>();
        if (row > 0) {
            result.setCode(CommonConstant.SC_OK_200);
            result.setSuccess(true);
        } else {
            result.setCode(CommonConstant.SC_ERROR_500);
            result.setSuccess(false);
        }
        return result;
    }

}
