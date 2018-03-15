package com.dongyin.ecommerce.dl.gateway.web.handler.activity;

import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.dto.ActAwardConfDTO;
import com.dongyin.leaf.wsacd.dto.ActPageDTO;
import com.dongyin.leaf.wsacd.dto.LotteryRecordDTO;
import com.dongyin.leaf.wsacd.errors.SalesActivityError;
import com.dongyin.leaf.wsacd.exception.SalesActivityException;
import com.dongyin.leaf.wsacd.service.iface.ActivityDetailService;
import com.dongyin.leaf.wsacd.service.iface.DrawAwardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 进入抽奖页
 * @author jc
 * @description
 * @create 2018/1/24
 */
@Service
public class ActivityDrawAwardPageHandler extends ActivityComplexHandler {

    private static final Logger log = LoggerFactory.getLogger(ActivityDrawAwardPageHandler.class);

    @Resource
    private DrawAwardService drawAwardService;

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response) throws BaseBusiException {

        Map<String, Object> requestMap = msg.getMsgFieldMap();
        Long actId = RequestMapUtils.getLongNotAllowNull(requestMap,"act_id", "act_id is null");

        String openId = RequestMapUtils.getStringAllowNull(requestMap,"open_id");

        try {
            //获取该活动的获奖手机名单
            List<LotteryRecordDTO> mobiles = drawAwardService.getLotteryRecordDTOs(actId);

            //获取该对象抽奖次数
            Integer num = drawAwardService.getDrawAwardNumber(actId,openId);

            //获取该对象抽取的奖励
            List<ActAwardConfDTO> actAward = drawAwardService.getActAwardConfByOpenId(openId,actId);

            Map<String,Object> totalMap = new HashMap<>();
            totalMap.put("mobiles",mobiles);
            totalMap.put("num",num);
            totalMap.put("memberAward",actAward);
            return ResponseUtils.getSuccessResponse(totalMap);
        } catch (SalesActivityException e) {
            log.error("ActivityDrawAwardPageHandler error :",e);
            return ResponseUtils.getFailResponse(SalesActivityError.B_BIZ_EXCEPTION);
        }
    }
}
