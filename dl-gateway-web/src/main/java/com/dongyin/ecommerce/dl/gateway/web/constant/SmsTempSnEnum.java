package com.dongyin.ecommerce.dl.gateway.web.constant;

import java.util.Objects;

/**
 * Created by lizg on 2017/1/11.
 */
public enum SmsTempSnEnum {

    MOBILE_VERIFY("mobile_verify", "手机验证码"),;

    public String code;

    public String desc;

    /**
     * @param code
     * @param desc
     */
    private SmsTempSnEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static SmsTempSnEnum getByCode(String code) {
        SmsTempSnEnum[] smsTempSns = SmsTempSnEnum.values();
        for (SmsTempSnEnum smsTempSn : smsTempSns) {
            if (Objects.equals(smsTempSn.getCode(), code)) {
                return smsTempSn;
            }
        }
        return null;
    }
}
