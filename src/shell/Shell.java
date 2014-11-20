/**
 * Copyright (C) 2014 Matthias Fisch
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import watten.Card;
import watten.Color;
import watten.Game;
import watten.Player;
import watten.Type;

/**
 *
 * @author fisch
 */
public class Shell {
    
    public static Color parseColor(String s) {
        if(s.equals("Eichel")) {
            return Color.Acorn;
        } else if(s.equals("Schelle")) {
            return Color.Bell;
        } else if(s.equals("Grass")) {
            return Color.Grass;
        } else if(s.equals("Herz")) {
            return Color.Heart;
        } else {
            throw new IllegalArgumentException(s + " ist keine gültige Farbe!");
        }
    }
    
    public static Type parseType(String s) {
        if(s.equals("7")) {
            return Type.Seven;
        } else if(s.equals("8")) {
            return Type.Eight;
        } else if(s.equals("9")) {
            return Type.Nine;
        } else if(s.equals("10")) {
            return Type.Ten;
        } else if(s.equals("Unter")) {
            return Type.Unter;
        } else if(s.equals("Ober")) {
            return Type.Ober;
        } else if(s.equals("König")) {
            return Type.King;
        } else if(s.equals("Sau")) {
            return Type.Ace;
        } else {
            throw new IllegalArgumentException(s + " ist kein gültiger Schlag!");
        }
    }
    
    public static String colorToString(Color color) {
        if(color == Color.Acorn) {
            return "Eichel";
        } else if(color == Color.Bell) {
            return "Schelle";
        } else if(color == Color.Grass) {
            return "Grass";
        } else {
            return "Herz";
        }
    }
    
    
    public static void printStatus(Game game) {
        System.out.println("\nAngesagt: " + new Card(game.getMasterColor(), game.getMasterType()));
        System.out.println("Deine Karten:");
        for(Iterator<Card> i = game.getHumanCards().iterator(); i.hasNext();) {
            Card currCard = i.next();
            System.out.print("  - " + currCard);
            if(currCard.isCritical()) {
                System.out.print(" (krit.)");
            } else if(currCard.getColor() == game.getMasterColor() && currCard.getType() == game.getMasterType()) {
                System.out.print(" (Hauwe)");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public static Card chooseCard(Game game, BufferedReader in) throws IOException {
        boolean chosen = false;
        while(!chosen) {
            System.out.print("Karte wählen: ");
            String[] command = in.readLine().split("\\s+");
            
            if(command.length == 1 && command[0].toLowerCase().equals("status")) {
                printStatus(game);
                continue;
            } else if(command.length != 2) {
                System.out.println("Karte in Form \"Farbe Schlag\" angeben!");
                continue;
            }
            Color color;
            try {
                color = parseColor(command[0]);
            } catch(IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }
            
            Type type;
            try {
                type = parseType(command[1]);
            } catch(IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }
            
            return new Card(color, type);
        }
        return null;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("This application and its source code comes with ABSOLUTELY NO WARRANTY; for details see LICENSE.txt.\n"
        				   + "This is free software, and you are welcome to redistribute it under certain conditions;\n\n");
        
        System.out.println("◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊\n" 
        				 + "◊◊◊◊◊◊◊ (c) Matthias Fisch  ◊◊◊◊◊◊\n"
        		         + "◊◊◊◊◊◊◊ github.com/fischmat ◊◊◊◊◊◊\n"
        				 + "◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊◊\n\n"
        				 + "Spielstatus mit 'status' vor einem Zug abfragen.");
        
        Game game = new Game(3);
        
        printStatus(game);
        
        /*boolean colChosen = false;
        while(!colChosen) {
            System.out.print("Trumpfarbe: ");
            try {
                game.setMasterColor(parseColor(in.readLine()));
                colChosen = true;
            } catch(IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }*/
        
        boolean typeChosen = false;
        while(!typeChosen) {
            System.out.print("Schlag: ");
            try {
                game.setMasterType(parseType(in.readLine()));
                typeChosen = true;
            } catch(IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        
        game.setMasterColor(game.machineSelectColor());
        System.out.println("Maschine wählt Trumpffarbe " + colorToString(game.getMasterColor()));
        
        System.out.println();
        
        boolean beginHuman = true;
        while(!game.isGameOver()) {
            if(!beginHuman) {
                Card machineCard = game.machineMove();
                System.out.println("Maschine legt " + machineCard + "\n");
                
                Card humanCard = chooseCard(game, in);
                
                if(game.humanMove(humanCard, machineCard)) {
                    System.out.println("Du machst einen Punkt!\n");
                    beginHuman = true;
                } else {
                    System.out.println("Maschine erzielt einen Punkt!\n");
                    beginHuman = false;
                }
            } else {
                Card humanCard = chooseCard(game, in);
                
                if(!game.isPlayerOwningCard(humanCard, Player.Human)) {
                	System.out.println("Du besitzt diese Karte nicht!");
                	continue;
                }
                
                Card machineCard = game.machineMove(humanCard);
                
                System.out.println("Maschine legt " + machineCard);
                
                if(game.isCardWinning(humanCard, machineCard)) {
                    System.out.println("Du machst einen Punkt!\n");
                    beginHuman = true;
                } else {
                    System.out.println("Maschine erzielt einen Punkt!\n");
                    beginHuman = false;
                }
            }
        }
        
        if(game.getHumanScore() > game.getMachineScore()) {
            System.out.println("Du hast gewonnen! " + game.getHumanScore() + ":" + game.getMachineScore());
        } else {
            System.out.println("Du hast verloren! " + game.getHumanScore() + ":" + game.getMachineScore());
        }
    }
}
