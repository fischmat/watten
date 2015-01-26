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
package watten;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author fisch
 */
public class Game {
    
    private List<Card> humanCards = new LinkedList<>();
    private List<Card> machineCards = new LinkedList<>();
    private List<Card> unknownByAI;
    
    private Color masterColor;
    private Type masterType;
    
    private int humanScore;
    private int machineScore;
    
    private int level = Integer.MAX_VALUE;
    
    
    public Game() {
        initialize();
    }
    
    public Game(int level) {
        this.level = level;
        initialize();
    }
    
    public List<Card> getPlayerCards() {
        return humanCards;
    }
    
    public List<Card> getMachineCards() {
        return machineCards;
    }
    
    private void initialize() {
        List<Card> allCards = new ArrayList<>();
        Color[] colors = Color.values();
        Type[] types = Type.values();
        int i = 0;
        for(int j = 0; j < colors.length; j++) {
            for(int k = 0; k < types.length; k++, i++) {
                allCards.add(new Card(colors[j], types[k]));
            }
        }
        
        unknownByAI = new LinkedList<>(allCards);
        
        Random rand = new Random();
        for(int plrCardCount = 0; plrCardCount < 5; plrCardCount++) {
            Card currCard = allCards.get(Math.abs(rand.nextInt()%allCards.size()));
            allCards.remove(currCard);
            humanCards.add(currCard);
        }
        
        for(int botCardCount = 0; botCardCount < 5; botCardCount++) {
            Card currCard = allCards.get(Math.abs(rand.nextInt()%allCards.size()));
            allCards.remove(currCard);
            unknownByAI.remove(currCard);
            machineCards.add(currCard);
        }
    }
    
    public Card machineMove() {
        TreeNode tree = new TreeNode(null, null, Player.Machine);
        tree.simulateGame(Player.Machine, machineCards, unknownByAI, masterColor, masterType, level);
        Card chosenCard = tree.getBestCardByTotalNumbers(masterColor, masterType);
        machineCards.remove(chosenCard);
        return chosenCard;
    }
    
    public Card machineMove(Card humanCard) {
        TreeNode tree = new TreeNode(null, null, Player.Machine);
        
        tree.simulateGame(Player.Machine, machineCards, unknownByAI, masterColor, masterType, level);
        
        Card chosenCard = tree.getBestCardByTotalNumbers(masterColor, masterType);
        
        machineCards.remove(chosenCard);
        humanCards.remove(humanCard);
        
        if(isCardWinning(humanCard, chosenCard)) {
            humanScore++;
        } else {
            machineScore++;
        }
        
        return chosenCard;
    }
    
    public Color machineSelectColor() {
        int[] counts = new int[Color.values().length];
        for(Iterator<Card> cardIter = machineCards.iterator(); cardIter.hasNext();) {
            counts[cardIter.next().getColor().ordinal()]++;
        }
        Color chosenColor = Color.values()[0];
        int maxCount = counts[0];
        for(int i = 0; i < counts.length; i++) {
            if(counts[i] > maxCount) {
                maxCount = counts[i];
                chosenColor = Color.values()[i];
            }
        }
        
        return chosenColor;
    }
    
    public boolean humanMove(Card playedCard, Card machinesCard) {
        
        humanCards.remove(playedCard);
        unknownByAI.remove(playedCard);
        if(isCardWinning(playedCard, machinesCard)) {
            humanScore++;
            return true;
        } else {
            machineScore++;
            return false;
        }
    }
    
    public Type machineSelectType() {
        int[] counts = new int[Type.values().length];
        for(Iterator<Card> cardIter = machineCards.iterator(); cardIter.hasNext();) {
            counts[cardIter.next().getColor().ordinal()]++;
        }
        Type chosenType = Type.values()[0];
        int maxCount = counts[0];
        for(int i = 0; i < counts.length; i++) {
            if(counts[i] > maxCount) {
                maxCount = counts[i];
                chosenType = Type.values()[i];
            }
        }
        
        // Wenn jeder Schlag nur einmal vorkommt:
        if(maxCount == 1) {
            Type minType = Type.Ace;
            chosenType = Type.Ace;
            boolean upgraded = false;
            for(Iterator<Card> cardIter = machineCards.iterator(); cardIter.hasNext();) {
                Card currentCard = cardIter.next();
                Type currentType = currentCard.getType();
                
                if(currentType.ordinal() < chosenType.ordinal()) {
                    if(currentCard.getColor() != masterColor) {
                        chosenType = currentType;
                    }
                    minType = currentType;
                }
            }
            
            if(!upgraded) {
                chosenType = minType;
            }
            
        }
        
        return chosenType;
    }
    
    public boolean isPlayerOwningCard(Card card, Player player) {
    	Collection<Card> playerCards;
    	if(player == Player.Human) {
    		playerCards = humanCards;
    	} else {
    		playerCards = machineCards;
    	}
    	
    	for (Card currentCard : playerCards) {
			if(card.equals(currentCard)) {
				return true;
			}
		}
    	
    	return false;
    }
    
    public boolean isGameOver() {
        return humanCards.isEmpty() || machineCards.isEmpty() || humanScore == 3 || machineScore == 3;
    }

    public Color getMasterColor() {
        return masterColor;
    }

    public void setMasterColor(Color masterColor) {
        this.masterColor = masterColor;
    }

    public Type getMasterType() {
        return masterType;
    }

    public void setMasterType(Type masterType) {
        this.masterType = masterType;
    }

    public List<Card> getHumanCards() {
        return humanCards;
    }
    
    public boolean isCardWinning(Card a, Card b) {
        return a.score(masterColor, masterType) >= b.score(masterColor, masterType);
    }

    public int getHumanScore() {
        return humanScore;
    }

    public int getMachineScore() {
        return machineScore;
    }
    
    
}
