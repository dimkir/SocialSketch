/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.socialsketch.tool.rubbish.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.SplashScreen;

public class SplashScreenDemo {
  public static void main(String[] args) {
    SplashScreen splashScreen = SplashScreen.getSplashScreen();
    if ( splashScreen == null ){
        System.out.println("Splash screen is unavailable.");
        return;
    }
    Dimension size = splashScreen.getSize();
    int borderDim = (int) (size.height * 0.05);
    Graphics g = splashScreen.createGraphics();
    g.setColor(Color.blue);
    for (int i = 0; i < borderDim; i++)
      g.drawRect(i, i, size.width - 1 - i * 2, size.height - 1 - i * 2);

    FontMetrics fm = g.getFontMetrics();
    
    int sWidth = fm.stringWidth("Initializing...");
    int sHeight = fm.getHeight();
    if (sWidth < size.width && 2 * sHeight < size.height) {
      g.setColor(Color.blue);
      g.drawString("Initializing...", (size.width - sWidth) / 2, size.height
          - 2 * sHeight);
    }

    splashScreen.update();

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
    }
  }
}