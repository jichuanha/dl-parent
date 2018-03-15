package com.dongyin.ecommerce.dl.gateway.web.handler.activity;


import com.dongyin.commons.base.model.BaseQTO;
import com.dongyin.commons.base.model.PaginationDTO;
import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.dto.AwardRecordDTO;
import com.dongyin.leaf.wsacd.dto.AwardRecordShowDTO;
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

/**下级为美丽顾问的引流详情
 * @author jc
 * @description
 * @create 2018/1/25
 */
@Service
public class ActivityDraiAgentDetailHandler extends ActivityComplexHandler {

    private static final Logger log = LoggerFactory.getLogger(ActivityDraiAgentDetailHandler.class);

    @Resource
    private ActivityDraiService activityDraiService;

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response) throws BaseBusiException {

        Map<String, Object> requestMap = msg.getMsgFieldMap();
        Long actId = RequestMapUtils.getLongNotAllowNull(requestMap,"act_id","act_id is null");

        Integer start = RequestMapUtils.getIntegerNotAllowNull(requestMap,
                "current_page", "起始页为空");
        Integer count = RequestMapUtils.getIntegerNotAllowNull(requestMap,
                "page_size", "每页条数为空");

        if (start == 0 || null == start) {
            start = 1;
        }
        if (count == 0 || null == count) {
            count = 20;
        }

        Long membrId = RequestMapUtils.getLongNotAllowNull(requestMap,"member_id","member_id is null");

        log.info("memberId:{}", membrId);

        BaseQTO<AwardRecordDTO> baseQTO = new BaseQTO<>();
        AwardRecordDTO awardRecordDTO = new AwardRecordDTO();
        awardRecordDTO.setActivityId(actId);
        awardRecordDTO.setMemberId(membrId);
        baseQTO.setCount(count);
        baseQTO.setStart(count*(start - 1));
        baseQTO.setData(awardRecordDTO);
        try {
            PaginationDTO<AwardRecordShowDTO> paginationDTO =  activityDraiService.getJuniorDraiawar(baseQTO);
            return ResponseUtils.getSuccessResponse(paginationDTO);
        } catch (SalesActivityException e) {
            log.error("ActivityDraiAgentDetailHandler error",e);
            return ResponseUtils.getFailResponse(SalesActivityError.B_BIZ_EXCEPTION,e.getMessage());
        }
    }
}
