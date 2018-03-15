package com.dongyin.ecommerce.dl.gateway.web.handler.actorder;

import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.service.iface.ActOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 判断是否为代理商
 * @author lizg
 */
@Service
public class HasAssociateAgentHandler extends ActOrderComplexHandler{
    private static final Logger log = LoggerFactory.getLogger(HasAssociateAgentHandler.class);

    @Resource
    private ActOrderService actOrderService;

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request,
                                      HttpServletResponse response) throws BaseBusiException {
        Map<String, Object> requestMap = msg.getMsgFieldMap();
        String openId = RequestMapUtils.getStringNotAllowNull(requestMap,
                "open_id","open_id is null");
        String shareOpenId = RequestMapUtils.getStringNotAllowNull(requestMap,
                "share_open_id","share_open_id is null");
        try {
            Boolean isShowPageAgent = actOrderService.checkActPageAgent(shareOpenId,openId);
            return ResponseUtils.getSuccessResponse(isShowPageAgent);
        } catch (BaseBusiException e) {
            log.error("check has associate agent msg :", e);
            return ResponseUtils.getFailResponse(e.getResponseCode(),e.getMessage());
        }

    }
}
