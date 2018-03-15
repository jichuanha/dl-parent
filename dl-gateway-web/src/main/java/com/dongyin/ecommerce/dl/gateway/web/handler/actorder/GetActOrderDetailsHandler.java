package com.dongyin.ecommerce.dl.gateway.web.handler.actorder;

import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.utils.DateFormatUtil;
import com.dongyin.commons.utils.JsonUtil;
import com.dongyin.commons.utils.PriceUtil;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.ecommerce.dl.gateway.web.vo.ActOrderVO;
import com.dongyin.leaf.wsacd.dto.ActOrderDTO;
import com.dongyin.leaf.wsacd.service.iface.ActOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
public class GetActOrderDetailsHandler extends ActOrderComplexHandler{

    private static final Logger log = LoggerFactory.getLogger(GetActOrderDetailsHandler.class);

    @Resource
    private ActOrderService actOrderService;

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request,
                                      HttpServletResponse response) throws BaseBusiException {
        Map<String, Object> requestMap = msg.getMsgFieldMap();
        Long orderId = RequestMapUtils.getLongNotAllowNull(requestMap,
                "order_id","open_id is null");
        try {
            ActOrderDTO actOrderDTO = actOrderService.getActOrderDetails(orderId);
            ActOrderVO actOrderVO = null;
            if (null != actOrderDTO) {
                actOrderVO = new ActOrderVO();
                log.info("[{}] orderDTO++:{}", JsonUtil.toJson(actOrderDTO));
                BeanUtils.copyProperties(actOrderDTO,actOrderVO);
                actOrderVO.setOrderDate(DateFormatUtil.date2String(actOrderDTO.getCreateDate(),
                        DateFormatUtil.TIME_FORMAT_A));
                actOrderVO.setUnitPriceStr(String.valueOf(PriceUtil.parseFen2Yuan(actOrderDTO.getUnitPrice())));
                actOrderVO.setTotalAmount(String.valueOf(PriceUtil.parseFen2Yuan(actOrderDTO.getTotalMoney())));

                if (0 != actOrderDTO.getOrderAward()) {
                    actOrderVO.setOrderAwardStr(String.valueOf(PriceUtil.parseFen2Yuan(actOrderDTO.getOrderAward())));
                }
            }
            return ResponseUtils.getSuccessResponse(actOrderVO);
        } catch (BaseBusiException e) {
            log.error("check has associate agent msg :", e);
            return ResponseUtils.getFailResponse(e.getResponseCode(),e.getMessage());
        }

    }
}
