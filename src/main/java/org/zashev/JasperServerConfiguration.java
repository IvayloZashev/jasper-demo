package org.zashev;

import java.util.Hashtable;

import com.jaspersoft.jasperserver.jaxrs.client.core.enums.JRSVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration for the jasper server instance
 */
@Component
public class JasperServerConfiguration {

    @Value("${jasper.url}")
    private String url;

    @Value("${jasper.unitBaseUri}")
    private String unitBaseUri;

    @Value("${jasper.username}")
    private String userName;

    @Value("${jasper.password}")
    private String password;

    @Value("${jasper.connectionTimeout}")
    private int connectionTimeout;

    @Value("${jasper.readTimeout}")
    private int readTimeout;

    @Value("${jasper.jasperserverVersion}")
    private String jasperServerVersion;

    @Value("${jasper.logHttp}")
    private boolean logHttp;

    @Value("${jasper.logHttpEntity}")
    private boolean logHttpEntity;

    public static Hashtable<String, String> htMediaType = new Hashtable<>();

    public JasperServerConfiguration() {

        htMediaType.put("PDF", "application/pdf");
        htMediaType.put("HTML", "application/html");
        htMediaType.put("XLS", "application/xls");
        htMediaType.put("XLSX", "application/xlsx");
        htMediaType.put("RTF", "application/rtf");
        htMediaType.put("CSV", "application/csv");
        htMediaType.put("XML", "application/xml");
        htMediaType.put("DOCX", "application/docx");
        htMediaType.put("ODT", "application/odt");
        htMediaType.put("ODS", "application/ods");
        htMediaType.put("JRPRINT", "application/jrprint");

    }

    public String getUrl() {
        return url;
    }

    public String getUnitBaseUri() {
        return unitBaseUri;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public String getJasperServerVersion() {
        return jasperServerVersion;
    }

    public boolean isLogHttp() {
        return logHttp;
    }

    public boolean isLogHttpEntity() {
        return logHttpEntity;
    }
}
