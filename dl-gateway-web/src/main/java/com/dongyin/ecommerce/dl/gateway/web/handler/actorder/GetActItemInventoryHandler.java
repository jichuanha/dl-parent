package com.dongyin.ecommerce.dl.gateway.web.handler.actorder;

import com.dongyin.commons.errors.CommonError;
import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.dto.ActItemDTO;
import com.dongyin.leaf.wsacd.service.iface.ActOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
public class GetActItemInventoryHandler extends ActOrderComplexHandler{

    private static final Logger log = LoggerFactory.getLogger(GetActItemInventoryHandler.class);

    @Resource
    private ActOrderService actOrderService;

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request,
                                      HttpServletResponse response) throws BaseBusiException {
        Map<String, Object> requestMap = msg.getMsgFieldMap();
        Long actId = RequestMapUtils.getLongNotAllowNull(requestMap,
                "act_id","act_id is null");
        Long itemId = RequestMapUtils.getLongNotAllowNull(requestMap,
                "item_id","item_id is null");
        try {
            ActItemDTO actItemDTO = actOrderService.getActItem(actId,itemId);
            if (null == actItemDTO) {
                return ResponseUtils.getFailResponse(
                        CommonError.B_BIZ_EXCEPTION);
            }
            return ResponseUtils.getSuccessResponse(actItemDTO);
        } catch (BaseBusiException e) {
            log.error("get act item inventory msg :", e);
            return ResponseUtils.getFailResponse(e.getResponseCode(),e.getMessage());
        }

    }
}
