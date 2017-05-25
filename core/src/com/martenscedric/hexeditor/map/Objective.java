package com.martenscedric.hexeditor.map;


import com.cedricmartens.hexmap.hexagon.Hexagon;
import com.cedricmartens.hexmap.map.HexMap;
import com.martenscedric.hexeditor.tile.BuildingType;
import com.martenscedric.hexeditor.tile.TileData;

import static com.martenscedric.hexeditor.misc.Const.BUILDING_COUNT;

/**
 * Created by 1544256 on 2017-05-10.
 */
public class Objective
{
    private int minScore;
    private int[] buildingRequirement;

    public Objective() {
    }

    public Objective(int[] buildingRequirement, int minScore) {

        if(buildingRequirement.length != BUILDING_COUNT)
            throw new IllegalArgumentException();

        this.buildingRequirement = buildingRequirement;
        this.minScore = minScore;
    }

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(Integer minScore) {
        this.minScore = minScore;
    }

    public int[] getBuildingRequirement() {
        return buildingRequirement;
    }

    public void setBuildingRequirement(int[] buildingRequirement) {
        this.buildingRequirement = buildingRequirement;
    }

    public boolean hasPassed(HexMap<TileData> grid)
    {
        int score = 0;
        int[] buildings = new int[BUILDING_COUNT];
        for(int i = 0; i < grid.getHexs().length; i++)
        {
            Hexagon<TileData> hex = grid.getHexs()[i];

            if(hex.getHexData().getBuildingType() != BuildingType.NONE)
            {
                score += hex.getHexData().getBuildingType().getScore() * hex.getHexData().getTileType().getMultiplier();
                buildings[hex.getHexData().getBuildingType().ordinal() - 1]++;
            }
        }

        for(int i = 0; i < BUILDING_COUNT; i++)
        {
            if(buildings[i] < buildingRequirement[i])
                return false;
        }

        return score >= minScore;
    }

    @Override
    public String toString() {

        String s = "";

        if(minScore != Integer.MIN_VALUE)
        {
            s+= String.format("Have a score of atleast %d", minScore);
        }

        for(int i = 0; i < BUILDING_COUNT; i++)
        {
            if(buildingRequirement[i] > 0)
            {
                if(!s.equals(""))
                    s+="\n";
                s+= String.format("Have atleast %d %s", buildingRequirement[i], BuildingType.values()[i + 1].getName().toLowerCase());
            }
        }

        return s;
    }
}