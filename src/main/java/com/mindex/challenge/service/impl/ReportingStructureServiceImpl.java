package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure getReportingStructure(String id) {
        LOG.debug("Getting reporting structure for employee with id [{}]", id);

        Employee employee = employeeService.read(id);
        if (employee == null) {
            throw new RuntimeException("Employee not found with id: " + id);
        }

        int numberOfReports = countReports(employee, new HashSet<>());
        return new ReportingStructure(employee, numberOfReports);
    }

    /**
     * Recursively calculates the total number of reports under a given employee.
     * This includes all direct reports as well as their respective reports,
     * ensuring that each employee is counted only once.
     *
     * @param employee The employee whose total number of reports is being calculated.
     * @param visited  A set of visited employee IDs to track processed employees.
     * @return The total number of reports under the employee (0 if they have no direct reports).
     */
    private int countReports(Employee employee, Set<String> visited) {
        if (employee.getDirectReports() == null || employee.getDirectReports().isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Employee directReport : employee.getDirectReports()) {
            if (visited.add(directReport.getEmployeeId())) {
                LOG.debug("Counting reports for direct report [{}]", directReport.getEmployeeId());
                Employee fullEmployee = employeeService.read(directReport.getEmployeeId());
                count += 1 + countReports(fullEmployee, visited);
            }
        }
        return count;
    }
}
