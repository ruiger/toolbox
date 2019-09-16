package com.ruiger.toolbox.sign.impl;

import com.ruiger.toolbox.sign.service.SignService;
import com.ruiger.toolbox.util.HolidayUtil;
import com.ruiger.toolbox.util.HttpClientUtil;
import com.ruiger.toolbox.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title: wuhan</p>
 * <p> </p>
 * <p>Company: www.dhcc.com.cn</p>
 * <p>Email: yeyi@dhcc.com.cn</p>
 * <p>Copyright: Copyright (c) 2019</p>
 *
 * @author yeyi
 * @version 1.0
 * @date 2019/9/16
 **/
@RestController
@Slf4j
public class DHCSignServiceImpl implements SignService {

	@Value("${sign.dhcc.url}")
	private String URL;

	@Value("${sign.dhcc.name}")
	private String DEFALUT_NAME;

	@RequestMapping(value = "/dhcSign")
	@Override
	public String sign(String name, String password) throws Exception {
		if (StringUtils.isBlank(name)) {
			name = DEFALUT_NAME;
		}
		//封装请求头
		Map<String, String> headers = new HashMap<>();
		headers.put("'Origin'", "192.168.0.231");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
		headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		headers.put("Accept-Encoding", "gzip,deflate");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		headers.put("Host", "192.168.0.231");
		headers.put("content-type", "charset=gb2312");
		headers.put("Cookie", "ASPSESSIONIDQQTACSAT=CBNNLIAANJAFIPAALIKPLOFP");
		headers.put("Referer", "http://192.168.0.231/index.asp");
		headers.put("Upgrade-Insecure-Requests", "1");
		headers.put("Cache-Control", "max-age=0");
		headers.put("Content-Type", "application/x-www-form-urlencoded");
//        headers.put("Content-Length", "44");

		Map<String, String> args = new LinkedHashMap<>();
		args.put("yhm", name);
		args.put("mm", name);
		args.put("bzsx", "");
		args.put("sxsc", "");
		args.put("Submit", "签到");
		String html = HttpUtil.executeUrl(URL, headers, args);
		// 从网页中抽取数据信息
		log.info("签到返回数据：" + "======================");
		log.info(html);
		return html;
	}


	@Scheduled(cron = "0 25 8 * * ?")
//	@Scheduled(fixedRate = 5000) //通过@Scheduled声明该方法是计划任务，使用fixedRate属性每隔固定时间执行
	public void sign() throws Exception {
		log.info("执行定时任务");
		//每天固定时间打开，采用随机数进行休眠
		Random r = new Random();
		int millis = r.nextInt(4) * 60 * 1000 + r.nextInt(60) * 1000;
		Thread.sleep(millis);
		Date now = Calendar.getInstance().getTime();
		log.info("任务执行时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String date = simpleDateFormat.format(now);
		if (!HolidayUtil.isHoliday(date)) {
			log.info("今天是工作日");
			sign("yy", "yy");
			/*
			Holiday holiday = this.holidayRepository.findByLeaveDate(date);
			if (holiday == null) {
				sign("yy","yy");
			} else {
				log.info("你今天休假了");
			}*/
		} else {
			log.info("今天休息哦");
		}

	}
}
