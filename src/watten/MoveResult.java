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

/**
 *
 * @author fisch
 */
public class MoveResult {
    
    private Card humanCard;
    private Card machineCard;
    private Color masterColor;
    private Type masterType;

    public MoveResult(Card humanCard, Card machineCard, Color masterColor, Type masterType) {
        this.humanCard = humanCard;
        this.machineCard = machineCard;
        this.masterColor = masterColor;
        this.masterType = masterType;
    }

    public Card getHumanCard() {
        return humanCard;
    }

    public Card getMachineCard() {
        return machineCard;
    }
    
    public Player getWinner() {
        if(humanCard.score(masterColor, masterType) 
                > machineCard.score(masterColor, masterType)) {
            return Player.Human;
        } else {
            return Player.Machine;
        }
    }
}
