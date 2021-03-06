package org.zashev;

import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.ReportOutputFormat;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.RunReportAdapter;
import com.jaspersoft.jasperserver.jaxrs.client.core.JasperserverRestClient;
import com.jaspersoft.jasperserver.jaxrs.client.core.RestClientConfiguration;
import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.JRSVersion;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.ResourceNotFoundException;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.ProcessingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Service class for exposing jasper reports server report units
 */
@RestController
public class JasperService {

    @Autowired
    JasperServerConfiguration jasperReportServerConfiguration;

    private static final String HEADER_NAME = "Content-Disposition";
    private static final String HEADER_VALUE = "attachment; filename=";

    /**
     * Gets report unit with the specified file format
     *
     * @param reportName         report name
     * @param reportOutputFormat report file format
     * @param reportParams       report parameters
     * @return response entity
     */
    @RequestMapping(value = "/getReport/{reportName}/{reportOutputFormat}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getReport(@PathVariable String reportName,
                                                         @PathVariable ReportOutputFormat reportOutputFormat,
                                                         @RequestParam Map<String, String> reportParams) {

        RestClientConfiguration configuration = new RestClientConfiguration(jasperReportServerConfiguration.getUrl());
        configuration.setConnectionTimeout(jasperReportServerConfiguration.getConnectionTimeout());
        configuration.setReadTimeout(jasperReportServerConfiguration.getReadTimeout());
        configuration.setJrsVersion(JRSVersion.valueOf(jasperReportServerConfiguration.getJasperServerVersion()));
        configuration.setLogHttp(jasperReportServerConfiguration.isLogHttp());
        JasperserverRestClient client = new JasperserverRestClient(configuration);

        Session session;

        try {

            session = client.authenticate(jasperReportServerConfiguration.getUserName(),
                    jasperReportServerConfiguration.getPassword());
        } catch (ProcessingException connectException) {
            return new ResponseEntity(connectException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ReportExecutionRequest request = new ReportExecutionRequest();
        request.setReportUnitUri(jasperReportServerConfiguration.getUnitBaseUri() + reportName);
        request.setAsync(true).setOutputFormat(reportOutputFormat);

        RunReportAdapter adapter = session
                .reportingService()
                .report(request.getReportUnitUri())
                .prepareForRun(reportOutputFormat);

        if (reportParams.size() != 0) {
            for (Map.Entry<String, String> m : reportParams.entrySet()) {
                    adapter.parameter(m.getKey(), m.getValue());
            }
        }

        try {

            OperationResult<InputStream> operationResult = adapter.run();

            InputStreamResource inputStreamResource = new InputStreamResource((InputStream) operationResult.getResponse().getEntity());

            return ResponseEntity
                    .ok().header(HEADER_NAME, HEADER_VALUE + reportName + "." + reportOutputFormat.toString().toLowerCase())
                    .contentLength(operationResult.getResponse().getLength())
                    .contentType(MediaType.parseMediaType(JasperServerConfiguration.htMediaType.get(reportOutputFormat.toString())))
                    .body(new InputStreamResource(inputStreamResource.getInputStream()));
        } catch (ResourceNotFoundException e) {

            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (IOException e) {

            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
