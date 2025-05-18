package com.astral.common.constant;

public class CommonConstant {

    public static final Integer SC_OK_200 = 200;

    public static final Integer SC_ERROR_500 = 500;

    public static final Integer SC_ERROR_401 = 401;

    /**
     * 无权限访问返回码
     */
    public static final Integer SC_JEECG_NO_AUTHZ = 510;

    /**
     * 文件上传类型（本地：local,upyun,minio）
     */
    public static final String UPLOAD_TYPE_LOCAL = "local";
    public static final String UPLOAD_TYPE_UPYUN = "upyun";
    public static final String UPLOAD_TYPE_MINIO = "minio";

}
