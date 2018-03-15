package com.dongyin.ecommerce.dl.gateway.web.handler.actorder;

import com.dongyin.commons.exception.BaseBusiException;
import com.dongyin.commons.gateway.web.handler.IMessageHandler;
import com.dongyin.commons.gateway.web.handler.Message;
import com.dongyin.commons.gateway.web.handler.MessageAbstractHandler;
import com.dongyin.commons.web.exception.WebException;
import com.dongyin.commons.web.vo.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class ActOrderComplexHandler extends MessageAbstractHandler {

    private static final Logger log = LoggerFactory
            .getLogger(ActOrderComplexHandler.class);

    private Map<String, IMessageHandler> subModuleMap = new HashMap<>();

    public void setSubModuleMap(Map<String, IMessageHandler> subModuleMap) {
        this.subModuleMap = subModuleMap;
    }

    @Override
    public ApiResponse handlerRequest(Message msg, HttpServletRequest request, HttpServletResponse response) throws BaseBusiException {
        try {
            String messageId = msg.getId().substring(4);
            IMessageHandler iMessageHandler = subModuleMap.get(messageId);
            return iMessageHandler.handlerRequest(msg,request,response);
        } catch (BaseBusiException e) {
            log.error(" ActOrderComplexHandler error: ", e);
            throw new WebException(e);
        }
    }
}
