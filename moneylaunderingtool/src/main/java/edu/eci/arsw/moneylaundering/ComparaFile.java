/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.util.Comparator;

/**
 *
 * @author johan.garrido
 */
public class ComparaFile implements Comparator{

    @Override
    public int compare(Object o1, Object o2) {
        long long1 = ((File)o1).length();
        long long2 = ((File)o2).length();
        if (long1 < long2) return -1;
        else if(long1 < long2) return 1;
        else return 0;
    }
    
}
