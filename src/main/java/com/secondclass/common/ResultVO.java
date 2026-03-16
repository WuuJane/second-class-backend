package com.secondclass.common;

import lombok.Data;

/**
 * 全局统一返回结果类
 */
@Data
public class ResultVO<T> {
    private Integer code; // 状态码：200代表成功，500代表失败
    private String msg;   // 提示信息（例如"报名成功"、"密码错误"）
    private T data;       // 真正要返回给前端的数据

    // 成功（不带数据）
    public static <T> ResultVO<T> success() {
        return success(null);
    }

    // 成功（带数据）
    public static <T> ResultVO<T> success(T data) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 失败
    public static <T> ResultVO<T> error(String msg) {
        ResultVO<T> result = new ResultVO<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}