package org.se.outl.Service;

import org.se.outl.Entity.Spot;

import java.util.List;

public interface SpotService {
    List<Spot> getRecentSpot();
    List<Spot> getHikeSpot();
    List<Spot> getBikeSpot();
    Spot getSpotById(int id);
}
