package net.pp.testengine;

import lombok.Getter;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by Klimpen on 21/04/2017.
 * TODO everything
 *
 */
public class MapManager implements GameObject{
    private TestEngine te;
    @Getter
    private HashMap<Location,Room> roomMap = new HashMap<>();
    private int xSize;
    private int ySize;
    private int zSize;
    @Getter
    private Location startLoc;
    PGraphics offscreen;

    public MapManager(TestEngine te, int xSize, int ySize, int zSize, PGraphics offscrean){
        this.te = te;
        this.offscreen = offscrean;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        MapGenerator mg = new MapGenerator(te, this, roomMap, xSize, ySize, zSize);
        startLoc = new Location(
                // makes it so we're not on the
                (int)(te.random(1,xSize)),
                (int)(te.random(1,ySize)),
                0);

        mg.createMap(startLoc);
    }

    @Override
    public void update() {
        roomMap.values().forEach(Room::update);
    }

    public void render(TestEngine engine, Rectangle blueBounds){
        HashMap<Color,Room> colorMap = new HashMap<>();
        offscreen.beginDraw();
        offscreen.clear();
        engine.player.offscreenTransform(engine);
        roomMap.values().forEach(r -> {
            r.render(engine, blueBounds);
            Color c = r.renderOffscreen(engine,offscreen);
            if (c != null)
                colorMap.put(c,r);
        });
        offscreen.endDraw();
        engine.selected = colorMap.get(new Color(offscreen.get(engine.mouseX,engine.mouseY)));
    }

    public boolean isWall(Location location) {
        return roomMap.containsKey(location) && roomMap.get(location).isWall;
    }

    public boolean isStair(Location location) {
        return roomMap.containsKey(location) && roomMap.get(location).isStair;
    }
}
