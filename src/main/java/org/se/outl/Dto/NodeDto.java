package org.se.outl.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.se.outl.Entity.Node;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeDto extends Node {
    private int seqCnt;
}
