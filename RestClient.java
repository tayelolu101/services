/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zenithbank.banking.remitta.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;


/**
 *
 * @author appdev1
 */
public class RestClient{
    
    private static Integer maxAutoRetryCount;
    private static Integer connectionTimeout;
    private static Integer readTimeout;
    
    private static String proxyHost;
    private static String proxyPort;
    private static boolean useProxy;
    
    static {
        Properties ps = new Properties();
        try {
            ps.load(RemittaService.class.getResourceAsStream("application.properties"));
            connectionTimeout = Integer.parseInt(ps.getProperty("remitta.connectionTimeout"));
            readTimeout = Integer.parseInt(ps.getProperty("remitta.readTimeout"));
            maxAutoRetryCount =  Integer.parseInt(ps.getProperty("remitta.maxAutoRetryCount"));  
            
            proxyHost = ps.getProperty("proxyHost");
            proxyPort = ps.getProperty("proxyPort");
            useProxy = !(ps.getProperty("useProxy", "false").equals("false")); 
            
            } catch (IOException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public <T> T send(String url,String method, Map<String, String> params, Object command, final Class<T> clazz) {
        final ObjectMapper mapper = new ObjectMapper();
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
        DefaultHttpClient httpClient = new DefaultHttpClient(cm);
        HttpUriRequest request = new HttpGet(url);
 
         if (useProxy) {
    
            HttpHost proxy = new HttpHost(proxyHost, new Integer(proxyPort));     
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }

        if(connectionTimeout >0 ){
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);
        }
        if(readTimeout >0 ){
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, readTimeout);
        }
        if(maxAutoRetryCount>0){
        httpClient.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext hc) {
                   if (executionCount >= maxAutoRetryCount) {
                        // Do not retry if over max retry count
                        return false;
                    }
                    if (exception instanceof InterruptedIOException) {
                        // Timeout
                        return false;
                    }
                    if (exception instanceof UnknownHostException) {
                        // Unknown host
                        return false;
                    }
                    if (exception instanceof SSLException) {
                        // SSL handshake exception
                        return false;
                    }
                    return true;
            }
        });
        }
        
        ResponseHandler<T> rh = new ResponseHandler<T>() {

                @Override
                public T handleResponse(final HttpResponse response) throws IOException {
                    StatusLine statusLine = response.getStatusLine();
                    HttpEntity entity = response.getEntity();
            
                    if (statusLine.getStatusCode() >= 300) {
                        throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
                    }
                    if (entity == null) {
                        throw new ClientProtocolException("Response contains no content");
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
                    String output;
                    StringBuilder buffer = new StringBuilder();
                    while ((output = br.readLine()) != null) {
                        buffer.append(output);
                    }
                    output = buffer.toString().replace("jsonp(", "").replace(")", "");
                  System.out.println("Return from action : "+buffer.toString());
                    return (T) mapper.readValue(output, clazz);
                    // return null;
                }
            };

        if(method.equalsIgnoreCase("POST")){
               request = new HttpPost(url);
               if(command != null)
               {
            try {
               String stringValue = mapper.writeValueAsString(command);
               StringEntity input = new StringEntity(stringValue,"utf-8","application/json");
               HttpPost post = (HttpPost)request;
               post.setEntity(input);
            } catch (JsonProcessingException ex) {
                Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (UnsupportedEncodingException ex) {
                Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
           
        }
        request.addHeader("accept", "application/json");
        BasicHttpParams basicParams = new BasicHttpParams();
        if(params != null){
        for(String p : params.keySet()){
         basicParams.setParameter(p, params.get(p));
        }
        request.setParams(basicParams);
        }
        
        T result=null;
       try {
            result = httpClient.execute(request, rh);
        } catch (IOException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        httpClient.getConnectionManager().shutdown();
        return result;
    }
    
}
