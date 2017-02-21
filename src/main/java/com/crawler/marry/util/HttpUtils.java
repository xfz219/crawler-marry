package com.crawler.marry.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HttpUtils {
    private static final Logger logger = Logger.getLogger(HttpUtils.class);
    public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:32.0) Gecko/20100101 Firefox/33.0";
    public static final String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4.4; HTC One_M8 Build/KTU84P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.93 Mobile Safari/537.36";
    public static final String UTF_8 = "UTF-8";
    public static final String GBK = "GBK";
    public static final HttpHost PROXY_FIDDLER = new HttpHost("127.0.0.1", 8888, "http");
    private static RequestConfig DEFAULT_REQUEST_CONFIG = null;
    private static int timeout = 600;
    private static int defaultMaxPerRoute = 20;

    public static HttpPost post(String url) {
        return post(url, null);
    }

    public static HttpPost post(String url, Map<String, Object> params) {
        return post(url, params, null);
    }

    public static HttpPost post(String url, Map<String, Object> params, HttpHost proxy) {
        return post(url, params, proxy, DEFAULT_USER_AGENT);
    }

    public static HttpPost post(String url, Map<String, Object> params, HttpHost proxy, String userAgent) {
        HttpPost result = new HttpPost(url);
        result.addHeader("User-Agent", userAgent == null ? DEFAULT_USER_AGENT : userAgent);
        if (params != null && !params.isEmpty()) {
            result.setEntity(buildParams(params));
        }
        result.setConfig(copyDefaultConfig().build());
        return result;
    }

    public static HttpGet get(String url) {
        return get(url, null);
    }

    public static HttpGet get(String url, Map<String, Object> params, String userAgent) {
        url += buildParamString(params);
        HttpGet result = new HttpGet(url);
        result.addHeader("User-Agent", userAgent == null ? DEFAULT_USER_AGENT : userAgent);
        result.setConfig(copyDefaultConfig().build());
        return result;
    }

    public static HttpGet get(String url, Map<String, Object> params) {
        url += buildParamString(params);
        HttpGet result = new HttpGet(url);
        result.addHeader("User-Agent", DEFAULT_USER_AGENT);
        result.setConfig(copyDefaultConfig().build());
        return result;
    }

    public static RequestConfig.Builder copyDefaultConfig() {
        return RequestConfig.copy(getDefaultRequestConfig());
    }

    public static RequestConfig getDefaultRequestConfig() {
        if (DEFAULT_REQUEST_CONFIG == null) {
            synchronized (HttpUtils.class) {
                if (DEFAULT_REQUEST_CONFIG == null) {
                    RequestConfig.Builder builder = RequestConfig.custom();
                    builder.setRedirectsEnabled(false).setRelativeRedirectsAllowed(false);
                    builder.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
                    if (Boolean.valueOf(PropertiesUtil.getProps("http.use.fiddler", "false"))) {
                        builder.setProxy(PROXY_FIDDLER);
                    }
                    // connect to a url
                    builder.setConnectTimeout(timeout);
                    // socket inputstream.read()
                    builder.setSocketTimeout(timeout * 2);
                    DEFAULT_REQUEST_CONFIG = builder.build();
                }
            }
        }
        return DEFAULT_REQUEST_CONFIG;
    }

    public static UrlEncodedFormEntity buildParams(Map<String, ? extends Object> params) {
        return buildParams(params, UTF_8);
    }

    @SuppressWarnings("rawtypes")
    public static UrlEncodedFormEntity buildParams(Map<String, ? extends Object> params, String encoding) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        for (Entry<String, ? extends Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                if (value instanceof List) {
                    for (Object o : (List) value) {
                        if (o != null) {
                            parameters.add(new BasicNameValuePair(entry.getKey(), o.toString()));
                        }
                    }
                } else {
                    parameters.add(new BasicNameValuePair(entry.getKey(), value.toString()));
                }
            } else {
                parameters.add(new BasicNameValuePair(entry.getKey(), null));
            }
        }
        return new UrlEncodedFormEntity(parameters, Charset.forName(encoding));
    }

    public static String buildParamString(Map<String, ? extends Object> params) {
        return buildParamString(params, UTF_8);
    }

    public static String buildParamString(Map<String, ? extends Object> params, String encoding) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            for (Entry<String, ? extends Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                value = value == null ? "" : value.toString();
                sb.append("&").append(URLEncoder.encode(entry.getKey(), encoding)).append("=")
                        .append(URLEncoder.encode((String) value, encoding));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static CloseableHttpClient getHttpClient() {
        return getHttpClient(false, null);
    }

    public static CloseableHttpClient getHttpClient(boolean trustAllSSL) {
        return getHttpClient(trustAllSSL, null);
    }

    public static CloseableHttpClient getHttpClient(SSLConnectionSocketFactory sslcsf, CookieStore cookieStore) {
        HttpClientBuilder builder = getBuilder();
        if (cookieStore != null) {
            builder.setDefaultCookieStore(cookieStore);
        }
        builder.setSSLSocketFactory(sslcsf);
        return builder.build();
    }

    public static CloseableHttpClient getHttpClient(boolean trustAllSSL, CookieStore cookieStore) {
        HttpClientBuilder builder = getBuilder();
        if (cookieStore != null) {
            builder.setDefaultCookieStore(cookieStore);
        }
        if (trustAllSSL) {
            builder.setSSLSocketFactory(SSLUtils.TRUAT_ALL_SSLSF);
        }
        return builder.build();
    }

    private static HttpClientBuilder getBuilder() {
        HttpClientBuilder builder = HttpClients.custom();
        builder.setMaxConnPerRoute(defaultMaxPerRoute);
        builder.setMaxConnTotal(defaultMaxPerRoute * 2);
        return builder;
    }

    public static void executeGet(CloseableHttpClient client, String url) throws ClientProtocolException, IOException {
        HttpGet get = HttpUtils.get(url);
        client.execute(get).close();
    }

    public static String executeGetWithResult(CloseableHttpClient client, String url) throws ClientProtocolException,
            IOException {
        HttpGet get = get(url);
        CloseableHttpResponse resp = client.execute(get);
        String result = EntityUtils.toString(resp.getEntity());
        resp.close();
        return result;
    }
    public static String executeGetWithResult(CloseableHttpClient client, String url, String encoding) throws ClientProtocolException,
            IOException {
        HttpGet get = get(url);
        CloseableHttpResponse resp = client.execute(get);
        String result = EntityUtils.toString(resp.getEntity(), encoding);
        resp.close();
        return result;
    }

    public static void executePost(CloseableHttpClient client, String url) throws ClientProtocolException, IOException {
        executePost(client, url, null);
    }

    public static String executePostWithResult(CloseableHttpClient client, HttpPost post)
            throws ClientProtocolException, IOException {
        CloseableHttpResponse resp = client.execute(post);
        String result = EntityUtils.toString(resp.getEntity());
        resp.close();
        return result;
    }

    public static String executePostWithResult(CloseableHttpClient client, HttpPost post, String encoding)
            throws ClientProtocolException, IOException {
        CloseableHttpResponse resp = client.execute(post);
        String result = EntityUtils.toString(resp.getEntity(), encoding);
        resp.close();
        return result;
    }

    public static String executePostWithResult(CloseableHttpClient client, String url, Map<String, Object> params)
            throws ClientProtocolException, IOException {
        return executePostWithResult(client, url, params, HttpUtils.UTF_8);
    }

    public static String executePostWithResult(CloseableHttpClient client, String url, Map<String, Object> params,
                                               String charset) throws ClientProtocolException, IOException {
        HttpPost post = params == null ? post(url) : post(url, params);
        CloseableHttpResponse resp = client.execute(post);
        String result = EntityUtils.toString(resp.getEntity(), charset);
        resp.close();
        return result;
    }

    public static void executePost(CloseableHttpClient client, String url, Map<String, Object> params)
            throws ClientProtocolException, IOException {
        HttpPost post = params == null ? post(url) : post(url, params);
        client.execute(post).close();
    }

    public static String getFirstCookie(CookieStore cookieStore, String name) {
        List<String> values = getCookie(cookieStore, name);
        return values.isEmpty() ? null : values.get(0);
    }

    public static List<String> getCookie(CookieStore cookieStore, String name) {
        List<String> result = new ArrayList<>();
        if (cookieStore == null) {
            return result;
        }
        for (Cookie cookie : cookieStore.getCookies()) {
            if (name.equals(cookie.getName())) {
                result.add(cookie.getValue());
            }
        }
        return result;
    }

    public static void printCookies(CookieStore cookieStore) {
        for (Cookie cookie : cookieStore.getCookies()) {
            System.out.println(cookie.toString());
        }
    }

    public static HttpPost buildPostFromHtml(String html) {
        return buildPostFromHtml(html, "form");
    }

    public static HttpPost buildPostFromHtml(String html, String selector) {
        return buildPostFromHtml(html, selector, HttpUtils.GBK);
    }

    public static HttpPost buildPostFromHtml(String html, String selector, String charSet) {
        Document document = Jsoup.parse(html, charSet == null ? HttpUtils.GBK : charSet);
        Elements elements = document.select(selector);
        if (elements.size() > 0) {
            Element form = elements.get(0);
            String url = form.attr("action");
            Elements inputs = form.select("input[type=hidden]");
            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < inputs.size(); i++) {
                params.put(inputs.get(i).attr("name"), inputs.get(i).attr("value"));
            }
            return HttpUtils.post(url, params);
        }
        return null;
    }

    public static Map<String, Object> getFormUrlAndParamsFromHtml(String html, String selector) {
        return getFormUrlAndParamsFromHtml(html, selector, HttpUtils.GBK);
    }

    public static Map<String, Object> getFormUrlAndParamsFromHtml(String html, String selector, String charSet) {
        Document document = Jsoup.parse(html, charSet == null ? HttpUtils.GBK : charSet);
        Elements elements = document.select(selector);
        if (elements.size() > 0) {
            Element form = elements.get(0);
            String url = form.attr("action");
            Elements inputs = form.select("input[type=hidden]");
            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < inputs.size(); i++) {
                params.put(inputs.get(i).attr("name"), inputs.get(i).attr("value"));
            }
            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("params", params);
            return result;
        }
        return null;
    }

    /**
     * 获取input[type=hidden]
     *
     * @author zhuyuhang
     * @param html
     * @return
     */
    public static Map<String, Object> buildHiddenInputParamsFromHtml(String html) {
        return buildHiddenInputParamsFromHtml(html, HttpUtils.GBK);
    }

    /**
     * 获取input[type=hidden]
     *
     * @author zhuyuhang
     * @param html
     * @param charSet
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> buildHiddenInputParamsFromHtml(String html, String charSet) {
        Document document = Jsoup.parse(html, charSet == null ? HttpUtils.GBK : charSet);
        Elements inputs = document.select("input[type=hidden]");
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < inputs.size(); i++) {
            String name = inputs.get(i).attr("name");
            String value = inputs.get(i).attr("value");
            if (params.get(name) != null) {
                Object v = params.get(name);
                if (v instanceof List) {
                    ((List<Object>) v).add(value);
                } else {
                    List<Object> l = new ArrayList<>();
                    l.add(v);
                    l.add(value);
                    params.put(name, l);
                }
            } else {
                params.put(name, value);
            }
        }
        return params;
    }

    public static Map<String, Object> buildParamsFromHtml(String html, String selector) {
        return buildParamsFromHtml(html, selector, HttpUtils.GBK);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> buildParamsFromHtml(String html, String selector, String charSet) {
        Document document = Jsoup.parse(html, charSet == null ? HttpUtils.GBK : charSet);
        Elements elements = document.select(selector);
        if (elements.size() > 0) {
            Element form = elements.get(0);
            Elements inputs = form.select("input[type=hidden]");
            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < inputs.size(); i++) {
                String name = inputs.get(i).attr("name");
                String value = inputs.get(i).attr("value");
                if (params.get(name) != null) {
                    Object v = params.get(name);
                    if (v instanceof List) {
                        ((List<Object>) v).add(value);
                    } else {
                        List<Object> l = new ArrayList<>();
                        l.add(v);
                        l.add(value);
                        params.put(name, l);
                    }
                } else {
                    params.put(name, value);
                }
            }
            return params;
        }
        return new HashMap<>();
    }

    public static String getCharsetFromContentType(String contentType) {
//        if (StringUtils.isBlank(contentType)) {
//            return null;
//        }
//        String[] cts = contentType.toLowerCase().split(";");
//        for (String s : cts) {
//            if (StringUtils.isNotBlank(s) && s.contains("charset")) {
//                return s.split("=")[1];
//            }
//        }
        return null;
    }

    /**
     * @author zhuyuhang
     * @param response
     * @param name
     * @param encode
     * @return
     */
    public static String getHeader(CloseableHttpResponse response, String name) {
        Header[] headers = response.getHeaders(name);
        if (headers.length > 0) {
            return headers[0].getValue();
        }
        return null;
    }

    /**
     * 从header里获取Location
     *
     * @author zhuyuhang
     * @param response
     * @return
     */
    public static String getLocationFromHeader(CloseableHttpResponse response) {
        return getLocationFromHeader(response, false);
    }

    /**
     * @author zhuyuhang
     * @param name
     * @param value
     * @param path
     * @param domain
     * @return
     */
    public static BasicClientCookie getCookie(String name, String value, String domain, String path) {
        BasicClientCookie clientCookie = new BasicClientCookie(name, value);
        clientCookie.setDomain(domain);
        clientCookie.setPath(path);
        return clientCookie;
    }

    public static String getLocationFromHeader(CloseableHttpResponse response, boolean closeResponse) {
        String result = getHeader(response, "Location");
        if (closeResponse) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String getLocationFromHeader(CloseableHttpClient client, String url) {
        return getLocationFromHeader(client, url, null, false);
    }

    public static String getLocationFromHeader(CloseableHttpClient client, String url, Map<String, Object> params) {
        return getLocationFromHeader(client, url, params, false);
    }

    public static String getLocationFromHeader(CloseableHttpClient client, String url, Map<String, Object> params,
                                               boolean isPost) {
        CloseableHttpResponse response;
        try {
            HttpPost request = null;
            if (isPost) {
                request = post(url, params);
                response = client.execute(request);

            } else {
                HttpGet get = get(url);
                response = client.execute(get);
            }
            return getLocationFromHeader(response, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final Pattern RE_UNICODE = Pattern.compile("\\\\u([0-9a-zA-Z]{4})");

    public static String unicodeToString(String s) {
        Matcher m = RE_UNICODE.matcher(s);
        StringBuffer sb = new StringBuffer(s.length());
        while (m.find()) {
            m.appendReplacement(sb, Character.toString((char) Integer.parseInt(m.group(1), 16)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static File getCaptchaCodeImage(CloseableHttpClient client, String url) {
        return getCaptchaCodeImage(client, HttpUtils.get(url));
    }

    public static File getCaptchaCodeImage(CloseableHttpClient client, HttpGet get) {
        logger.info("验证码图片url:" + get.getURI().toString());
        try {
            CloseableHttpResponse response = client.execute(get);
            File codeFile = new File(PropertiesUtil.getProps("captcha.dir"), System.currentTimeMillis() + ".jpg");
//            FileUtils.copyInputStreamToFile(response.getEntity().getContent(), codeFile);
            response.close();
            logger.info("获取验证码成功");
            return codeFile;
        } catch (Exception e) {
            logger.error("获取验证码图片失败", e);
        }
        return null;
    }



    //
    // private static FirefoxProfile FIREFOXPROFILE_WITHOUT_IMAGE = new
    // FirefoxProfile(
    // new File("F:/tmp/webdriver-profile"));

    /**
     * 更换URL中的特殊符号
     *
     * @author liuheli
     * @param url
     * @return
     */
    public static String replaceUrlChara(String url) {
        url = url.replace("|", "%124");
        return url;
    }

    public static String resolvePath(String src, String url) {
        try {
            return new URL(src).toURI().resolve(url).toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return url;
    }
}
