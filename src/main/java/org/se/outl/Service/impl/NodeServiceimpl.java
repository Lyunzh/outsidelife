package org.se.outl.Service.impl;

import org.se.outl.Dto.NodeDto;
import org.se.outl.Entity.Node;
import org.se.outl.Mapper.NodeMapper;
import org.se.outl.Service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeServiceimpl implements NodeService {


    @Autowired
    private NodeMapper nodeMapper;
    @Override
    public List<NodeDto> getNodes(int routeId)
    {
        return nodeMapper.getNodes(routeId);
    }
}
