package org.koushik.distributed.tracing.model;

import java.util.List;
import java.util.Set;

public record AllConnections(Set<String> services, List<ConnectionDetails> allConnections) {
}
