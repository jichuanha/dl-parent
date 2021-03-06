package com.dongyin.ecommerce.dl.gateway.web.handler.activity;

import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.dto.ActivityDTO;
import com.dongyin.leaf.wsacd.errors.SalesActivityError;
import com.dongyin.leaf.wsacd.service.iface.ActivityDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 活动列表
 * @author wsh
 *
 * 2018年3月5日
 */
@Service
public class ActivityPictureHandler extends ActivityComplexHandler {

	private static final Logger log = LoggerFactory.getLogger(ActivityPictureHandler.class);

	@Resource
	private ActivityDetailService activityDetailService;
	
	@Override
	public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response)
			throws BaseBusiException {
		
		try {
			ActivityDTO activityDTO = new ActivityDTO();
			activityDTO.setActStatus(2);
			List<ActivityDTO> activitys = activityDetailService.getActivitys(activityDTO);
			return ResponseUtils.getSuccessResponse(activitys);
		} catch (BaseBusiException e) {
			log.error("getActivitys error:", e);
			return ResponseUtils.getFailResponse(SalesActivityError.B_BIZ_EXCEPTION);
		}
	}
}
