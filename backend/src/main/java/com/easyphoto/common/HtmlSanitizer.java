package com.easyphoto.common;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * 富文本 HTML 清洗，防 XSS。允许基础排版标签与图片（含相对路径图片）。
 */
public final class HtmlSanitizer {

    private HtmlSanitizer() {
    }

    // 基于 relaxed() 白名单：允许常见排版标签。
    // 对 img src 移除协议限制，以保留 /uploads/xxx.jpg 这类相对路径；
    // script、onerror 等危险标签/属性仍被剔除。
    private static final Safelist SAFELIST = Safelist.relaxed()
            .addTags("hr")
            .addAttributes("img", "src", "alt", "width", "height", "style")
            .addAttributes("span", "style")
            .addAttributes("p", "style")
            .removeProtocols("img", "src", "http", "https");

    public static String sanitize(String html) {
        if (html == null) {
            return "";
        }
        return Jsoup.clean(html, SAFELIST);
    }
}
