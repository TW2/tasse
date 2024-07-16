/*
 * Copyright (C) 2024 util2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wingate.tasse.render;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author util2
 */
public class FontObject {
    
    private Font font;
    private FontMetrics metrics;
    
    private String line;
    private Dimension video;
    
    private final List<Float> advances = new ArrayList<>();
    private double lineWidth_ = 0d;
    private double lineHeight_ = 0d;
    private float[] percentsOfFalseWidth;
    private Rectangle2D lineRect;

    public FontObject() {
        this(new Font("SansSerif", Font.PLAIN, 35), "Show demo!", new Dimension(1280, 720));
    }
        
    public FontObject(Font font, String line, Dimension video){
        this.font = font;
        this.line = getStripped(line);
        this.video = video;
        search(font);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font, String line, Dimension video) {
        this.font = font;
        this.line = getStripped(line);
        this.video = video;
        advances.clear();
        search(font);
    }
    
    private void search(Font f){
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        metrics = g.getFontMetrics(f);
        g.dispose();
        
        // =====================================================================
        // ADVANCEs
        // =====================================================================
        float lineWidth = metrics.stringWidth(line);
        float[] lineCharsWidth = new float[line.length()];
        float lineWidthForChars = 0f;
        
        for(int i=0; i<line.length(); i++){
            lineCharsWidth[i] = metrics.charWidth(line.charAt(i));
            lineWidthForChars += lineCharsWidth[i];
        }
        
        percentsOfFalseWidth = new float[line.length()];
        
        for(int i=0; i<line.length(); i++){
            percentsOfFalseWidth[i] = lineCharsWidth[i] / lineWidthForChars;
        }
        
        for(int i=0; i<line.length(); i++){
            advances.add(lineWidth * percentsOfFalseWidth[i]);
        }
        
        // =====================================================================
        // BOUNDS
        // =====================================================================
        lineWidth_ = metrics.getStringBounds(line, g).getWidth();
        lineHeight_ = metrics.getStringBounds(line, g).getHeight();
        lineRect = metrics.getStringBounds(line, g);
    }
    
    public int getAscent(){
        return metrics.getAscent();
    }
    
    public int getDescent(){
        return metrics.getDescent();
    }
    
    public int getLeading(){
        return metrics.getLeading();
    }
    
    public int getHeight(){
        return metrics.getHeight();
    }
    
    public double getLineWidth(){
        return lineWidth_;
    }
    
    public double getLineHeight(){
        return lineHeight_;
    }
    
    public Point2D getPerCharacterAlignNumpad(int charIndex, int an){
        Point2D position = new Point2D.Double();
        double x = 0;
        double y = 0;
        
        for(int i=0; i<line.length(); i++){
            Rectangle2D r = new Rectangle2D.Double(
                    x,
                    y,
                    lineWidth_ * percentsOfFalseWidth[i],
                    lineHeight_
            );
            
            if(i == charIndex){
                switch(an){
                    case 1 -> { position = new Point2D.Double(r.getMinX(), r.getMaxY()); }
                    case 2 -> { position = new Point2D.Double(r.getCenterX(), r.getMaxY()); }
                    case 3 -> { position = new Point2D.Double(r.getMaxX(), r.getMaxY()); }
                    case 4 -> { position = new Point2D.Double(r.getMinX(), r.getCenterY()); }
                    case 5 -> { position = new Point2D.Double(r.getCenterX(), r.getCenterY()); }
                    case 6 -> { position = new Point2D.Double(r.getMaxX(), r.getCenterY()); }
                    case 7 -> { position = new Point2D.Double(r.getMinX(), r.getMinY()); }
                    case 8 -> { position = new Point2D.Double(r.getCenterX(), r.getMinY()); }
                    case 9 -> { position = new Point2D.Double(r.getMaxX(), r.getMinY()); }
                }
                break;
            }            
            
            x += lineWidth_ * percentsOfFalseWidth[i];
        }
        
        return position;
    }
    
    public Point2D getPerLineAlignNumpad(int an){
        Point2D position = new Point2D.Double();
        
        Rectangle2D r = lineRect;
        
        switch(an){
            case 1 -> { position = new Point2D.Double(r.getMinX(), r.getMaxY()); }
            case 2 -> { position = new Point2D.Double(r.getCenterX(), r.getMaxY()); }
            case 3 -> { position = new Point2D.Double(r.getMaxX(), r.getMaxY()); }
            case 4 -> { position = new Point2D.Double(r.getMinX(), r.getCenterY()); }
            case 5 -> { position = new Point2D.Double(r.getCenterX(), r.getCenterY()); }
            case 6 -> { position = new Point2D.Double(r.getMaxX(), r.getCenterY()); }
            case 7 -> { position = new Point2D.Double(r.getMinX(), r.getMinY()); }
            case 8 -> { position = new Point2D.Double(r.getCenterX(), r.getMinY()); }
            case 9 -> { position = new Point2D.Double(r.getMaxX(), r.getMinY()); }
        }
        
        return position;
    }
    
    public static String getStripped(String s){
        StringBuilder output = new StringBuilder();
        
        if(s.contains("{") && s.contains("}")){
            Pattern p = Pattern.compile("\\{[^\\}]+\\}(?<value>[^\\{]*)");
            Matcher m = p.matcher(s);
            
            while(m.find()){
                output.append(m.group("value"));
            }
        }else{
            output.append(s);
        }        
        
        return output.toString();
    }
}
