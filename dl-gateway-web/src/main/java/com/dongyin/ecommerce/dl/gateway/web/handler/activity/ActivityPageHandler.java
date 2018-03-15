package com.dongyin.ecommerce.dl.gateway.web.handler.activity;

import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.dto.ActPageDTO;
import com.dongyin.leaf.wsacd.errors.SalesActivityError;
import com.dongyin.leaf.wsacd.service.iface.ActivityDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 
 * @author wsh
 *
 * 2017年12月29日
 */
@Service
public class ActivityPageHandler extends ActivityComplexHandler {

	private static final Logger log = LoggerFactory.getLogger(ActivityPageHandler.class);

	@Resource
	private ActivityDetailService activityDetailService;
	
	@Override
	public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response)
			throws BaseBusiException {
		Map<String, Object> requestMap = msg.getMsgFieldMap();

		Long id = RequestMapUtils.getLongNotAllowNull(requestMap,"activity_id", "activity_id is null");
		try{
			ActPageDTO actPageDTO = activityDetailService.getActPageDTOByActId(id);
			return ResponseUtils.getSuccessResponse(actPageDTO);
		}catch(Exception e){
			log.error("getActPageDTOByActId error :", e);
			return ResponseUtils
					.getFailResponse(SalesActivityError.B_BIZ_EXCEPTION);
		}
	}


}
