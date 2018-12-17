package com.django.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONObject;

import sun.misc.BASE64Encoder;

/**
 * 百度AI人脸识别
 * 
 * @author yinyunqi
 *
 */
@Controller
public class FaceController {
	
	/**
	 * 跳转到页面
	 * @param model
	 * @return
	 */
	@RequestMapping("/faceReg")
	public String faceRegIndex(Model model) {
		return "faceReg";
	}

	/**
	 * 接收文件并处理
	 * @param file
	 * @return 返回处理后的结果
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/face/reg/file", headers = "content-type=multipart/*", method = RequestMethod.POST)
	public String FaceRegFile(@RequestParam("file") MultipartFile file) throws Exception{
		// 转二进制
		byte[] bytes = null;
		try {
			bytes = file.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 转码
		@SuppressWarnings("restriction")
		// 注意导入import sun.misc.BASE64Encoder;
		String sendStr = new BASE64Encoder().encode(bytes);
		// Django接口地址
		String url = "http://localhost:8000/index/";
		URL u = new URL(url);
		// 打开URL连接
		URLConnection rulConnection = u.openConnection();
		HttpURLConnection httpURLConnection = (HttpURLConnection) rulConnection; 
		// 设置URL参数。注：HttpURLConnection默认setDoOutput=false，关闭向请求中写数据
		httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        // 输出流
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        // 输入流
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        
        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);
            // 向请求中写数据
            outputStreamWriter.write(sendStr.toString());
            outputStreamWriter.flush();
            // 连接返回值大于300为请求失败
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }
         //  接收响应流
         inputStream = httpURLConnection.getInputStream();
         inputStreamReader = new InputStreamReader(inputStream);
         // 读取流
         reader = new BufferedReader(inputStreamReader);
         while ((tempLine = reader.readLine()) != null) {
        	 // 将每行读取到的数据添加到StringBuffer中
             resultBuffer.append(tempLine);
         }
         System.out.println(resultBuffer.toString());
         // 将结果返回给前台
         JSONObject object = new JSONObject();
         object.put("result", resultBuffer.toString());
         return object.toJSONString();
        } finally {
            // 关关关关
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            
            if (outputStream != null) {
                outputStream.close();
            }
            
            if (reader != null) {
                reader.close();
            }
            
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
	}
}
