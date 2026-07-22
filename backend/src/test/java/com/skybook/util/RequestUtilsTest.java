package com.skybook.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestUtilsTest {

    @Test
    void parsesBrowserAndOs() {
        String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        assertThat(RequestUtils.browser(ua)).isEqualTo("Chrome");
        assertThat(RequestUtils.operatingSystem(ua)).isEqualTo("macOS");
    }

    @Test
    void sha256IsStable() {
        assertThat(IdUtils.sha256("abc")).isEqualTo(IdUtils.sha256("abc"));
        assertThat(IdUtils.bookingReference()).startsWith("SBK").hasSize(8);
    }
}
