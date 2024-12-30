package org.se.outl.Service.impl;


import org.se.outl.Mapper.SpotMapper;
import org.se.outl.Service.SpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.se.outl.Entity.Spot;

import java.util.List;

@Service
public class SpotServiceimpl implements SpotService {
    @Autowired
    private SpotMapper spotMapper;

    @Override
    public List<Spot> getRecentSpot()
    {
        return spotMapper.getRecentSpot();
    }

    @Override
    public List<Spot> getBikeSpot(){
        return spotMapper.getBikeSpot();
    }

    @Override
    public List<Spot> getHikeSpot()
    {
        return spotMapper.getHikeSpot();
    }

    @Override
    public  Spot getSpotById(int spotId)
    {
        return spotMapper.getSpotById(spotId);
    }

}
