package com.dongyin.ecommerce.dl.gateway.web.handler.sms;

import com.dongyin.authen.errors.AuthenError;
import com.dongyin.authen.iface.MemberCertService;
import com.dongyin.commons.authen.dto.MemberCertDTO;
import com.dongyin.commons.authen.exception.AuthenException;
import com.dongyin.commons.cache.CacheClient;
import com.dongyin.commons.constants.HandleTypeEnum;
import com.dongyin.commons.errors.CommonError;
import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.service.iface.SmsService;
import com.dongyin.commons.utils.JsonUtil;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.ecommerce.dl.gateway.web.constant.SmsTempSnEnum;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
public class SendSmsHandler extends SmsComplexHandler{
    private static final Logger log = LoggerFactory
            .getLogger(SendSmsHandler.class);

    @Resource
    private SmsService smsService;

    @Resource
    private CacheClient cacheClient;

    @Resource
    private MemberCertService memberCertService;

    private final int minute = 10; /* 超时时间，单位分钟 */
    private final String tempSn = SmsTempSnEnum.MOBILE_VERIFY.code;/* 手机验证码模板编号 */

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request,
                                      HttpServletResponse response) throws BaseBusiException {
        Map<String, Object> requestMap = msg.getMsgFieldMap();

        String mobile = RequestMapUtils.getStringNotAllowNull(requestMap, "mobile","手机号码不能为空");

        String handleType = RequestMapUtils.getStringNotAllowNull(requestMap, "handle_type","操作类型不能为空");

        if (null == HandleTypeEnum.getByCode(handleType)) {
            log.error("操作类型handleType格式错误");
            return ResponseUtils.getFailResponse(CommonError.P_PARAM_INVALID,"操作类型handleType格式错误");
        }

        String handleName = HandleTypeEnum.getByCode(handleType).desc; // 短信标题

        // 验证手机号码
        try {
            log.info(" mobile :{} ", mobile);
            log.info(" handleType :{} ", handleType);
            MemberCertDTO memberDTO = new MemberCertDTO();
            memberDTO.setMobile(mobile);
            log.info(" memberDTO :{} ", JsonUtil.toJson(memberDTO));
            MemberCertDTO memberCertDTO = memberCertService.getMember(memberDTO);
            log.info(" memberCertDTO :{} ", JsonUtil.toJson(memberCertDTO));

            if (handleType.equals(HandleTypeEnum.REAL_NAME_AUTH.code)) {
                if (null != memberCertDTO) {
                    return ResponseUtils.getFailResponse(AuthenError.B_E_MOBILE_HAS_REGISTERED,
                            " 该手机号已被使用，请更换 ");
                }
            }
        } catch (AuthenException e) {
            log.error("调用memberCertService.memberLogin接口异常: {}", e);
            return ResponseUtils.getFailResponse(AuthenError.S_SYSTEM_ERROR, ExceptionUtils.getFullStackTrace(e));
        }

		/* 生成验证码 */
        long verifyCode = System.currentTimeMillis() % 1000000;
        String verifyCodeStr = String.format("%06d", verifyCode);

		/* 短信动态内容 */
        String[] values = {verifyCodeStr, handleName, String.valueOf(minute)};
        log.info(" set in cache key : " + mobile + handleType + " value : " + verifyCodeStr);

		/* 验证码信息缓存，10分钟，按秒计算，key为mobile+handleType */
        try {

            Boolean resultRtn = smsService.sendSecondsTick(mobile, tempSn, values);
            String rtnStr = "短信发送失败";
            if (resultRtn) {

                cacheClient.set(mobile + handleType, minute * 60, verifyCodeStr);

                log.info(" cache set success key : " + mobile + handleType + " value : "
                        + cacheClient.get(mobile + handleType));

                rtnStr = "短信已发送";
            }

            log.info("rtnStr:{}", rtnStr);
            return ResponseUtils.getSuccessResponse(rtnStr);

        } catch (Exception e) {
            return ResponseUtils.getFailResponse(CommonError.S_SYSTEM_ERROR, e.getMessage());
        }

    }

}
