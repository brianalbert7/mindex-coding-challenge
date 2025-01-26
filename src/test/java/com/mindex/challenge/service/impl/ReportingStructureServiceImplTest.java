package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureUrl;
    private String employeeIdUrl;

    @Autowired
    private ReportingStructureService reportingStructureService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/reporting-structure";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
    }

    @Test
    public void testGetReportingStructure() {
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        // Perform GET request for reporting structure
        ReportingStructure reportingStructure = restTemplate.getForObject(reportingStructureUrl + "/{employeeId}", ReportingStructure.class, employeeId);

        // Assert reporting structure and employee are not null
        assertNotNull(reportingStructure);
        assertNotNull(reportingStructure.getEmployee());

        // Assert employee data is correct
        assertEquals(employeeId, reportingStructure.getEmployee().getEmployeeId());
        assertEquals("John", reportingStructure.getEmployee().getFirstName());
        assertEquals("Lennon", reportingStructure.getEmployee().getLastName());
        assertEquals("Development Manager", reportingStructure.getEmployee().getPosition());
        assertEquals("Engineering", reportingStructure.getEmployee().getDepartment());

        // Assert total number of reports (direct + indirect)
        // John Lennon has 4 total reports (Paul, Ringo, Pete, George)
        assertEquals(4, reportingStructure.getNumberOfReports());
    }
}
