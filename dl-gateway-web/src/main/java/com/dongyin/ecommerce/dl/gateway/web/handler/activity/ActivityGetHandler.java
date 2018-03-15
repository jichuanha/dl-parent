package com.dongyin.ecommerce.dl.gateway.web.handler.activity;


import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.dto.ActivityDTO;
import com.dongyin.leaf.wsacd.errors.SalesActivityError;
import com.dongyin.leaf.wsacd.exception.SalesActivityException;
import com.dongyin.leaf.wsacd.service.iface.ActivityDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author jc
 * @description
 * @create 2018/2/27
 */
@Service
public class ActivityGetHandler extends ActivityComplexHandler {

    private static final Logger log = LoggerFactory.getLogger(ActivityGetHandler.class);

    @Resource
    private ActivityDetailService activityDetailService;

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response) throws BaseBusiException {

        Map<String, Object> requestMap = msg.getMsgFieldMap();


        Long id = RequestMapUtils.getLongNotAllowNull(requestMap, "activity_id", "activity_id is null");

        try {
            ActivityDTO activityDTO = activityDetailService.activityStart(id);

            return ResponseUtils.getSuccessResponse(activityDTO);
        } catch (SalesActivityException e) {
            return ResponseUtils.getFailResponse(SalesActivityError.B_BIZ_EXCEPTION,e.getMessage());
        }
    }
}
