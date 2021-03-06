package com.sanhao.notice.handler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sanhao.notice.config.Const;
import com.sanhao.notice.model.SmsResult;
import com.sanhao.notice.service.Handler;
import com.sanhao.notice.service.SmsProvider;

@Service("handlerCL")
public class HandlerCL implements Handler {
	private final static Logger logger = Logger.getLogger("notice");
	@Autowired
	@Qualifier("smsProviderCL")
	private SmsProvider smsProvider;

	@Autowired(required = false)
	private Handler next;

	public SmsResult handlerRequest(String mobile, String content) {
		SmsResult result = new SmsResult();
		result = smsProvider.sms_send(mobile, content);
		if (result.getSms_status() == Const.SMS_STATUS_OK) {
			logger.info("创蓝短信接口调用成功");
			return result;
		} else if (next != null) {
			logger.error("创蓝短信接口调用失败");
			return next.handlerRequest(mobile, content);
		} else {
			logger.error("没有可用的短信处理通道了");
			return result;
		}
	}

}