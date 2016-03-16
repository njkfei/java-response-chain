# java职责链实战

## 项目背景
　公司有一个短信通知业务，考虑到成本以及可用性问题，选用了4家短信服务商．最开始设计时，只有一家服务商．
　因此．发送短信时，这样就可以．
　```
　smsProviderService.send_sms(mobile,content);
　```
　
　后来，公司又增加了短信服务商，这家供应商更便宜．但为了可靠性，比如新的供应商短信发送失败，这时候还要使用用第二家供应商服务．于是，
　代码成这样了．
　```
　if(smsProviderService.send_sms(mobile,content) != ok){
　}else{
　smsProviderService2.send_sms(mobile,content);
　}
　```
　
　后来，又来了一家．这时候，代码成了这样．
　```
　　if(smsProviderService.send_sms(mobile,content) != ok){
　}else 　if(smsProviderService2.send_sms(mobile,content) != ok){{
　smsProviderService3.send_sms(mobile,content);
　}
　```
　
　再后来，又来了一家．我受不了了．我不能被这玩意搞死啊，怎么办？
　重构．
　
　引用职责链模式即可．直接上代码去．

## 接口定义　
```
import com.sanhao.notice.model.SmsResult;

public interface Handler {
	  // 返回接口通道 0 表示无通道可用
	  public  SmsResult handlerRequest(String mobile,String content);
}
```

## 服务商1
```
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
```

##　服务商2
```
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sanhao.notice.config.Const;
import com.sanhao.notice.model.SmsResult;
import com.sanhao.notice.service.Handler;
import com.sanhao.notice.service.SmsProvider;

@Service("handlerHX")
public class HandlerHX  implements Handler{
	private final static Logger logger = Logger.getLogger("notice");
	@Autowired
	@Qualifier("smsProviderHX")
	private SmsProvider smsProvider;
	
	@Autowired
	@Qualifier("handlerYP")
	private Handler next;

	public SmsResult handlerRequest(String mobile, String content) {
		SmsResult result = new SmsResult();
		result = smsProvider.sms_send(mobile, content);
		if (result.getSms_status() == Const.SMS_STATUS_OK) {
			logger.info("华信短信接口调用成功");
			return result;
		} else if (next != null) {
			logger.error("华信短信接口调用失败");
			return next.handlerRequest(mobile, content);
		} else {
			logger.error("没有可用的短信处理通道了");
			return result;
		}
	}
	  
}
```

##　服务商3
```
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sanhao.notice.config.Const;
import com.sanhao.notice.model.SmsResult;
import com.sanhao.notice.service.Handler;
import com.sanhao.notice.service.SmsProvider;

@Service("handlerHY")
public class HandlerHY implements Handler {
	private final static Logger logger = Logger.getLogger("notice");
	@Autowired
	@Qualifier("smsProviderHuyi")
	private SmsProvider smsProvider;

	@Autowired
	@Qualifier("handlerCL")
	private Handler next;

	public SmsResult handlerRequest(String mobile, String content) {
		SmsResult result = new SmsResult();
		result = smsProvider.sms_send(mobile, content);
		if (result.getSms_status() == Const.SMS_STATUS_OK) {
			logger.info("互亿短信接口调用成功");
			return result;
		} else if (next != null) {
			logger.error("互亿短信接口调用失败");
			return next.handlerRequest(mobile, content);
		} else {
			logger.error("没有可用的短信处理通道了");
			return result;
		}
	}

}
```
## 服务商4
```
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sanhao.notice.config.Const;
import com.sanhao.notice.model.SmsResult;
import com.sanhao.notice.service.Handler;
import com.sanhao.notice.service.SmsProvider;

@Service("handlerYP")
public class HandlerYP implements Handler {
	private final static Logger logger = Logger.getLogger("notice");
	@Autowired
	@Qualifier("smsProviderYP")
	private SmsProvider smsProvider;

	@Autowired
	@Qualifier("handlerHY")
	private Handler next;

	public SmsResult handlerRequest(String mobile, String content) {
		SmsResult result = new SmsResult();
		result = smsProvider.sms_send(mobile, content);
		if (result.getSms_status() == Const.SMS_STATUS_OK) {
			logger.info("云片短信接口调用成功");
			return result;
		} else if (next != null) {
			logger.error("云片短信接口调用失败");
			return next.handlerRequest(mobile, content);
		} else {
			logger.error("没有可用的短信处理通道了");
			return result;
		}
	}
}
```
