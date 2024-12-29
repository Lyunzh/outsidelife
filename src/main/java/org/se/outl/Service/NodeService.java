package org.se.outl.Service;

import org.se.outl.Dto.NodeDto;
import org.se.outl.Entity.Node;

import java.util.List;

public interface NodeService {
    List<NodeDto> getNodes(int routeId);
}
