package tech.muyi.util.request;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Cookie 读写工具。
 *
 * <p>默认行为：
 * <ul>
 *   <li>path 固定为 "/"，覆盖整个站点路径。</li>
 *   <li>cookieMaxage <= 0 时不主动设置 Max-Age，通常表现为会话级 cookie。</li>
 *   <li>根据 request URL 推导 domain，localhost 场景不设置 domain。</li>
 * </ul>
 *
 * <p>注意：该工具未统一设置 HttpOnly/Secure/SameSite，安全属性需在调用侧或网关层补齐。</p>
 *
 * @author: muyi
 * @date: 2022/11/20
 **/
@Slf4j
public class CookieUtils {

    /**
     * 得到Cookie的值（不解码)
     *
     * @param request    请求
     * @param cookieName Cookie名称
     * @return Cookie
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        return getCookieValue(request, cookieName, false);
    }

    /**
     * 得到Cookie的值
     *
     * @param request    请求
     * @param cookieName Cookie名称
     * @param isDecoder  是否解码
     * @return Cookie
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, boolean isDecoder) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (Cookie cookie : cookieList) {
                if (cookie.getName().equals(cookieName)) {
                    if (isDecoder) {
                        // 历史兼容：默认 UTF-8 解码。
                        retValue = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } else {
                        retValue = cookie.getValue();
                    }
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Cookie解码失败: cookieName={}", cookieName, e);
        }
        return retValue;
    }

    /**
     * 得到Cookie的值
     *
     * @param request      请求
     * @param cookieName   Cookie名称
     * @param encodeString 编码格式
     * @return Cookie
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, String encodeString) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (Cookie cookie : cookieList) {
                if (cookie.getName().equals(cookieName)) {
                    retValue = URLDecoder.decode(cookie.getValue(), encodeString);
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Cookie解码失败: cookieName={}", cookieName, e);
        }
        return retValue;
    }

    /**
     * 设置Cookie的值 不设置生效时间默认浏览器关闭即失效,也不编码
     *
     * @param request     请求
     * @param response    响应
     * @param cookieName  Cookie名称
     * @param cookieValue Cookie值
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue) {
        setCookie(request, response, cookieName, cookieValue, -1);
    }

    /**
     * 设置Cookie的值 在指定时间内生效,但不编码
     *
     * @param request      请求
     * @param response     响应
     * @param cookieName   Cookie名称
     * @param cookieValue  Cookie值
     * @param cookieMaxage cookie生效的最大秒数
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage) {
        setCookie(request, response, cookieName, cookieValue, cookieMaxage, false);
    }

    /**
     * 设置Cookie的值 不设置生效时间
     *
     * @param request     请求
     * @param response    响应
     * @param cookieName  Cookie名称
     * @param cookieValue Cookie值
     * @param isEncode    是否编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, boolean isEncode) {
        setCookie(request, response, cookieName, cookieValue, -1, isEncode);
    }

    /**
     * 设置Cookie的值 在指定时间内生效, 编码参数
     *
     * @param request      请求
     * @param response     响应
     * @param cookieName   Cookie名称
     * @param cookieValue  Cookie值
     * @param cookieMaxage cookie生效的最大秒数
     * @param isEncode     是否编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage, boolean isEncode) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxage, isEncode);
    }

    /**
     * 设置Cookie的值 在指定时间内生效, 编码参数(指定编码)
     *
     * @param request      请求
     * @param response     响应
     * @param cookieName   Cookie名称
     * @param cookieValue  Cookie值
     * @param cookieMaxage cookie生效的最大秒数
     * @param encodeString 编码格式
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage, String encodeString) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxage, encodeString);
    }

    /**
     * 删除Cookie带cookie域名
     *
     * @param request    请求
     * @param response   响应
     * @param cookieName Cookie名称
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        // 延续现有行为：写空值并沿用默认过期策略，不显式置 0。
        doSetCookie(request, response, cookieName, "", -1, false);
    }

    /**
     * 设置Cookie的值，并使其在指定时间内生效
     *
     * @param request      请求
     * @param response     响应
     * @param cookieName   Cookie名称
     * @param cookieValue  Cookie值
     * @param cookieMaxage cookie生效的最大秒数
     * @param isEncode     是否编码
     */
    private static void doSetCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage, boolean isEncode) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else if (isEncode) {
                cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxage > 0) {
                cookie.setMaxAge(cookieMaxage);
            }
            if (null != request) {
                // 设置域名的cookie
                String domainName = getDomainName(request);
                if (!"localhost".equals(domainName)) {
                    // 跨子域共享 cookie（例如 a.example.com / b.example.com）。
                    cookie.setDomain(domainName);
                }
            }
            cookie.setPath("/");
            // 设置安全属性
            cookie.setHttpOnly(true);
            // 注意: Secure 属性应该根据实际环境判断，HTTPS 环境下才设置
            // cookie.setSecure(request.isSecure());
            response.addCookie(cookie);
        } catch (Exception e) {
            log.error("设置Cookie失败: cookieName={}", cookieName, e);
        }
    }

    /**
     * 设置Cookie的值，并使其在指定时间内生效
     *
     * @param request      请求
     * @param response     响应
     * @param cookieName   Cookie名称
     * @param cookieValue  Cookie值
     * @param cookieMaxage cookie生效的最大秒数
     * @param encodeString 编码格式
     */
    private static void doSetCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage, String encodeString) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else {
                cookieValue = URLEncoder.encode(cookieValue, encodeString);
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxage > 0) {
                cookie.setMaxAge(cookieMaxage);
            }
            if (null != request) {
                // 设置域名的cookie
                String domainName = getDomainName(request);
                if (!"localhost".equals(domainName)) {
                    cookie.setDomain(domainName);
                }
            }
            cookie.setPath("/");
            // 设置安全属性
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        } catch (Exception e) {
            log.error("设置Cookie失败: cookieName={}", cookieName, e);
        }
    }

    /**
     * 得到cookie的域名
     *
     * @param request 请求
     * @return 域名
     */
    private static String getDomainName(HttpServletRequest request) {
        String domainName = null;
        String serverName = request.getRequestURL().toString();
        if (serverName.equals("")) {
            domainName = "";
        } else {
            serverName = serverName.toLowerCase();
            // 移除协议前缀（http:// 或 https://）
            if (serverName.startsWith("https://")) {
                serverName = serverName.substring(8);
            } else if (serverName.startsWith("http://")) {
                serverName = serverName.substring(7);
            }
            final int end = serverName.indexOf("/");
            serverName = serverName.substring(0, end);
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3) {
                // www.xxx.com.cn
                domainName = "." + domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
            } else if (len <= 3 && len > 1) {
                // xxx.com or xxx.cn
                domainName = "." + domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }

        if (domainName.indexOf(":") > 0) {
            String[] ary = domainName.split("\\:");
            domainName = ary[0];
        }
        return domainName;
    }
}