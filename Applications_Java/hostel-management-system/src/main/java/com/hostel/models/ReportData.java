package com.hostel.models;

import java.util.HashMap;
import java.util.Map;

public class ReportData {
    private String reportName;
    private Map<String, String> data = new HashMap<>();

    public ReportData(String reportName) {
        this.reportName = reportName;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void addData(String key, String value) {
        this.data.put(key, value);
    }
}
