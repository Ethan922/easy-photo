package com.easyphoto.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HtmlSanitizerTest {

    @Test
    void stripsScriptTags() {
        String dirty = "<p>hi</p><script>alert(1)</script>";
        String clean = HtmlSanitizer.sanitize(dirty);
        assertFalse(clean.contains("<script"));
        assertTrue(clean.contains("<p>hi</p>"));
    }

    @Test
    void stripsEventHandlers() {
        String dirty = "<img src=\"http://x/y.jpg\" onerror=\"alert(1)\">";
        String clean = HtmlSanitizer.sanitize(dirty);
        assertFalse(clean.contains("onerror"));
    }

    @Test
    void keepsRelativeImageSrc() {
        String dirty = "<img src=\"/uploads/2026/07/x.jpg\">";
        String clean = HtmlSanitizer.sanitize(dirty);
        assertTrue(clean.contains("/uploads/2026/07/x.jpg"),
                "relative image url should be preserved, got: " + clean);
    }

    @Test
    void nullBecomesEmpty() {
        assertEquals("", HtmlSanitizer.sanitize(null));
    }
}
