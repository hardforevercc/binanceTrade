package com.binance.trade.serviceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.binance.bean.TickerBean;
import com.binance.mybatis.dao.OkexExtMapper;
import com.binance.mybatis.dao.OkexTradeRecordMapper;
import com.binance.mybatis.model.OkexTradeRecord;
import com.binance.trade.serviceI.OkexAdaMainFlowV2ServiceI;
import com.binance.trade.serviceI.OkexPrivateServiceI;
import com.binance.trade.serviceI.OkexPublicServiceI;
import com.binance.trade.utils.OkexTradeUtils;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("okexAdaMainFlowV2ServiceI")
public class OkexAdaMainFlowV2ServiceImpl implements OkexAdaMainFlowV2ServiceI{
	private static final String BTC_USDT = "btc_usdt";
	private static final String ADA_USDT = "ada_usdt";
	private static final String BUY = "buy";
	private static final String SELL = "sell";
	private static String type;
	private static final String MARKETBUY = "buy_market";
	private static final String MARKETSELL = "sell_market";
	//private static BigDecimal currprice;//当前价格
	private static BigDecimal currAdaPrice;//当前Ada价格
	private static BigDecimal amount;
	private static BigDecimal myTradeAmt;
	private static BigDecimal myUsdt;
	private static BigDecimal myAda;
	private static final BigDecimal RULELEASTAMT = new BigDecimal("0.11");
	private static final BigDecimal RULEMAXAMT = new BigDecimal("0.15");
	private static final BigDecimal FEE = new BigDecimal("1").divide(new BigDecimal("100"));
	private static BigDecimal lastBuyPrice;
	private static int i = 0;
	private static List<BigDecimal> priceList= new ArrayList<BigDecimal>();
	private static List<BigDecimal> amountList= new ArrayList<BigDecimal>();
	@Autowired
	OkexPublicServiceI okexPublicService;
	@Autowired
	OkexPrivateServiceI okexPrivateService;
	@Autowired
	OkexExtMapper okexExtDao;
	@Autowired
	OkexTradeRecordMapper okexTradeRecordDao;
	@Override
	public void run() {
		while(true) {
			
			try {
				execute();
				Thread.sleep(300);
			} catch (InterruptedException e) {
				log.error("程序运行异常",e);
			}
		}
		
	}
	private void  execute() {
		//查询ada价格
		log.info("[==========OkexAdaMainFlowV2==========]");
		try {
			type = "";
			String adaTickerResp = okexPublicService.getTicker(ADA_USDT);
			TickerBean adaTicker = OkexTradeUtils.dealTicker(adaTickerResp);
			currAdaPrice = adaTicker.getTicker().getLast();
			log.info("[step_1][the last price of ada_usdt = "+currAdaPrice+"]");
			String balance = okexPrivateService.getBalance();
			Map<String, String> acMap;
			try {
				acMap = OkexTradeUtils.getFreeAc(balance, null);
			} catch (Exception e) {
				log.error("[step_1.1][query the balance of userAc failed!]",e);
				return;
			}
			log.info("[step_2][the balance of current acount:"+JSONObject.toJSONString(acMap)+"]");
			myAda = new BigDecimal(acMap.get("adaAc"));
			myUsdt = new BigDecimal(acMap.get("usdtAc"));
			//lastBuyPrice = 最后一条买入或卖出记录
			if(lastBuyPrice == null) {
				lastBuyPrice = okexExtDao.selectLastBuyPrice();
			}
			log.info("[step_3][the price of the last deal:"+JSONObject.toJSONString(acMap)+"]");
			if(currAdaPrice.compareTo(lastBuyPrice) == 0) {
				log.info("[step_3.1][the currAdaPrice equals to the lastBuyPrice,no deal...]");
				return;
			}
			//视为空仓
			if(myAda.compareTo(BigDecimal.ONE) < 0) {
				lastBuyPrice = BigDecimal.ZERO;
			}
			//视为满仓
			BigDecimal buyAndsellPercent = new BigDecimal("0.2");
			if(myUsdt.compareTo(BigDecimal.ONE) < 0) {
				exeTradeOrder(SELL,myAda.multiply(buyAndsellPercent).toString(),currAdaPrice.toString());
				return;
			}
			BigDecimal holdPercent =myAda.multiply(currAdaPrice).divide(myAda.multiply(currAdaPrice).add(myUsdt),BigDecimal.ROUND_DOWN); 
			BigDecimal myUsdtToAda = myUsdt.divide(currAdaPrice,BigDecimal.ROUND_DOWN);
			
			//boolean flag = lastBuyPrice.subtract(currAdaPrice.multiply(BigDecimal.ONE.add(FEE))).compareTo(BigDecimal.ZERO) >= 0?true:false;
			log.info("lastBuyPrice is equal to "+ lastBuyPrice);
			if(lastBuyPrice.subtract(currAdaPrice).compareTo(BigDecimal.ZERO) >= 0) {
				if(lastBuyPrice.subtract(currAdaPrice.multiply(BigDecimal.ONE.add(FEE.multiply(new BigDecimal("2.5"))))).compareTo(BigDecimal.ZERO) >= 0) {
					type = BUY;
				}
			}else {
				if(currAdaPrice.subtract(lastBuyPrice.multiply(BigDecimal.ONE.add(FEE))).compareTo(BigDecimal.ZERO) >= 0) {
					type = SELL;
				}
			}	
			//当前价格小于0.11
			if(currAdaPrice.compareTo(RULELEASTAMT) < 0 ) {
				if(currAdaPrice.compareTo(new BigDecimal("0.09")) <=0 ) {
					if(holdPercent.compareTo(new BigDecimal("0.5")) <= 0) {
						//only buy
						buyAndsellPercent = new BigDecimal("0.5").subtract(holdPercent);
						exeTradeOrder(BUY,myUsdtToAda.multiply(buyAndsellPercent).toString(),currAdaPrice.toString());
						return;
					}else {					
						exeTradeOrder(type,myAda.multiply(buyAndsellPercent).toString(),currAdaPrice.toString());
						return;					
					}
				}else if(currAdaPrice.compareTo(new BigDecimal("0.1")) <=0){
					if
					(holdPercent.compareTo(new BigDecimal("0.4")) <= 0) {
						buyAndsellPercent = new BigDecimal("0.4").subtract(holdPercent);
						exeTradeOrder(BUY,myUsdtToAda.multiply(buyAndsellPercent).toString(),currAdaPrice.toString());
						return;
					}else {
						if(type.equals(BUY)) {
							buyAndsellPercent = myUsdtToAda.multiply(buyAndsellPercent);
						}
						if(type.equals(SELL)) {
							buyAndsellPercent = myAda.multiply(buyAndsellPercent);
						}
						exeTradeOrder(type,buyAndsellPercent.toString(),currAdaPrice.toString());
						return;	
					}
				}else {
					if(holdPercent.compareTo(new BigDecimal("0.3")) <= 0) {
						buyAndsellPercent = new BigDecimal("0.3").subtract(holdPercent);
						exeTradeOrder(BUY,myUsdtToAda.multiply(buyAndsellPercent).toString(),currAdaPrice.toString());
						return;
					}else {
						
						if(StringUtils.isNotBlank(type)) {
							if(type.equals(BUY)) {
								buyAndsellPercent = myUsdtToAda.multiply(buyAndsellPercent);
							}
							if(type.equals(SELL)) {
								buyAndsellPercent = myAda.multiply(buyAndsellPercent);
							}
							exeTradeOrder(type,buyAndsellPercent.toString(),currAdaPrice.toString());
						}else {
							log.info("the price is not reasonable...no deals!");
						}
						
						return;
					}
				}
			}
			//当前价格大于0.15
			else if(currAdaPrice.compareTo(RULEMAXAMT) > 0 ) {
				if(holdPercent.compareTo(new BigDecimal("0.1")) <= 0) {
					buyAndsellPercent = new BigDecimal("0.1").subtract(holdPercent);
					exeTradeOrder(BUY,myUsdtToAda.multiply(buyAndsellPercent).toString(),currAdaPrice.toString());
					return;
				}else {
					if(StringUtils.isNotBlank(type)) {
						if(type.equals(BUY)) {
							buyAndsellPercent = myUsdtToAda.multiply(buyAndsellPercent);
						}
						if(type.equals(SELL)) {
							buyAndsellPercent = myAda.multiply(buyAndsellPercent);
						}
						exeTradeOrder(type,buyAndsellPercent.toString(),currAdaPrice.toString());
					}else {
						log.info("the price is not reasonable...no deals!");
					}
					
					return;
				}
			}
			//当前价格大于等于0.11,小于等于0.15
			else {
				if(holdPercent.compareTo(new BigDecimal("0.3")) <= 0) {
					if(holdPercent.compareTo(new BigDecimal("0.2")) <= 0) {
						buyAndsellPercent = new BigDecimal("0.2").subtract(holdPercent);
						exeTradeOrder(BUY,myUsdtToAda.multiply(buyAndsellPercent).toString(),currAdaPrice.toString());
						return;
					}
				}else {
					if(StringUtils.isNotBlank(type)) {
						if(type.equals(BUY)) {
							buyAndsellPercent = myUsdtToAda.multiply(buyAndsellPercent);
						}
						if(type.equals(SELL)) {
							buyAndsellPercent = myAda.multiply(buyAndsellPercent);
						}
						exeTradeOrder(type,buyAndsellPercent.toString(),currAdaPrice.toString());
					}else {
						log.info("the price is not reasonable...no deals!");
					}
					return;
					
				}
			}
		}catch(Exception e) {
			log.error("程序运行异常 ^_^ 没关系 我还能跑~~~~",e);
		}
		
		
	}
	public void exeTradeOrder(String type,String amount,String price) {
		Map<String,String> tradeMap = new HashMap<String,String>();
		tradeMap.put("type",type);
		tradeMap.put("amount",amount.toString());
		tradeMap.put("symbol",ADA_USDT );
		tradeMap.put("price", price.toString());
		boolean flag = exeTrade(tradeMap);
		
		if(flag) {
			
			if(type.equals(BUY)) {
				
				priceList.add(currAdaPrice);
				amountList.add(new BigDecimal(amount));
				BigDecimal sumAmt = BigDecimal.ZERO;
				BigDecimal sumAmount = BigDecimal.ZERO;
				for(int i =0;i < priceList.size();i++) {
					sumAmt = sumAmt.add(priceList.get(i).multiply(amountList.get(i)));
					sumAmount = sumAmount.add(amountList.get(i));
				}
				lastBuyPrice = sumAmt.divide(sumAmount,BigDecimal.ROUND_DOWN);
				log.info("the price of current holding:lastBuyPrice ="+lastBuyPrice);
			}else {
				priceList.clear();
				//priceList.add(BigDecimal.ZERO);
				lastBuyPrice = currAdaPrice;
			}
			log.info("the lastBuyPrice is = "+ lastBuyPrice);
			insertIntoRecord(tradeMap,flag);
		}else {
			log.info("交易失败第{"+i+"}次");
			//若失败次数*为10的倍数则清除历史订单
			if(i%5 == 0) {
				Map<String,String> map = new HashMap<String,String>();
				map.put("symbol","ada_usdt");
				map.put("status","0");
				map.put("current_page","1");
				map.put("page_length","200");
				Map<String,List<String>> orderList = okexPrivateService.getHisOrders(map);
				Map<String,Object> cancerMap = new HashMap<String,Object>();
				cancerMap.put("orderIds", orderList.get("orderIds"));
				cancerMap.put("symbol","ada_usdt");
				okexPrivateService.cancelOrder(cancerMap);
				log.info("未完成订单取消完成");
			}
		}
	}
	/**
	 * 执行订单
	 * @param orderMap
	 * @return
	 */
	private boolean exeTrade(Map<String,String> orderMap) {
		log.info(JSONObject.toJSONString(orderMap));
		String orderResp = okexPrivateService.exeOrder(orderMap);
		log.info("订单结果:"+orderResp);
		if(null == orderResp) {
			log.error("系统异常,查询结果为空");
		}
		if(JSONObject.parseObject(orderResp).getBooleanValue("result")) {
			return true;
		}else{
			return false;
		}
	}
	private void insertIntoRecord(Map<String,String> map,boolean flag) {
		OkexTradeRecord record = new OkexTradeRecord();
		BigDecimal amount = new BigDecimal(map.get("amount"));
		BigDecimal price = new BigDecimal(map.get("price"));
		if(flag) {			
			record.setIsok("Y");			
		}else {		
			record.setIsok("N");
		}
		record.setAmt(price.multiply(amount));
		record.setCreateDate(new Date());
		record.setCurprice(price);
		record.setCuramount(amount);
		record.setType(map.get("type").toString());
		okexTradeRecordDao.insert(record);
	}
	
}
