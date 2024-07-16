package org.wingate.tasse;

import org.wingate.tasse.core.CreateFile;

/**
 *
 * @author util2
 */
public class Tasse {

    public static void main(String[] args) {
        System.out.println("Format: java -jar tasse.jar asspath width height milliseconds");
        System.out.println("Execute");
        CreateFile.main(args);
    }
}
