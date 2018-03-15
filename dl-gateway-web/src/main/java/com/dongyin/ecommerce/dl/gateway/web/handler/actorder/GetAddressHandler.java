package com.dongyin.ecommerce.dl.gateway.web.handler.actorder;

import com.dongyin.commons.errors.CommonError;
import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.hzkans.leaf.user.dto.MemberDTO;
import com.hzkans.leaf.user.service.iface.DeliveryRegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author
 * Created by wsh on 2018/3/12.
 */
@Service
public class GetAddressHandler extends ActOrderComplexHandler {

    private static final Logger log = LoggerFactory.getLogger(GetAddressHandler.class);

    @Resource
    private DeliveryRegionService deliveryRegionService;

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response) throws BaseBusiException {

        try {
            Map map = deliveryRegionService.getRegionMsg();
            return ResponseUtils.getSuccessResponse(map);
        } catch (Exception e) {
            log.error("GetAddressHandler error :", e);
            return ResponseUtils.getFailResponse(CommonError.S_SYSTEM_ERROR);
        }

    }
}
