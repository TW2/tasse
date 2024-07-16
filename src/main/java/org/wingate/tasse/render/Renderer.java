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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.wingate.tasse.ass.ASS;
import org.wingate.tasse.ass.AssEvent;

/**
 *
 * @author util2
 */
public class Renderer {
    
    private ASS ass;

    public Renderer() {
        this(ASS.NoFileToLoad());
    }

    public Renderer(ASS ass) {
        this.ass = ass;
    }

    public ASS getAss() {
        return ass;
    }

    public void setAss(ASS ass) {
        this.ass = ass;
    }
    
    @SuppressWarnings("UseSpecificCatch")
    public BufferedImage createMemoryImage(int width, int height, long ms){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        for(AssEvent event : ass.getEvents()){
//            System.out.println(String.format("ms: %d - start: %d, end: %d",
//                    ms, event.getTime().getMsStart(), event.getTime().getMsStop()));
            if(event.getTime().isBetween(ms)){
                try{
                    Font font = event.getStyle().getFont();
                    Font f = font.deriveFont(font.getSize2D() * 86 / 100);
                    String line = event.getText();
                    Dimension video = new Dimension(width, height);
                    
                    FontObject fo = new FontObject(f, line, video);
                    
                    int an = 2;
                    // Scanne la ligne à la recherche de la dernière occurence.
                    // Si trouvé, alors appliqué sinon style
                    if(line.contains("\\an")){
                        Pattern p = Pattern.compile("\\\\an(?<an>\\d{1})");
                        Matcher m = p.matcher(line);
                        while(m.find()){
                            an = Integer.parseInt(m.group("an"));
                        }
                    }else{
                        an = event.getStyle().getAlignment();
                    }
                    
                    // Charge les valeurs du point d'insertion
                    // Un point d'insertion texte en Java se place en bas à gauche pour les langages LTR
                    double x = 0, y = 0;
                    
                    // On applique d'abord la situation où il n'y a pas de surcharge
                    switch(an){
                        case 1 -> {
                            // On se trouve en bas à gauche,
                            // on peut modifier la position principalement grâce à :
                            // - la marge de gauche MarginL
                            // - la marge verticale MarginV
                            // mais aussi avec MarginR TODO
                            // On essaie de charger la valeur de la ligne
                            // à défaut, on charge la valeur du style
                            x = searchForMarginL(event);
                            y = height - searchForMarginV(event);
                        }
                        case 2 -> {
                            // On se trouve en bas au centre,
                            // on peut modifier la position grâce à :
                            // - la marge de gauche MarginL
                            // - la marge de gauche MarginR
                            // - la marge verticale MarginV
                            // On essaie de charger la valeur de la ligne
                            // à défaut, on charge la valeur du style
                            int ml = searchForMarginL(event);
                            int mr = searchForMarginR(event);
                            int viewport = width - ml - mr;
                            double brutLoc = (viewport - fo.getLineWidth()) / 2;
                            x = ml + brutLoc;
                            y = height - searchForMarginV(event);
                        }
                        case 3 -> {
                            // On se trouve en bas à droite,
                            // on peut modifier la position principalement grâce à :
                            // - la marge de gauche MarginR
                            // - la marge verticale MarginV
                            // mais aussi avec MarginL TODO
                            // On essaie de charger la valeur de la ligne
                            // à défaut, on charge la valeur du style
                            int mr = searchForMarginR(event);
                            int viewport = width - mr;
                            double brutLoc = viewport - fo.getLineWidth();
                            x = brutLoc;
                            y = height - searchForMarginV(event);
                        }
                        case 4 -> {
                            // On se trouve au milieu à gauche,
                            // on peut modifier la position principalement grâce à :
                            // - la marge de gauche MarginL
                            // - la marge verticale MarginV
                            // mais aussi avec MarginR TODO
                            // On essaie de charger la valeur de la ligne
                            // à défaut, on charge la valeur du style
                            x = searchForMarginL(event);
                            y = height / 2 + fo.getDescent(); // TODO ajuster marges V haut bas
                        }
                        case 5 -> {
                            // On se trouve au milieu au centre,
                            // on peut modifier la position grâce à :
                            // - la marge de gauche MarginL
                            // - la marge de gauche MarginR
                            // - la marge verticale MarginV
                            // On essaie de charger la valeur de la ligne
                            // à défaut, on charge la valeur du style
                            int ml = searchForMarginL(event);
                            int mr = searchForMarginR(event);
                            int viewport = width - ml - mr;
                            double brutLoc = (viewport - fo.getLineWidth()) / 2;
                            x = ml + brutLoc;
                            y = height / 2 + fo.getDescent(); // TODO ajuster marges V haut bas
                        }
                        case 6 -> {
                            // On se trouve au milieu à droite,
                            // on peut modifier la position principalement grâce à :
                            // - la marge de gauche MarginR
                            // - la marge verticale MarginV
                            // mais aussi avec MarginL TODO
                            // On essaie de charger la valeur de la ligne
                            // à défaut, on charge la valeur du style
                            int mr = searchForMarginR(event);
                            int viewport = width - mr;
                            double brutLoc = viewport - fo.getLineWidth();
                            x = brutLoc;
                            y = height / 2 + fo.getDescent(); // TODO ajuster marges V haut bas
                        }
                        case 7 -> {
                            // On se trouve en haut à gauche,
                            // on peut modifier la position principalement grâce à :
                            // - la marge de gauche MarginL
                            // - la marge verticale MarginV
                            // mais aussi avec MarginR TODO
                            // On essaie de charger la valeur de la ligne
                            // à défaut, on charge la valeur du style
                            x = searchForMarginL(event);
                            y = fo.getAscent() + searchForMarginV(event);
                        }
                        case 8 -> {
                            // On se trouve en haut au centre,
                            // on peut modifier la position grâce à :
                            // - la marge de gauche MarginL
                            // - la marge de gauche MarginR
                            // - la marge verticale MarginV
                            // On essaie de charger la valeur de la ligne
                            // à défaut, on charge la valeur du style
                            int ml = searchForMarginL(event);
                            int mr = searchForMarginR(event);
                            int viewport = width - ml - mr;
                            double brutLoc = (viewport - fo.getLineWidth()) / 2;
                            x = ml + brutLoc;
                            y = fo.getAscent() + searchForMarginV(event);
                        }                            
                        case 9 -> {
                            // On se trouve en haut à droite,
                            // on peut modifier la position principalement grâce à :
                            // - la marge de gauche MarginR
                            // - la marge verticale MarginV
                            // mais aussi avec MarginL TODO
                            // On essaie de charger la valeur de la ligne
                            // à défaut, on charge la valeur du style
                            int mr = searchForMarginR(event);
                            int viewport = width - mr;
                            double brutLoc = viewport - fo.getLineWidth();
                            x = brutLoc;
                            y = fo.getAscent() + searchForMarginV(event);
                        }
                    }
                    
                    // On modifie ces valeus si on trouve pos / move
                    if(line.contains("\\pos")){
                        Pattern p = Pattern.compile("\\\\pos\\((?<x>\\d+),(?<y>\\d+)\\)");
                        Matcher m = p.matcher(line);
                        int xPOS = 0, yPOS = 0;
                        boolean found = false;
                        while(m.find()){
                            xPOS = Integer.parseInt(m.group("x"));
                            yPOS = Integer.parseInt(m.group("y"));
                            found = true;
                        }
                        if(found == true){
                            x += xPOS;
                            y += yPOS;
                        }
                    }
                    
                    if(line.contains("\\move")){
                        Pattern p = Pattern.compile("\\\\move\\((?<x>\\d+),(?<y>\\d+),");
                        Matcher m = p.matcher(line);
                        int xPOS = 0, yPOS = 0;
                        boolean found = false;
                        while(m.find()){
                            xPOS = Integer.parseInt(m.group("x"));
                            yPOS = Integer.parseInt(m.group("y"));
                            found = true;
                        }
                        if(found == true){
                            x += xPOS;
                            y += yPOS;
                        }
                    }
                    
                    // On dessine le texte
                    g.setColor(Color.red);
                    g.setFont(f);
                    g.drawString(FontObject.getStripped(line), (float)x, (float)y);
                }catch(Exception exc){
                    
                }
            }
        }
        
        g.dispose();
        return image;
    }
    
    public int searchForMarginL(AssEvent ev){
        return ev.getMarginL() > 0 ? ev.getMarginL() : ev.getStyle().getMarginL();
    }
    
    public int searchForMarginR(AssEvent ev){
        return ev.getMarginR() > 0 ? ev.getMarginR() : ev.getStyle().getMarginR();
    }
    
    public int searchForMarginV(AssEvent ev){
        return ev.getMarginV() > 0 ? ev.getMarginV() : ev.getStyle().getMarginV();
    }
    
    public int searchForMarginT(AssEvent ev){
        return ev.getMarginV() > 0 ? ev.getMarginV() : ev.getStyle().getMarginT();
    }
    
    public int searchForMarginB(AssEvent ev){
        return ev.getMarginV() > 0 ? ev.getMarginV() : ev.getStyle().getMarginB();
    }
}
