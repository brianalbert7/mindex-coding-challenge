package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl ;
    private String employeeCompensationUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        employeeCompensationUrl = "http://localhost:" + port + "/compensation/{employeeId}";
    }

    @Test
    public void testCreateReadUpdate() {
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        Compensation testCompensation = new Compensation();
        testCompensation.setEmployeeId(employeeId);
        testCompensation.setSalary(100000.00);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse("2025-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        testCompensation.setEffectiveDate(date);

        // Create checks
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

        assertNotNull(createdCompensation);
        assertEquals(testCompensation.getSalary(), createdCompensation.getSalary(), 0.01);
        assertEquals(testCompensation.getEffectiveDate(), createdCompensation.getEffectiveDate());

        // Read checks
        Compensation readCompensation = restTemplate.getForEntity(employeeCompensationUrl, Compensation.class, createdCompensation.getEmployeeId()).getBody();
        assertNotNull(readCompensation);

        assertEquals(createdCompensation.getSalary(), readCompensation.getSalary(), 0.01);
        assertEquals(createdCompensation.getEffectiveDate(), readCompensation.getEffectiveDate());

        // Update checks
        readCompensation.setSalary(120000.00);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Compensation updatedCompensation =
                restTemplate.exchange(employeeCompensationUrl,
                    HttpMethod.PUT,
                    new HttpEntity<>(readCompensation, headers),
                    Compensation.class,
                    employeeId).getBody();

        assertCompensationEquivalence(readCompensation, updatedCompensation);
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getSalary(), actual.getSalary(), 0.01);
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
    }
}
