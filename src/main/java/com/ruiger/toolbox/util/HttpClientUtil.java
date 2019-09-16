package com.ruiger.toolbox.util;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("deprecation")
public class HttpClientUtil {
	public static KeyStore getKeyStoreFromFile(String file, String keyPass, String keyType) throws Exception {
		if(StringUtils.isBlank(file)){
			throw new IllegalArgumentException("file name can not be blank");
		}
		char[] pass = getPsw(keyPass);
		if(StringUtils.isEmpty(keyType)){
			keyType = KeyStore.getDefaultType();
		}
		KeyStore ks = KeyStore.getInstance(keyType);
		FileInputStream fin = new FileInputStream(file);
		ks.load(fin,pass);
		fin.close();
		return ks;
	}
	private static char[] getPsw(String psw){
		if(StringUtils.isEmpty(psw)){return null;}
		return psw.toCharArray();
	}
	private static CloseableHttpClient getSSLClient(SSLContext sslContext){
		SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslContext,new String[]{"TLSv1","TLSv2","TLSv3"},null,null);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sf).build();
		return httpclient;
	}
	public static CloseableHttpClient getTwoWayAuthHttpsClient(KeyStore serverStore, KeyStore personStore, String personKeyPsw) throws Exception {
		char[] personPsw = getPsw(personKeyPsw);
		SSLContext sslContext = SSLContexts.custom()
			.loadKeyMaterial(personStore, personPsw)
//			.loadTrustMaterial(trustStore,new TrustStrategy() {
//				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//					return true;
//				}
//			})
			.loadTrustMaterial(serverStore, new TrustSelfSignedStrategy())
			.build();
		return getSSLClient(sslContext);
	}
	public static CloseableHttpClient getOneWayAuthHttpsClient(KeyStore serverStore) throws Exception {
		SSLContext sslContext = SSLContexts.custom()
			.loadTrustMaterial(serverStore, new TrustSelfSignedStrategy())
			.build();
		return getSSLClient(sslContext);
	}
	public static CloseableHttpClient getNoAuthHttpsClient() throws Exception {
		SSLContext sc = SSLContext.getInstance("SSLv3");
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
			}
			@Override
			public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
			}
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		sc.init(null, new TrustManager[]{trustManager}, null);
		return getSSLClient(sc);
	}
	public static CloseableHttpClient getHttpClient(){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		return httpclient;
	}
	public static String doGet(CloseableHttpClient httpClient,String url,Map<String, String> param,Map<String,String> headers) throws Exception {
		URIBuilder builder = new URIBuilder(url);
		if(param != null) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				builder.addParameter(entry.getKey(), entry.getValue());
			}
		}
		HttpGet httpgets = new HttpGet(builder.build());
		if(headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httpgets.addHeader(entry.getKey(), entry.getValue());
			}
		}
		CloseableHttpResponse response = httpClient.execute(httpgets);
		try{
			return getResp(response);
		}catch(Exception e){
			throw e;
		}finally{
			response.close();
		}
	}
	private static String getResp(CloseableHttpResponse resp) throws Exception {
		InputStream is = resp.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try{
			while ((line = reader.readLine()) != null) {
				//sb.append(new String(line.getBytes("utf-8"),"gbk") + "\n");
				sb.append(new String(line.getBytes(), "utf-8"));
			}
		}catch(Exception e){
			throw e;
		}finally{
			is.close();
		}
		return sb.toString();
	}
	public static String doPost(CloseableHttpClient httpClient, String url, Map<String, String> param, Map<String,String> headers) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		if(param != null){
			ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : param.entrySet()) {
				paramList.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
			httpPost.setEntity(entity);
		}
		if(headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
		}
		CloseableHttpResponse response = httpClient.execute(httpPost);
		try{
			return getResp(response);
		}catch(Exception e){
			throw e;
		}finally{
			response.close();
		}
	}
	public static String doPost(CloseableHttpClient httpClient, String url, String json, Map<String,String> headers) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		if(!StringUtils.isBlank(json)){
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
		}
		if(headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
		}
		CloseableHttpResponse response = httpClient.execute(httpPost);
		try{
			return getResp(response);
		}catch(Exception e){
			throw e;
		}finally{
			response.close();
		}
	}
}
