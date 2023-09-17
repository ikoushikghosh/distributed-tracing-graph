package org.koushik.distributed.tracing.model;

public class ConnectionDetails {
    private String sourceService;
    private String destService;
    private Integer latency;

    public ConnectionDetails() {}
    public ConnectionDetails(String sourceService, String destService, Integer latency) {
        this.sourceService = sourceService;
        this.destService = destService;
        this.latency = latency;
    }

    public String getSourceService() {
        return sourceService;
    }

    public void setSourceService(String sourceService) {
        this.sourceService = sourceService;
    }

    public String getDestService() {
        return destService;
    }

    public void setDestService(String destService) {
        this.destService = destService;
    }

    public Integer getLatency() {
        return latency;
    }

    public void setLatency(Integer latency) {
        this.latency = latency;
    }

    @Override
    public String toString() {
        return "ConnectionDetails {" +
                "sourceService='" + sourceService + '\'' +
                ", destService='" + destService + '\'' +
                ", latency=" + latency +
                '}';
    }
}
