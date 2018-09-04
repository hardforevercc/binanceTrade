package com.binance.trade.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.binance.trade.serviceI.OkexAdaMainFlowServiceI;
import com.binance.trade.serviceI.OkexPublicServiceI;
import com.binance.trade.utils.HttpUtils;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/base")
public class OkexQueryController {
	@Autowired
	OkexPublicServiceI okexPublicService;
	@Autowired
	OkexAdaMainFlowServiceI okexMainFlowService;
	@RequestMapping("/test")
	@ResponseBody
	public String test(HttpServletRequest request) throws Exception {
		String currency = null;
		try {
			currency = JSONObject.parseObject(HttpUtils.getMsg(request)).getString("currency");
			log.info("请求查询交易对:"+currency);
		} catch (IOException e) {
			log.error("内部系统处理错误",e);
		}
		return "交易进行中";
	}
	@RequestMapping("/index")
	@ResponseBody
	public String index() {
		return null;
		
	}
}
