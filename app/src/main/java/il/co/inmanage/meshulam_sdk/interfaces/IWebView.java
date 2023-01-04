package il.co.inmanage.meshulam_sdk.interfaces;

import org.apache.http.cookie.Cookie;

import java.util.List;

public interface IWebView {

    List<Cookie> getCocCookieList();

    String getBaseHostUrl();
}
