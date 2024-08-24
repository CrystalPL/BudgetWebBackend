package pl.crystalek.budgetweb.auth.device;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ua_parser.Client;
import ua_parser.Parser;

@UtilityClass
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DeviceUtil {
    Parser PARSER = new Parser();

    public DeviceInfo getDeviceInfo(final String UAHeader) {
        //TODO sprawdzanie czy uaheader w ogóle jest

        final Client client = PARSER.parse(UAHeader);
        final String browserName = client.userAgent.family;
        final String OSName = client.os.family + " " + client.os.major;

        return new DeviceInfo(OSName, browserName);
    }
}
