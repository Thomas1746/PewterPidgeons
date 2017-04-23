package net.pp.testengine;

import com.sun.org.apache.regexp.internal.RE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import processing.core.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.NORMAL;
import static processing.core.PConstants.QUADS;

/**
 * Created by Klimpen on 21/04/2017.
 * TODO everything
 */
@ToString
@AllArgsConstructor
public class Room implements GameObject{
    private MapManager manager;
    @Getter
    Location position;
    public static final int ROOM_SIZE = 100;
    public boolean isWall;
    @Getter
    @Setter
    private Collectible collectible = null;
    public boolean isStair;


    public Room(MapManager manager, Location loc, boolean isWall) {
        this.manager = manager;
        this.position = loc;
        this.isWall = isWall;
    }

    public void randomize() {

        //        en = new HiddenEntity(300,300,25);
    }
    @Override
    public void update() {
        if (collectible != null) collectible.update();
    }
    PImage wall;
    PImage floor;
    PImage roof;
    String[] roofImages = {"roof.png"};
    String[] wallImages = {"stone_wall.png","stone_wall_moss.png"};
    String[] floorImages = {"step_texture.png"};
    PShape rendered = null;
    @Override
    public void render(TestEngine engine, Rectangle bounds) {
        Location player = engine.player.getLocation();
        if (position.getZ() != player.getZ() && engine.manager.distanceToStair(this) > 8) {
            return;
        }
        if (rendered == null) {
            renderToShape(engine,bounds);
        }
        engine.pushMatrix();
        engine.translate(position.getX()*Room.ROOM_SIZE,(-position.getZ()*(Room.ROOM_SIZE+0.01f)),position.getY()*Room.ROOM_SIZE);
        if (collectible != null) {
            engine.pushMatrix();
            engine.translate(0,Room.ROOM_SIZE/2,0);
            engine.rotateX(PConstants.HALF_PI);
            engine.scale(2);
            Models.MINE.model.drawModel(bounds,engine);
            engine.popMatrix();
        }
        if (!isStair)
            engine.scale(Room.ROOM_SIZE/2);
        else {
            engine.translate(0,Room.ROOM_SIZE/2,0);
            engine.rotateX(PConstants.HALF_PI);
            engine.scale(5);
            Models.STAIR.model.drawModel(null,engine);
        }
        engine.shape(rendered);
        engine.popMatrix();
        if (collectible != null)
            collectible.render(engine,bounds);
    }

    private void renderToShape(TestEngine engine, Rectangle bounds) {
        rendered = engine.createShape(GROUP);
        PShape sub2 = engine.createShape();
        if (wall == null) {
            wall = loadImage(engine,wallImages[(int) engine.random(4)>=3?1:0]);
            floor = loadImage(engine,floorImages[(int) engine.random(floorImages.length)]);
            roof = loadImage(engine,roofImages[(int) engine.random(roofImages.length)]);
        }
        if (isWall) {
            sub2.beginShape(QUADS);
            sub2.texture(wall);
            sub2.textureMode(NORMAL);
            // Given one texture and six faces, we can easily set up the uv coordinates
            // such that four of the faces tile "perfectly" along either u or v, but the other
            // two faces cannot be so aligned.  This code tiles "along" u, "around" the X/Z faces
            // and fudges the Y faces - the Y faces are arbitrarily aligned such that a
            // rotation along the X axis will put the "top" of either texture at the "top"
            // of the screen, but is not otherwised aligned with the X/Z faces. (This
            // just affects what type of symmetry is required if you need seamless
            // tiling all the way around the cube)
            if (!engine.manager.isWall(position.getRelative(Direction.NORTH))) {
                // +Z "front" face
                sub2.vertex(-1, -1, 1, 0, 0);
                sub2.vertex(1, -1, 1, 1, 0);
                sub2.vertex(1, 1, 1, 1, 1);
                sub2.vertex(-1, 1, 1, 0, 1);
                sub2.normal(0, 0, 1);
            }
            if (!engine.manager.isWall(position.getRelative(Direction.SOUTH))) {
                // -Z "back" face
                sub2.vertex(1, -1, -1, 0, 0);
                sub2.vertex(-1, -1, -1, 1, 0);
                sub2.vertex(-1, 1, -1, 1, 1);
                sub2.vertex(1, 1, -1, 0, 1);
                sub2.normal(0, 0, -1);
            }
            if (!engine.manager.isWall(position.getRelative(Direction.EAST))) {
//                // +X "right" face
                sub2.vertex(1, -1, 1, 0, 0);
                sub2.vertex(1, -1, -1, 1, 0);
                sub2.vertex(1, 1, -1, 1, 1);
                sub2.vertex(1, 1, 1, 0, 1);
                sub2.normal(1, 0, 0);
            }
//
            if (!engine.manager.isWall(position.getRelative(Direction.WEST))) {
                sub2.vertex(-1, -1, -1, 0, 0);
                sub2.vertex(-1, -1, 1, 1, 0);
                sub2.vertex(-1, 1, 1, 1, 1);
                sub2.vertex(-1, 1, -1, 0, 1);
                sub2.normal(-1, 0, 0);
            }


            sub2.endShape();
        } else if (!isStair) {

            if (!manager.isStair(position.getRelative(Direction.DOWN))) {
                PShape sub = engine.createShape();
                sub.beginShape(PConstants.QUAD);
                sub.texture(floor);
                sub.textureMode(NORMAL);
                // +Y "bottom" face
                sub.vertex(-1,  1,  1, 0, 0);
                sub.vertex( 1,  1,  1, 1, 0);
                sub.vertex( 1,  1, -1, 1, 1);
                sub.vertex(-1,  1, -1, 0, 1);
                sub.endShape();
                rendered.addChild(sub);
            }
            sub2.beginShape(PConstants.QUAD);
            sub2.texture(roof);
            sub2.textureMode(NORMAL);
            // -Y "top" face
            sub2.vertex(-1, -1, -1, 0, 0);
            sub2.vertex( 1, -1, -1, 1, 0);
            sub2.vertex( 1, -1,  1, 1, 1);
            sub2.vertex(-1, -1,  1, 0, 1);
            sub2.endShape();
        }
        rendered.addChild(sub2);
    }
    private static HashMap<String,PImage> imageHashMap = new HashMap<>();
    private static PImage loadImage(TestEngine engine, String image) {
        if (imageHashMap.containsKey(image)) return  imageHashMap.get(image);
        PImage image3 = engine.loadImage(image);
        imageHashMap.put(image,image3);
        return image3;
    }

    public boolean isSolid() {
        return isWall || isStair;
    }

    public void setPowerup(int type) {
        Collectible c = new Powerup(this.position,type);
        this.collectible = c;
        manager.getCollectibles().add(c);
    }
    public void setMine() {
        this.collectible = new Mine(position);
        manager.getCollectibles().add(collectible);
    }
}
