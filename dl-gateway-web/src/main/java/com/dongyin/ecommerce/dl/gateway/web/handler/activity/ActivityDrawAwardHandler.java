package com.dongyin.ecommerce.dl.gateway.web.handler.activity;


import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.constants.ActUsLotteryEnum;
import com.dongyin.leaf.wsacd.dto.ActAwardConfDTO;
import com.dongyin.leaf.wsacd.dto.ActivityDTO;
import com.dongyin.leaf.wsacd.errors.SalesActivityError;
import com.dongyin.leaf.wsacd.exception.SalesActivityException;
import com.dongyin.leaf.wsacd.service.iface.ActivityManagerService;
import com.dongyin.leaf.wsacd.service.iface.DrawAwardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author jc
 * @description 抽奖
 * @create 2018/1/16
 */
@Service
public class ActivityDrawAwardHandler extends ActivityComplexHandler {
    private static final Logger log = LoggerFactory.getLogger(ActivityItemDetailHandler.class);

    @Resource
    private DrawAwardService drawAwardService;

    @Resource
    private ActivityManagerService activityManagerService;
    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response)
            throws BaseBusiException {

        Map<String, Object> requestMap = msg.getMsgFieldMap();

        Long id = RequestMapUtils.getLongNotAllowNull(requestMap,"act_id", "act_id is null");

        String openId = RequestMapUtils.getStringNotAllowNull(requestMap,"open_id","open_id is null");

        ActivityDTO activityDTO = activityManagerService.getActivityById(id);

        if(activityDTO.getUsLottery().equals(ActUsLotteryEnum.NOT_START_USE.getCode())) {
            return ResponseUtils.getFailResponse(SalesActivityError.ACTIVITY_DRAWAWARD_NOTSTART_ERROR);
        }


        Integer num = null;
        try {
            num = drawAwardService.getDrawAwardNumber(id,openId);
        } catch (SalesActivityException e) {
            log.error("getDrawAwardNumber error :",e);
            return ResponseUtils.getFailResponse(SalesActivityError.B_BIZ_EXCEPTION);
        }
        if(num == 0) {
            return ResponseUtils.getFailResponse(SalesActivityError.ACTIVITY_DRAWAWARDNULL_ERROR);
        }

        try {
            ActAwardConfDTO actAwardConfDTO = drawAwardService.drawAward(id,openId);
            return ResponseUtils.getSuccessResponse(actAwardConfDTO);
        } catch (SalesActivityException e) {
            log.error("getActAwardConfByActId error :",e);
            return ResponseUtils.getFailResponse(e.getResponseCode(),e.getMessage());
        }
    }
}
