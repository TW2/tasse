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
package org.wingate.tasse.core;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.wingate.tasse.ass.ASS;
import org.wingate.tasse.render.Renderer;

/**
 *
 * @author util2
 */
public class CreateFile {
    
    @SuppressWarnings("UseSpecificCatch")
    public static void main(String[] args) {
        try{
            ASS ass = ASS.Read(args[0]);
            Dimension size = new Dimension(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            long ms = Long.parseLong(args[3]);
            
            Renderer rer = new Renderer(ass);
            BufferedImage image = rer.createMemoryImage(size.width, size.height, ms);
            
            ImageIO.write(image, "png", new File("image.png"));
        }catch(Exception exc){
            System.err.println("Error!");
            System.out.println("Format: java -jar tasse.jar asspath width height milliseconds");
        }
    }
    
}
