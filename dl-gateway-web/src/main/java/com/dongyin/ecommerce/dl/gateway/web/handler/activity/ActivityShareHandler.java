package com.dongyin.ecommerce.dl.gateway.web.handler.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dongyin.commons.utils.PriceUtil;
import com.hzkans.leaf.user.dto.MemberDTO;
import com.hzkans.leaf.user.service.iface.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.dongyin.leaf.wsacd.dto.ActItemDTO;
import com.dongyin.leaf.wsacd.errors.SalesActivityError;
import com.dongyin.leaf.wsacd.exception.SalesActivityException;
import com.dongyin.leaf.wsacd.service.iface.ActivityManagerService;

/**
 * 活动分享
 * 
 * @author wsh
 * @description
 * @create 2018/3/5
 */
@Service
public class ActivityShareHandler extends ActivityComplexHandler {

	private static final Logger log = LoggerFactory.getLogger(ActivityShareHandler.class);

	@Resource
	private MemberService memberService;

	@Resource
	private ActivityManagerService activityManagerService;

	@Override
	public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response)
			throws BaseBusiException {

		Map<String, Object> requestMap = msg.getMsgFieldMap();
		String shareOpenId = RequestMapUtils.getStringNotAllowNull(requestMap,
				"share_open_id", "share_open_id is null");
		Long activityId = RequestMapUtils.getLongNotAllowNull(requestMap,
				"activity_id", "activity_id is null");

		try {
			ActItemDTO actItemDTO = new ActItemDTO();
			actItemDTO.setActivityId(activityId);
			List<ActItemDTO> actItem = activityManagerService.getActItem(actItemDTO);
			Map<String,Object> map = null;
			if(actItem != null || actItem.size() != 0) {
				map = new HashMap<String,Object>();
				MemberDTO memberDTO = memberService.getMembersByOpenId(shareOpenId);
				map.put("portraitUri",memberDTO.getPortraitUri());
				map.put("realName",memberDTO.getRealName());
				map.put("itemId",String.valueOf(actItem.get(0).getItemId()));
				map.put("itemPrice", PriceUtil.parseFen2YuanStr(actItem.get(0).getItemPrice()));
			}
			return ResponseUtils.getSuccessResponse(map);
		} catch (SalesActivityException e) {
			log.error("getActItem error :", e);
			return ResponseUtils.getFailResponse(SalesActivityError.B_BIZ_EXCEPTION);
		}
	}
}
