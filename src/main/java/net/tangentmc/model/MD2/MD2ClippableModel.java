package net.tangentmc.model.MD2;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import net.pp.testengine.TestEngine;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
public class MD2ClippableModel {
    private Header header;
    public Frame[] frames;
    private float[] glcmds;
    private PImage texture;
    private PApplet applet;
    short[][] uvs;
    short[][] tris;
    private Animation animation = new Animation(1, 0, 1.0F, 1.0F);

    MD2ClippableModel(Header header, Frame[] frames, float[] glCmds, PImage texture, PApplet applet, short[][] uvs, short[][] tris) {
        this.header = header;
        this.frames = frames;
        this.glcmds = glCmds;
        this.texture = texture;
        this.applet = applet;
        this.uvs = uvs;
        this.tris = tris;
    }

    public void setAnimation(Animation animation, float transitionSpeed) {
        if(this.animation != null && transitionSpeed < 1.0F) {
            this.animation = new TweenAnimation(this.animation, Animation.fromAnimation(animation), transitionSpeed);
        } else {
            this.animation = animation;
        }
    }

    public Animation[] getPregeneratedAnimations() {
        List<Animation> animationList = new ArrayList<>();
        String lastPrefix = this.frames[0].getName().replaceAll("[^\\D.]", "").replace("\u0000", "");
        int firstFrame = 0;
        int lastFrame = 0;

        for(int i = 0; i < this.frames.length; ++i) {
            String prefix = this.frames[i].getName().replaceAll("[^\\D.]", "").replace("\u0000", "");
            if(prefix.equals(lastPrefix)) {
                lastFrame = i;
            } else {
                animationList.add(new Animation(firstFrame, lastFrame, 0, 0.0F, 1.0F, 1.0F));
                lastFrame = i;
                firstFrame = i;
            }
        }

        animationList.add(new Animation(firstFrame, lastFrame, 0, 0.0F, 1.0F, 1.0F));
        return (Animation[])animationList.toArray(new Animation[animationList.size()]);
    }
    public void drawModel(Rectangle clipBounds, PShape engine) {
        if(this.animation instanceof TweenAnimation && ((TweenAnimation)this.animation).isIntermediate()) {
            this.animation = ((TweenAnimation)this.animation).getNext();
        }

        this.applet.textureMode(1);
        this.renderFrame(clipBounds,engine);
        this.animation.nextFrame();
    }
    public void drawModel(Rectangle clipBounds, TestEngine engine) {
        if(this.animation instanceof TweenAnimation && ((TweenAnimation)this.animation).isIntermediate()) {
            this.animation = ((TweenAnimation)this.animation).getNext();
        }

        this.applet.textureMode(1);
        this.renderFrame(clipBounds,engine);
        this.animation.nextFrame();
    }

    private void renderFrame(Rectangle clipBounds, TestEngine engine) {
        Vertex[] vertlist = this.interpolate();
        int triangle;
        short[][] var2 = this.tris;
        triangle = var2.length;
        nextCmd:
        for(int var4 = 0; var4 < triangle; ++var4) {
            short[] tri = var2[var4];
            this.applet.beginShape(8);
            this.applet.texture(this.texture);
            if (clipBounds != null) {
                for (int i = 0; i < 3; ++i) {
                    int index = tri[i];
                    int x = (int) engine.screenX(vertlist[index].getVert().x, vertlist[index].getVert().y, vertlist[index].getVert().z);
                    int y = (int) engine.screenY(vertlist[index].getVert().x, vertlist[index].getVert().y, vertlist[index].getVert().z);
                    if (!clipBounds.contains(new Point(x, y))) continue nextCmd;
                }
            }
            for(int i = 0; i < 3; ++i) {
                float u = (float)this.uvs[tri[3 + i]][0] / (float)this.header.getSkinwidth();
                float v = (float)this.uvs[tri[3 + i]][1] / (float)this.header.getSkinheight();
                int index = tri[i];

                this.applet.vertex(vertlist[index].getVert().x, vertlist[index].getVert().y, vertlist[index].getVert().z, u, v);
                float[] normals = NormalTable.normalTable[vertlist[index].getLightnormalindex()];
                this.applet.normal(normals[0], normals[1], normals[2]);
            }

            this.applet.endShape();
        }

    }
    private void renderFrame(Rectangle clipBounds, PShape engine) {
        Vertex[] vertlist = this.interpolate();
        int triangle;
        short[][] var2 = this.tris;
        triangle = var2.length;
        for(int var4 = 0; var4 < triangle; ++var4) {
            short[] tri = var2[var4];
            this.applet.beginShape(8);
            this.applet.texture(this.texture);
            for(int i = 0; i < 3; ++i) {
                float u = (float)this.uvs[tri[3 + i]][0] / (float)this.header.getSkinwidth();
                float v = (float)this.uvs[tri[3 + i]][1] / (float)this.header.getSkinheight();
                int index = tri[i];

                this.applet.vertex(vertlist[index].getVert().x, vertlist[index].getVert().y, vertlist[index].getVert().z, u, v);
                float[] normals = NormalTable.normalTable[vertlist[index].getLightnormalindex()];
                this.applet.normal(normals[0], normals[1], normals[2]);
            }

            this.applet.endShape();
        }

    }
    public void stopAnimation() {
        this.animation.stop();
    }

    public void startAnimation() {
        this.animation.resume();
    }

    private Vertex[] interpolate() {
        Vertex[] vertlist = new Vertex[this.header.getVertexCount()];
        Vertex[] current = this.frames[this.animation.getCurrframe()].getVerticies();
        Vertex[] next = this.frames[this.animation.getNextFrame()].getVerticies();

        for(int i = 0; i < this.header.getVertexCount(); ++i) {
            vertlist[i] = new Vertex(new PVector((current[i].getVert().x + this.animation.getIntorpol() * (next[i].getVert().x - current[i].getVert().x)) * this.animation.getScale(), (current[i].getVert().y + this.animation.getIntorpol() * (next[i].getVert().y - current[i].getVert().y)) * this.animation.getScale(), (current[i].getVert().z + this.animation.getIntorpol() * (next[i].getVert().z - current[i].getVert().z)) * this.animation.getScale()), current[i].getLightnormalindex());
        }

        return vertlist;
    }
}