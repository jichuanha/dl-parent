package com.dongyin.ecommerce.dl.gateway.web.handler.activity;


import com.dongyin.commons.base.model.BaseQTO;
import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.dto.AwardRecordDTO;
import com.dongyin.leaf.wsacd.errors.SalesActivityError;
import com.dongyin.leaf.wsacd.exception.SalesActivityException;
import com.dongyin.leaf.wsacd.service.iface.ActivityDraiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *  代理商本人引流信息
 * @author jc
 * @description
 * @create 2018/1/24
 */
@Service
public class ActivityDraiDetailiHandler extends ActivityComplexHandler {

    private static final Logger log = LoggerFactory.getLogger(ActivityDraiDetailiHandler.class);

    @Resource
    ActivityDraiService activityDraiService;

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response) throws BaseBusiException {

        Map<String, Object> requestMap = msg.getMsgFieldMap();


        Long memebrId = RequestMapUtils.getLongNotAllowNull(requestMap,"member_id","member_id is null");

        Long actId = RequestMapUtils.getLongNotAllowNull(requestMap,"act_id","act_id is null");

        Integer start = RequestMapUtils.getIntegerNotAllowNull(requestMap,"current_page","current_page is null");

        Integer pageSize = RequestMapUtils.getIntegerNotAllowNull(requestMap,"page_size","page_size is null");

        if (start == 0 || null == start) {
            start = 1;
        }
        if (pageSize == 0 || null == pageSize) {
            pageSize = 20;
        }

        try {
            BaseQTO<AwardRecordDTO> baseQTO = new BaseQTO<>();
            AwardRecordDTO awardRecordDTO = new AwardRecordDTO();
            awardRecordDTO.setMemberId(memebrId);
            awardRecordDTO.setActivityId(actId);
            baseQTO.setData(awardRecordDTO);
            baseQTO.setStart((start - 1)*pageSize);
            baseQTO.setCount(pageSize);
            Map<String,Object> map = activityDraiService.getOwnDraiaward(baseQTO);
            return ResponseUtils.getSuccessResponse(map);
        } catch (SalesActivityException e) {
            log.error("ActivityDraiDetailiHandler error :",e);
            return ResponseUtils.getFailResponse(SalesActivityError.B_BIZ_EXCEPTION);
        }
    }
}
