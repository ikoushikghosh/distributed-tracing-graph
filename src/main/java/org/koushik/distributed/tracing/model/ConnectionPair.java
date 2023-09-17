package org.koushik.distributed.tracing.model;

public record ConnectionPair(String serviceNode, Integer latency) {
}
