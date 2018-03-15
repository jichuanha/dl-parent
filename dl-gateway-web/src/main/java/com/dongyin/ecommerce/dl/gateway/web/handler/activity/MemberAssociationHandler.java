package com.dongyin.ecommerce.dl.gateway.web.handler.activity;

import com.dongyin.commons.errors.CommonError;
import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.web.util.RequestMapUtils;
import com.dongyin.commons.web.util.ResponseUtils;
import com.dongyin.commons.web.vo.ApiResponse;
import com.hzkans.leaf.user.dto.MemberAssociationDTO;
import com.hzkans.leaf.user.service.iface.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author ChengGM
 * @create 2017-04-10 15:10
 **/
@Service
public class MemberAssociationHandler extends ActivityComplexHandler {

	private static final Logger log = LoggerFactory
			.getLogger(MemberAssociationHandler.class);
	@Resource
	private MemberService memberService;

	@Override
	public ApiResponse handlerRequest(Message msg, HttpServletRequest request,
									  HttpServletResponse response) throws BaseBusiException {
		Map<String, Object> requestMap = msg.getMsgFieldMap();
		String openId = RequestMapUtils.getStringNotAllowNull(requestMap,
				"open_id", "open_id不能为空");
		try {
			MemberAssociationDTO memberAssociationDTO = memberService
					.getMemberAssociationByOpenId(openId);
			if (null == memberAssociationDTO) {
				return ResponseUtils.getSuccessResponse(false);
			}else {
				return ResponseUtils.getSuccessResponse(true);
			}

		} catch (Exception e) {
			log.error("getRegionMsg error :", e);
			return ResponseUtils
					.getFailResponse(CommonError.S_SYSTEM_ERROR);
		}

	}
}
