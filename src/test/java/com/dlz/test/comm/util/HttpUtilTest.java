package com.dlz.test.comm.util;

import com.dlz.comm.exception.HttpException;
import com.dlz.comm.json.JSONMap;
import com.dlz.comm.util.web.HttpEnum;
import com.dlz.comm.util.web.HttpRequestParam;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpUtilTest {
	private static final String authorization = "Authorization";
	@Test
	public void timoutTest(){
		System.out.println(getOldId("c34bc463-f6c0-4a53-8d9e-2e1d5fdeed501"));
		System.out.println(getOldId("c34bc463-f6c0-4a53-8d9e-2e1d5fdeed50"));
	}
	@Test
	public void timoutTest2(){
		//		String aliyunApiPyhost = "http://192.168.1.68:8766/tool/countOss";
//		String aliyunApiPyhost = "http://py.chan3d.com/countOss";
		Map<String, String> map = new HashMap<>();
		map.put("prefix", "scenes/2FTHluvb0V2jRZEnecQldzLuZHv/");
		String url = "https://www.chan3d.com/tool/countOss";
		HttpRequestParam<String> msg= HttpRequestParam.createFormReq(url,map,String.class);
		msg.addHeader("Authorization","bearer d1808329-64b3-407f-92c8-7462bf6071ad");
		String s = HttpEnum.GET.send(msg);
		System.out.println(s);
	}

	@Test
	public void timoutTest3(){
		//		String aliyunApiPyhost = "http://192.168.1.68:8766/tool/countOss";
//		String aliyunApiPyhost = "http://py.chan3d.com/countOss";
		Map<String, String> map = new HashMap<>();
		map.put("prefix", "scenes/2FTHluvb0V2jRZEnecQldzLuZHv/");
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Authorization","bearer d1808329-64b3-407f-92c8-7462bf6071ad");
		String url = "https://www.chan3d.com/tool/countOss";
		String s = doGet(url,map,headerMap);
		System.out.println(s);
	}

	public static String doGet(String url, Map<String, String> param,Map<String, String> headerMap) {
		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String resultString = "";
		CloseableHttpResponse response = null;
		int code = 0;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();
			System.out.println(uri.toString());

			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri.toString());

			if (headerMap != null) {
				Iterator headerIterator = headerMap.entrySet().iterator();          //循环增加header
				while(headerIterator.hasNext()){
					Map.Entry<String,String> elem = (Map.Entry<String, String>) headerIterator.next();
					httpGet.addHeader(elem.getKey(),elem.getValue());
				}
			}
			// 执行请求
			response = httpclient.execute(httpGet);
			code = response.getStatusLine().getStatusCode();
			// 判断返回状态是否为200
			if (code == 200) {
				resultString = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		} catch (Exception e) {
			throw new HttpException(e.getMessage(),code);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				throw new HttpException(e.getMessage(),code);
			}
		}
		return resultString;
	}

	private static String getOldId(String token){
		HttpRequestParam<JSONMap> formReq = HttpRequestParam.createFormReq("http://39.108.125.90:8080/scene-portal/rest/api/user/info", null, JSONMap.class);
		formReq.setRequestConfig(RequestConfig.custom().setConnectTimeout(100).build());
		formReq.addHeader(authorization,"bearer "+token);
		try {
			return HttpEnum.GET.send(formReq).getStr("data");
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
