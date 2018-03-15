package com.dongyin.ecommerce.dl.gateway.web.handler.actorder;

import com.dongyin.commons.errors.CommonError;
import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.utils.PriceUtil;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.ecommerce.dl.gateway.web.vo.ActOrderVO;
import com.dongyin.leaf.wsacd.constants.ActOrderStatusEnum;
import com.dongyin.leaf.wsacd.dto.ActOrderDTO;
import com.dongyin.leaf.wsacd.service.iface.ActOrderService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订单列表
 *
 * @author lizg
 */
@Service
public class GetActOrderListHandler extends ActOrderComplexHandler {

    private static final Logger log = LoggerFactory.getLogger(GetActOrderListHandler.class);

    @Resource
    private ActOrderService actOrderService;

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request,
                                      HttpServletResponse response) throws BaseBusiException {
        Map<String, Object> requestMap = msg.getMsgFieldMap();
        String openId = RequestMapUtils.getStringNotAllowNull(requestMap,
                "open_id", "open_id is null");
        Integer orderStatus = RequestMapUtils.getIntegerNotAllowNull(requestMap,
                "order_status", "order_status is null");
        log.info("[{}] orderStatus:{}", orderStatus);
        try {
            if (!orderStatus.equals(ActOrderStatusEnum.UN_DELIVER_SHIP.getCode()) &&
                    !orderStatus.equals(ActOrderStatusEnum.HAS_DELIVER_SHIP.getCode())) {
                return ResponseUtils.getFailResponse(
                        CommonError.P_PARAM_INVALID, "请输入正确的订单状态");
            }

            List<ActOrderVO> actOrderVOList = null;
            List<ActOrderDTO> actOrderDTOList = actOrderService.getMobileActOrderList(orderStatus, openId);
            if (CollectionUtils.isNotEmpty(actOrderDTOList)) {
                actOrderVOList = new ArrayList<>();
                for (ActOrderDTO actOrderDTO : actOrderDTOList) {
                    ActOrderVO actOrderVO = new ActOrderVO();
                    BeanUtils.copyProperties(actOrderDTO, actOrderVO);
                    actOrderVO.setTotalMoneyStr(String.valueOf(PriceUtil.parseFen2Yuan(actOrderDTO.getTotalMoney())));
                    actOrderVO.setUnitPriceStr(String.valueOf(PriceUtil.parseFen2Yuan(actOrderDTO.getUnitPrice())));
                    actOrderVO.setActOrderDetailsDTOList(actOrderDTO.getActOrderDetailsDTOList());

                }
            }

            return ResponseUtils.getSuccessResponse(actOrderVOList);
        } catch (BaseBusiException e) {
            log.error("get mobile act  orders msg :", e);
            return ResponseUtils.getFailResponse(CommonError.S_SYSTEM_ERROR, e.getMessage());
        }

    }
}
