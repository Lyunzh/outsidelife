package org.se.outl.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Spot {
    private int spotId;
    private String spotName;
    private String description;
    private String imageUrl;
    private String location;
    private String type;
}
