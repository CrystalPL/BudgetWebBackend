package pl.crystalek.budgetweb.auth.device;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceUtilTest {

    private static Stream<Arguments> provideUserAgentsForDeviceInfoTest() {
        return Stream.of(
                Arguments.of(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
                        "Chrome",
                        "Windows 10"
                ),
                Arguments.of(
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15",
                        "Safari",
                        "Mac OS X 10"
                ),
                Arguments.of(
                        "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0",
                        "Firefox",
                        "Ubuntu"
                ),
                Arguments.of(
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1",
                        "Mobile Safari",
                        "iOS 14"
                ),
                Arguments.of(
                        "Mozilla/5.0 (Linux; Android 11; SM-G998B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36",
                        "Chrome Mobile",
                        "Android 11"
                ),
                Arguments.of(
                        "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko",
                        "IE",
                        "Windows 10"
                )
        );
    }

    private static Stream<Arguments> provideInvalidUserAgents() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("   "),
                Arguments.of("invalid-user-agent"),
                Arguments.of("random-string-not-a-user-agent")
        );
    }

    @ParameterizedTest
    @MethodSource("provideUserAgentsForDeviceInfoTest")
    void shouldCorrectlyParseVariousUserAgents(String userAgent, String expectedBrowser, String expectedOsPrefix) {
        DeviceInfo deviceInfo = DeviceUtil.getDeviceInfo(userAgent);

        assertThat(deviceInfo).isNotNull();
        assertThat(deviceInfo.browserName()).isEqualTo(expectedBrowser);
        assertThat(deviceInfo.OSName()).startsWith(expectedOsPrefix);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserAgents")
    void shouldHandleInvalidUserAgents(String invalidUserAgent) {
        DeviceInfo deviceInfo = DeviceUtil.getDeviceInfo(invalidUserAgent);

        assertThat(deviceInfo).isNotNull();
        assertThat(deviceInfo.browserName()).isNotNull();
        assertThat(deviceInfo.OSName()).isNotNull();
    }

}