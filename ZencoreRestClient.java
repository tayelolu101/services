/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zenithbank.banking.ibank.zencore.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
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

/**
 *
 * @author appdev1
 */
public class ZencoreRestClient {

    private static String url;
    private static String proxyHost;
    private static String proxyPort;
    private static boolean useProxy;

    static {
        Properties ps = new Properties();
        try {
            ps.load(ZencoreService.class.getResourceAsStream("zencore.properties"));
            url = ps.getProperty("url");
            proxyHost = ps.getProperty("proxyHost");
            proxyPort = ps.getProperty("proxyPort");
            useProxy = !(ps.getProperty("useProxy", "false").equals("false")); //if use proxy exist but not false

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ZencoreRestClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ZencoreRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public <T> T send(String endPoint, String method, Map<String, String> params, Object command, final Class<T> clazz) {
        final ObjectMapper mapper = new ObjectMapper();
        String serviceUrl = url + endPoint;
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
        DefaultHttpClient httpClient = new DefaultHttpClient(cm);
        HttpUriRequest request = new HttpGet(serviceUrl);

        if (useProxy) {
            HttpHost proxy = new HttpHost(proxyHost, new Integer(proxyPort));
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }

        ResponseHandler<T> rh = new ResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response) throws IOException {
                StatusLine statusLine = response.getStatusLine();
                HttpEntity entity = response.getEntity();

                if (statusLine.getStatusCode() != 200) {
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
                System.out.println(buffer.toString());
                return (T) mapper.readValue(buffer.toString(), clazz);
                // return null;
            }
        };

        if (method.equalsIgnoreCase("POST")) {
            request = new HttpPost(serviceUrl);
            if (command != null) {
                try {
                    String stringValue = mapper.writeValueAsString(command);
                    StringEntity input = new StringEntity(stringValue,"application/json", "utf-8");
                    HttpPost post = (HttpPost) request;
                    post.setEntity(input);
                } catch (JsonProcessingException ex) {
                    Logger.getLogger(ZencoreRestClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ZencoreRestClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        request.addHeader("accept", "application/json");
        BasicHttpParams basicParams = new BasicHttpParams();
        if (params != null) {
            for (String p : params.keySet()) {
                basicParams.setParameter(p, params.get(p));
            }
            request.setParams(basicParams);
        }

        T result = null;
        try {
            result = httpClient.execute(request, rh);
        } catch (IOException ex) {
            Logger.getLogger(ZencoreRestClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }

}
