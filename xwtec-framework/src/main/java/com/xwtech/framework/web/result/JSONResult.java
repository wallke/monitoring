package com.xwtech.framework.web.result;


/**
 * JSONResult : Response JSONResult for RESTful,封装返回JSON格式的数据
 *
 * @author StarZou
 * @since 2014年5月26日 上午10:51:46
 */

public class JSONResult<T> extends Result {

    private static final long serialVersionUID = 7880907731807860636L;

    /**
     * 数据
     */
    private T data;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public JSONResult() {
        super();
    }


    /**
     * 失败返回
     * @param message
     * @param code
     */
    public void setFailInfo(String message,String code){
        super.setSuccess(false);
        super.setMessage(message);
        super.setCode(code);
    }

    /**
     * 失败返回
     * @param message
     */
    public void setFailInfo(String message){
        super.setSuccess(false);
        super.setMessage(message);
        super.setCode(FAIL);
    }


    /**
     * 失败返回
     * @param message
     * @param code
     */
    public void setErrorInfo(String message,String code){
        super.setSuccess(false);
        super.setMessage(message);
        super.setCode(code);
    }

    /**
     * 失败返回
     * @param message
     */
    public void setErrorInfo(String message){
        super.setSuccess(false);
        super.setMessage(message);
        super.setCode(ERROR);
    }



    /**
     * 成功返回
     * @param message
     */
    public void setSuccessInfo(String message){
        super.setSuccess(true);
        super.setMessage(message);
        super.setCode(SUCCESS);
    }



    /**
     * 成功返回
     * @param data
     * @param message
     */
    public void setSuccessInfo(T data,String message){
        this.data = data;
        setSuccessInfo(message);
    }


    /**
     * 成功返回 数据
     * @param data
     */
    public void setSuccessInfo(T data){
        this.data = data;
        setSuccessInfo("");
    }

}