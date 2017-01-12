package com.feicui.mytreasure.treasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 宝藏仓库,用来缓存宝藏及区域数据
 */

public class TreasureRepo {

    private static TreasureRepo treasureRepo;


    private HashSet<Area> cachedAreas = new HashSet<Area>();
    private HashMap<Integer, Treasure> treasureMap = new HashMap<Integer, Treasure>();


    public static synchronized TreasureRepo getInstance() {
        if (treasureRepo == null) {
            treasureRepo = new TreasureRepo();
        }
        return treasureRepo;
    }

    private TreasureRepo() {

    }


    public void cache(Area area) {
        cachedAreas.add(area);
    }

    public boolean isCached(Area area) {
        return cachedAreas.contains(area);
    }



    public void addTreasure(List<Treasure> treasureList) {
        for (Treasure treasure : treasureList) {
            treasureMap.put(treasure.getId(), treasure);
        }
    }

    public Treasure getTreasure(int id) {
        return treasureMap.get(id);
    }

    public List<Treasure> getTreasure() {

        List<Treasure> list = new ArrayList<>();

        for (Treasure treasure : treasureMap.values()) {
            list.add(treasure);
        }
        return list;
    }

    public void clear() {
        cachedAreas.clear();
        treasureMap.clear();
    }


}
