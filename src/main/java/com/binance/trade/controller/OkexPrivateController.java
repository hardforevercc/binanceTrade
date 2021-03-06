package com.binance.trade.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binance.trade.serviceI.OkexAdaMainFlowServiceI;
import com.binance.trade.serviceI.OkexAdaMainFlowV2ServiceI;
import com.binance.trade.serviceI.OkexPrivateServiceI;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/private")
public class OkexPrivateController {
	@Autowired
	OkexPrivateServiceI okexPrivateService;
	@Autowired
	OkexAdaMainFlowServiceI okexAdaMainFlowService;
	@Autowired
	OkexAdaMainFlowV2ServiceI okexAdaMainFlowV2Service;
	@RequestMapping("/getUserInfo")
	public String getBalance(HttpServletRequest request) {
		
		return okexPrivateService.getBalance();
	}
	@RequestMapping("/Adatrade")
	public String startAda(HttpServletRequest request) {
		try {
			okexAdaMainFlowService.execute();
		} catch (Exception e) {
			log.error("trade error",e);
		}
		return "ada trading start...";
	}
	@RequestMapping("/Adatrade2")
	public String getHistory(HttpServletRequest request) {
		Map<String,String> map = null;
		String reqMsg;
		try {
			//reqMsg = HttpUtils.getMsg(request);
			//map = JSONObject.parseObject(reqMsg,Map.class);
			okexAdaMainFlowV2Service.run();
		} catch (Exception e) {
			log.error("报文格式转换异常",e);
		}
		
		return "okex_trade_v2 starting";
	}
}
