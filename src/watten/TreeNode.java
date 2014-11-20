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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author fisch
 */
public class TreeNode {
    
    private List<TreeNode> childs = new LinkedList<>();
    private Card machineCard;
    private Card humanCard;
    private Player first;
    
    public TreeNode(Card machineCard, Card humanCard, Player playedBy) {
        this.machineCard = machineCard;
        this.humanCard = humanCard;
        this.first = playedBy;
    }
    
    public void simulateGame(Player player, List<Card> cards, List<Card> cardPool, Color masterColor, Type masterType, int level) {
        if(level == 0) {
            return;
        }
        
        if(player == Player.Machine) {
            for(Iterator<Card> cardIter = cards.iterator(); cardIter.hasNext();) {
                Card machineCard = cardIter.next();

                List<Card> remainingCards = new LinkedList<>();
                remainingCards.addAll(cards);
                remainingCards.remove(machineCard);

                boolean criticalRequ = machineCard.getType() == masterType;
                for(Iterator<Card> poolIter = cardPool.iterator(); poolIter.hasNext();) {
                    Card humanCard = poolIter.next();
                    if(humanCard.getType() != masterType && criticalRequ && !humanCard.isCritical()) {
                        continue;
                    }

                    List<Card> remainingPool = new LinkedList<>();
                    remainingPool.addAll(cardPool);
                    remainingPool.remove(humanCard);

                    TreeNode nextMove = new TreeNode(machineCard, humanCard, Player.Machine);
                    nextMove.simulateGame(invertPlayer(player), remainingCards, remainingPool, masterColor, masterType, level - 1);
                    childs.add(nextMove);
                }
            }
        } else {
            for(Iterator<Card> poolIter = cardPool.iterator(); poolIter.hasNext();) {
                Card humanCard = poolIter.next();
                
                List<Card> remainingPool = new LinkedList<>();
                remainingPool.addAll(cardPool);
                remainingPool.remove(humanCard);
                
                boolean criticalRequ = machineCard.getType() == masterType;
                for(Iterator<Card> cardIter = cards.iterator(); cardIter.hasNext();) {
                    Card machineCard = cardIter.next();
                    if(humanCard.getType() != masterType && criticalRequ && !humanCard.isCritical()) {
                        continue;
                    }
                    
                    List<Card> remainingCards = new LinkedList<>();
                    remainingCards.addAll(cards);
                    remainingCards.remove(machineCard);
                    
                    TreeNode nextMove = new TreeNode(machineCard, humanCard, Player.Human);
                    nextMove.simulateGame(invertPlayer(player), remainingCards, remainingPool, masterColor, masterType, level - 1);
                    childs.add(nextMove);
                }
            }
        }
    }
    
    public List<TreeNode> getBestMoves(Color masterColor, Type masterType) {
        if(childs.isEmpty()) {
            List<TreeNode> path = new LinkedList<>();
            path.add(this);
            return path;
        }
        
        int maxWins = 0;
        List<TreeNode> bestPath = null;
        
        for(Iterator<TreeNode> i = childs.iterator(); i.hasNext();) {
            List<TreeNode> path = i.next().getBestMoves(masterColor, masterType);
            if(bestPath == null) {
                bestPath = path;
            }
            int botWinCount = 0;
            for(Iterator<TreeNode> j = path.iterator(); j.hasNext();) {
                if(j.next().isMachineWinning(masterColor, masterType)) {
                    botWinCount++;
                }
            }
            if(botWinCount > maxWins) {
                bestPath = path;
                maxWins = botWinCount;
            }
        }
        
        if(this.humanCard != null && this.machineCard != null) {
            bestPath.add(0, this);
        }
        return bestPath;
    }
    
    public Card getBestCardByTotalNumbers(Color masterColor, Type masterType) {
        
        int maxWins = 0;
        Card best = null;
        
        for(Iterator<TreeNode> i = childs.iterator(); i.hasNext();) {
            TreeNode currentChild = i.next();
            int wins = currentChild.getTotalWins(masterColor, masterType);
            
            if(wins > maxWins) {
                maxWins = wins;
                best = currentChild.machineCard;
            }
        }
        return best;
    }
    
    public Card getBestCardByTotalNumbers(Card humanCard, Color masterColor, Type masterType) {
        
        int maxWinsGeneral = 0, maxWinsSameCol = 0;
        Card bestGeneral = null, bestSameCol = null;
        
        for(Iterator<TreeNode> i = childs.iterator(); i.hasNext();) {
            TreeNode currentChild = i.next();
            if(currentChild.getHumanCard().getColor() == humanCard.getColor() 
                && currentChild.getHumanCard().getType() == humanCard.getType()) {
                
                int wins = currentChild.getTotalWins(masterColor, masterType);
                if(wins > maxWinsGeneral) {
                    maxWinsGeneral = wins;
                    bestGeneral = currentChild.getMachineCard();
                }
                if((currentChild.getMachineCard().getColor() == humanCard.getColor() 
                    || currentChild.getMachineCard().getColor() == masterColor 
                    || currentChild.getMachineCard().getType() == masterType 
                    || currentChild.getMachineCard().isCritical()) && wins > maxWinsSameCol) {
                        
                    maxWinsSameCol = wins;
                    bestSameCol = currentChild.getMachineCard();
                }
            }
        }
        
        if(bestSameCol != null) {
            return bestSameCol;
        } else {
            return bestGeneral;
        }
    }
    
    private int getTotalWins(Color masterColor, Type masterType) {
        if(childs.isEmpty()) {
            if(machineCard.score(masterColor, masterType) > humanCard.score(masterColor, masterType)) {
                return 1;
            } else {
                return 0;
            }
        }
        
        int wins = 0;
        if(machineCard.score(masterColor, masterType) > humanCard.score(masterColor, masterType)) {
            wins++;
        }
        
        for(Iterator<TreeNode> i = childs.iterator(); i.hasNext();) {
            wins += i.next().getTotalWins(masterColor, masterType);
        }
        
        return wins;
    }
    
    private Player invertPlayer(Player plr) {
        if(plr == Player.Human) {
            return Player.Machine;
        } else {
            return Player.Human;
        }
    }

    public boolean isMachineWinning(Color masterColor, Type masterType) {
        return machineCard.score(masterColor, masterType) > humanCard.score(masterColor, masterType);
    }

    public Card getMachineCard() {
        return machineCard;
    }

    public Card getHumanCard() {
        return humanCard;
    }
    
    public List<TreeNode> getChildren() {
        return childs;
    }
    
}
