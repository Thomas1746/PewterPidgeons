package net.pp.testengine;

import lombok.Getter;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by Klimpen on 21/04/2017.
 * TODO everything
 *
 */
public class MapManager implements GameObject{
    @Getter
    private HashMap<Location,Room> roomMap = new HashMap<>();
    private int xSize;
    private int ySize;
    private int zSize;
    @Getter
    private Location startLoc;

    public MapManager(int xSize, int ySize, int zSize){
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        MapGenerator mg = new MapGenerator(this, roomMap, xSize, ySize, zSize);
        startLoc = new Location(
                // makes it so we're not on the
                Math.max(1,(int)(Math.random()*xSize)),
                Math.max(1,(int)(Math.random()*ySize)),0);

        mg.createMap(startLoc);
    }

    @Override
    public void update() {
        roomMap.values().forEach(Room::update);
    }

    public void render(TestEngine engine, Rectangle blueBounds){
        roomMap.values().forEach(r -> r.render(engine, blueBounds));
    }

    public boolean isWall(Location location) {
            return roomMap.containsKey(location) && roomMap.get(location).isWall;
    }

    public boolean isStair(Location location) {
        return roomMap.containsKey(location) && roomMap.get(location).isStair;
    }
}
