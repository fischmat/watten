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
public class Card {
    
    private static final int MASTER_COLOR_DIFF = 8;
    private static final int MASTER_TYPE_DIFF = 16;
    private static final int HAUWE_RATING = 25;
    private static final int SPITZ_RATING = 26;
    private static final int WELLE_RATING = 27;
    private static final int MAX_RATING = 28;
    
    
    private Color color;
    private Type type;
    
    public Card(Color color, Type type) {
        this.color = color;
        this.type = type;
    }
    
    public int score(Color masterColor, Type masterType) {
        
        if(color == Color.Acorn && type == Type.Seven) {
            return SPITZ_RATING;
        } else if(color == Color.Bell && type == Type.Seven) {
            return WELLE_RATING;
        } else if(color == Color.Acorn && type == Type.Seven) {
            return MAX_RATING;
        }
        
        int rating = type.ordinal() + 1;
        if(color == masterColor && type == masterType) {
            return HAUWE_RATING;
        } else if(type == masterType) {
            rating += MASTER_TYPE_DIFF;
        } else if(color == masterColor) {
            rating += MASTER_COLOR_DIFF;
        }
        return rating;
    }

    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }
    
    public boolean isCritical() {
        if(color == Color.Acorn && type == Type.Seven) {
            return true;
        } else if(color == Color.Bell && type == Type.Seven) {
            return true;
        } else if(color == Color.Acorn && type == Type.Seven) {
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if(color == Color.Acorn) {
            s.append("Eichel ");
        } else if(color == Color.Bell) {
            s.append("Schelle ");
        } else if(color == Color.Grass) {
            s.append("Grass ");
        } else {
            s.append("Herz ");
        }
        
        if(type == Type.Seven) {
            s.append("7");
        } else if(type == Type.Eight) {
            s.append("8");
        } else if(type == Type.Nine) {
            s.append("9");
        } else if(type == Type.Ten) {
            s.append("10");
        } else if(type == Type.Unter) {
            s.append("Unter");
        } else if(type == Type.Ober) {
            s.append("Ober");
        } else if(type == Type.King) {
            s.append("König");
        } else if(type == Type.Ace) {
            s.append("Sau");
        }
        return s.toString();
    }
    
    @Override
    public boolean equals(Object c) {
        return ((Card)c).getColor() == color && ((Card)c).getType() == type;
    }
}
