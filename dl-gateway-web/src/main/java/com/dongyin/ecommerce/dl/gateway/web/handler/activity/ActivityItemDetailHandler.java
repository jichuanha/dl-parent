package com.dongyin.ecommerce.dl.gateway.web.handler.activity;

import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.dto.ActItemDTO;
import com.dongyin.leaf.wsacd.errors.SalesActivityError;
import com.dongyin.leaf.wsacd.service.iface.ActivityDetailService;
import com.dongyin.leaf.wsacd.service.iface.ActivityManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 活动商品详情
 * @author wsh
 *
 * 2018年1月12日
 */
@Service
public class ActivityItemDetailHandler extends ActivityComplexHandler {
	private static final Logger log = LoggerFactory.getLogger(ActivityItemDetailHandler.class);

	@Resource
	private ActivityManagerService activityManagerService;
	
	@Resource
	private ActivityDetailService activityDetailService;
	
	@Override
	public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response)
			throws BaseBusiException {
		
		Map<String, Object> requestMap = msg.getMsgFieldMap();

		Long id = RequestMapUtils.getLongNotAllowNull(requestMap,"activity_id", "id is null");
		try{

			List<ActItemDTO> list = activityDetailService.getActItemDTOByActId(id);

			return ResponseUtils.getSuccessResponse(list);
		}catch(Exception e){
			log.error("getActPageDTOByActId error :", e);
			return ResponseUtils
					.getFailResponse(SalesActivityError.B_BIZ_EXCEPTION);
		}
	}
}
