package com.dongyin.ecommerce.dl.gateway.web.handler.actorder;

import com.dongyin.commons.authen.errors.AuthenError;
import com.dongyin.commons.cache.CacheClient;
import com.dongyin.commons.constants.HandleTypeEnum;
import com.dongyin.commons.errors.CommonError;
import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.utils.Strings;
import com.dongyin.commons.web.exception.WebException;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.ecommerce.dl.gateway.web.vo.ActOrderVO;
import com.dongyin.leaf.wsacd.dto.ActOrderDTO;
import com.dongyin.leaf.wsacd.service.iface.ActOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author lizg
 *生成订单handler
 */

@Service
public class AddActOrderHandler extends ActOrderComplexHandler{

    private static final Logger log = LoggerFactory
            .getLogger(AddActOrderHandler.class);

    @Resource
    private ActOrderService actOrderService;

    @Resource
    private CacheClient cacheClient;

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request,
                                      HttpServletResponse response) throws BaseBusiException {
        Map<String, Object> requestMap = msg.getMsgFieldMap();

        Long activityId = RequestMapUtils.getLongNotAllowNull(requestMap, "activity_id",
                "activity_id is null");
        Long itemId = RequestMapUtils.getLongNotAllowNull(requestMap, "item_id",
                "item_id is null");
        Integer buyCounts = RequestMapUtils.getIntegerNotAllowNull(requestMap,
                "buy_counts", "buy_counts is null");
        String wechatNo = RequestMapUtils.getStringNotAllowNull(requestMap,
                "wechat_no","wechat_no is null");
        String provinceCode = RequestMapUtils.getStringNotAllowNull(requestMap,
                "province_code","province_code is null");
        String cityCode = RequestMapUtils.getStringNotAllowNull(requestMap,
                "city_code","city_code is null");
        String areaCode = RequestMapUtils.getStringNotAllowNull(requestMap,"area_code",
                "area_code is null");
        String provinceName = RequestMapUtils.getStringNotAllowNull(requestMap,
                "province_name","province_name is null");
        String cityName= RequestMapUtils.getStringNotAllowNull(requestMap,
                "city_name","city_name is null");
        String areaName = RequestMapUtils.getStringNotAllowNull(requestMap,"area_name",
                "area_name is null");
        String address = RequestMapUtils.getStringNotAllowNull(requestMap, "address","address is null");

        String consignee = RequestMapUtils.getStringNotAllowNull(requestMap,"consignee","consignee is null");

        String mobile = RequestMapUtils.getStringNotAllowNull(requestMap,"mobile","mobile is null");

        //授权人的openId
        String openId = RequestMapUtils.getStringNotAllowNull(requestMap, "open_id","open_id is null");

        //分享人得shareOpenId
        String shareOpenId = RequestMapUtils.getStringNotAllowNull(requestMap, "share_open_id","share_open_id is null");

        String verifyCode = RequestMapUtils.getStringAllowNull(requestMap, "verify_code");// 验证码

        try {

            if (Strings.isBlank(verifyCode)) {
                return ResponseUtils.getFailResponse(
                        CommonError.P_PARAM_ISNULL, "请输入验证码！");
            }

            // 获取服务端缓冲 验证码
            String serverSideVerifyCode = null;
            try {
                serverSideVerifyCode = getVerifyCode(mobile,
                        HandleTypeEnum.REAL_NAME_AUTH.code);
            } catch (Exception e) {
                log.error("get verify code cache is error .....");
            }

            // 如果服务端验证码不存在
            if (null == serverSideVerifyCode) {
                return ResponseUtils.getFailResponse(
                        AuthenError.B_E_VERYFY_CODE_TIMEOUT, "验证码已失效");
            }

            if (!serverSideVerifyCode.equals(verifyCode)) {
                return ResponseUtils.getFailResponse(
                        AuthenError.B_E_VERYFY_CODE_INVALID, "请输入正确的验证码");
            }

            if (buyCounts <= 0) {
                throw new WebException("购买数量必须大于0.....");
            }
            // 创建订单
            ActOrderDTO actOrderDTO = new ActOrderDTO();
            actOrderDTO.setActivityId(activityId);
            actOrderDTO.setItemId(itemId);
            actOrderDTO.setWechatNo(wechatNo);
            actOrderDTO.setProvinceCode(provinceCode);
            actOrderDTO.setCityCode(cityCode);
            actOrderDTO.setAreaCode(areaCode);
            actOrderDTO.setAddress(address);
            actOrderDTO.setConsignee(consignee);
            actOrderDTO.setMobile(mobile);
            actOrderDTO.setShareOpenId(shareOpenId);
            actOrderDTO.setBuyCounts(buyCounts);
            actOrderDTO.setOpenId(openId);
            actOrderDTO.setProvinceName(provinceName);
            actOrderDTO.setCityName(cityName);
            actOrderDTO.setAreaName(areaName);
            Long  orderId = actOrderService.addActOrder(actOrderDTO);
            log.info("[{}] orderId:{}",orderId);
            ActOrderVO  actOrderVO = new ActOrderVO();
            actOrderVO.setOrderId(orderId);
            return ResponseUtils.getSuccessResponse(actOrderVO);
        } catch (BaseBusiException e) {
            log.error("act order create msg :", e);
            return ResponseUtils
                    .getFailResponse(e.getResponseCode(),e.getMessage());
        }

    }

    /**
     * 获取验证码
     *
     * @param mobile
     * @param typeCode
     * @return
     * @throws BaseBusiException
     */
    private String getVerifyCode(String mobile, String typeCode)
            throws Exception {
        String key = mobile + typeCode;
        //13107732809real_name_auth
        log.info("[{}] key:{}",key);
        Object obj = cacheClient.get(key);
        if (obj != null) {
            return (String) obj;
        } else {
            return null;
        }
    }
}
