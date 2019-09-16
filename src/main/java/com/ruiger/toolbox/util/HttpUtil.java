package com.ruiger.toolbox.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 睿哥
 * @version 1.0
 * @time 9:46
 * @description #
 * @since 2017/03/10
 */
public  class HttpUtil {

	public final static int HTTP_GET = 1;		//get请求
	public final static int HTTP_POST = 2;		//post请求

	public static String executeUrl(String url,int type) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse lfdResponse = null;
		if(type == 1){
			HttpGet httpGet = new HttpGet(url);
			lfdResponse = httpClient.execute(httpGet);
		}else {
			HttpPost httpPost = new HttpPost(url);
			lfdResponse = httpClient.execute(httpPost);
		}
		if (lfdResponse.getStatusLine().getStatusCode() == 200) {
			HttpEntity loginEntity = lfdResponse.getEntity();
			String entityContent = EntityUtils.toString(loginEntity,"utf-8");
			return entityContent;
		}
		return null;
	}

	/**
	 * 发送post请求 带请求头
	 * @param url
	 * @param header
	 * @return
	 */
	public static String executeUrl(String url ,Map<String,String> header,Map<String,String> args)throws IOException{
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		if(!CollectionUtils.isEmpty(header)){
			for (String key : header.keySet()) {
				httpPost.setHeader(key, header.get(key));
			}
		}
		List<NameValuePair> params = new ArrayList();
		if(!CollectionUtils.isEmpty(args)){
			for (String key : args.keySet()) {
				params.add(new BasicNameValuePair(key, args.get(key)));
			}
		}
		httpPost.setEntity(new UrlEncodedFormEntity(params));
		HttpResponse lfdResponse = httpClient.execute(httpPost);
		if (lfdResponse.getStatusLine().getStatusCode() == 200) {
			HttpEntity loginEntity = lfdResponse.getEntity();
			String entityContent = EntityUtils.toString(loginEntity,"gb2312");
			return entityContent;
		}
		return null;

	}
}
