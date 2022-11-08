
/*
 * CluePlayer.java
 */

import java.util.*;

/** 
 * This class contains player strategies for the game of Clue. 
 *
 * @author     Kaitlyn DeCola
 *
 */

public class CluePlayer {  
	
	 private ArrayList<String> allSuspects = new ArrayList<String>() {
		 {
			 add("Scarlet");
			 add("Mustard");
			 add("White");
			 add("Peacock");
             add("Green");
             add("Plum");
         }
	 };
	 private ArrayList<String> allWeapons = new ArrayList<String>() {
		 {
			 add("Rope");
			 add("Revolver");
			 add("Wrench");
			 add("Pipe");	 
             add("Candlestick");
             add("Knife");
		 }   
	 };
	 private ArrayList<String> allRooms = new ArrayList<String>() {
		 {
			 add("Kitchen");
		     add("Ballroom");
		     add("Conservatory");
		     add("Billiard Room");
		     add("Library");
		     add("Study");
		     add("Hallway");
		     add("Lounge");
		     add("Dining Room");
		 }   
	 };
	 
	 
	 private ArrayList<Square> allDoors = new ArrayList<Square>() {
		 {
			 add(new Square(4, 3)); // kitchen
			 add(new Square(4, 6)); // ballroom 1
			 add(new Square(5, 7)); // ballroom 2
			 add(new Square(5, 10)); // ballroom 3
			 add(new Square(4, 11)); // ballroom 4
			 add(new Square(5, 14)); // conservatory
			 add(new Square(12, 3)); // dining room 1
			 add(new Square(9, 4)); // dining room 2
			 add(new Square(7, 14)); // billards 1
			 add(new Square(9, 16)); // billards 2
			 add(new Square(12, 15)); // library 1
			 add(new Square(13, 14)); // library 2
			 add(new Square(16, 4)); // lounge
			 add(new Square(16, 11)); // hallway 1
			 add(new Square(15, 9)); // hallway 2
			 add(new Square(17, 14)); // study
		 }
	 };

    /**
     *  Find a random square on the board for a player to move to.
     *
     *  @return                  The square that the player ends up on  
     */
    public Square findSquareRand() {

        int row = 0, col = 0;
        boolean valid = false;
        
        while (!valid) {
            col = (int)(Math.random()*(Clue.board.WIDTH)) + 1;
            row = (int)(Math.random()*(Clue.board.HEIGHT)) + 1;
            if (col >= 0 && col < Clue.board.WIDTH && 
                row >= 2 && row < Clue.board.HEIGHT)
                valid = true;
        }  
        return new Square(row, col);
    }

    /**
     *  Find a square on the board for a player to move to by rolling the 
     *  die and chosing a random direction. The square that is chosen must
     *  be legally accessible to the player (i.e., the player must adhere to 
     *  the rules of the game to be there).
     *
     *  @param    c_row          The current row of this player
     *  @param    c_col          The current column of this player
     *
     *  @return                  The square that this player ends up on
     */
    public Square findSquareRandDir(int c_row, int c_col) {
        int roll = getRoll();
        String currRoom = Clue.board.getRoom(c_row, c_col);
        // time to exit room
        if(Clue.board.isDoor(c_row, c_col)) {
        	if(Clue.board.getRoom(c_row+1, c_col).equals(" ")) {
        		c_row++;
        		roll--;
        	}
        	else if(Clue.board.getRoom(c_row-1, c_col).equals(" ")) {
        		c_row--;
        		roll--;
        	}
        	else if(Clue.board.getRoom(c_row, c_col+1).equals(" ")) {
        		c_col++;
        		roll--;
        	}
        	else if(Clue.board.getRoom(c_row, c_col-1).equals(" ")) {
        		c_col--;
        		roll--;
        	}
        }
        // for the number of rolls
        while( roll > 0) {
        	// pick a random direction
        	int dir = randomDirection();
        	if(dir == 1) {
        		int newRoll = findSquare(c_row+1, c_col, currRoom, roll);
        		if(newRoll < roll) {
        			c_row++;
        		}
        		roll = newRoll;
        	}
        	else if(dir == 2) {
        		int newRoll = findSquare(c_row-1, c_col, currRoom, roll);
        		if(newRoll < roll) {
        			c_row--;
        		}
        		roll = newRoll;
        	}
        	else if(dir == 3) {
        		int newRoll = findSquare(c_row, c_col+1, currRoom, roll);
        		if(newRoll < roll) {
        			c_col++;
        		}
        		roll = newRoll;
        	}
        	else {
        		int newRoll = findSquare(c_row, c_col-1, currRoom, roll);
        		if(newRoll < roll) {
        			c_col--;
        		}
        		roll = newRoll;
        	}
        }
        return new Square(c_row, c_col);
    }

    /**
     *  Find a square on the board for a player to move to by rolling the 
     *  die and chosing a good direction. The square that is chosen must
     *  be legally accessible to the player (i.e., the player must adhere to 
     *  the rules of the game to be there).
     *
     *  @param    c_row          The current row of this player
     *  @param    c_col          The current column of this player
     *  @param    notes          The Detective Notes of this player 
     *
     *  @return                  The square that this player ends up on
     */
    public Square findSquareSmart(int c_row, int c_col, DetectiveNotes notes) {
        int roll = getRoll();
        String currRoom = Clue.board.getRoom(c_row, c_col);
        Square target = null;
        // find the closest door
        if(currRoom == " ") {
        	target = findClosestDoor(c_row, c_col, notes);
        }
        else {
        	// time to exit
        	 if(Clue.board.isDoor(c_row, c_col)) {
        		if(Clue.board.getRoom(c_row+1, c_col).equals(" ")) {
             		c_row++;
             		roll -= 1;
             	}
             	else if(Clue.board.getRoom(c_row-1, c_col).equals(" ")) {
             		c_row -= 1;
             		roll -= 1;
             	}
             	else if(Clue.board.getRoom(c_row, c_col+1).equals(" ")) {
             		c_col++;
             		roll -= 1;
             	}
             	else if(Clue.board.getRoom(c_row, c_col-1).equals(" ")) {
             		c_col -= 1;
             		roll -= 1;
             	}
        		// continue the move
        		target = findClosestDoor(c_row, c_col, notes);
        	 }        	 
        }
        // moves toward target, if gets to target, stop
        
        return moveTowardsTarget(target, c_row, c_col, roll);
    }

    /**
     *  Move to a legal square on the board. If the move lands on a door,
     *  make a suggestion by guessing a random suspect and random weapon.
     *
     *  @param    curr_row        The row of the player before move
     *  @param    curr_column     The column of the player before move
     *  @param    row             Selected row 
     *  @param    column          Selected column 
     *  @param    color           Player color
     *  @param    name            Player name
     *  @param    notes           Player Detective Notes 
     *
     *  @return                   A suggestion -> [name,room,suspect,weapon]
     */
    public String[] moveNaive(int curr_row, int curr_column, 
                         int row, int column, String color, String name, 
                         DetectiveNotes notes) {

    	String [] retVal = new String[4];
        String suspect = notes.getRandomSuspect();
        String weapon = notes.getRandomWeapon();
        String room = Clue.board.getRoom(row,column);

        if (Clue.board.isDoor(curr_row,curr_column))
            Clue.board.setColor(curr_row,curr_column,"Gray");
        else 
            Clue.board.setColor(curr_row, curr_column, "None");

        if (Clue.board.isDoor(row,column)) { 
            retVal[0] = name;
            retVal[1] = room;
            retVal[2] = suspect;
            retVal[3] = weapon;

            if (Clue.gui) {
                System.out.print(name+" suggests that the crime was committed");
                System.out.println(" in the " + room + " by " + suspect +
                               " with the " + weapon);
            }
        }
        else retVal = null;

        Clue.board.setColor(row,column,color);

	return retVal;
    }

    /**
     *  Move to a legal square on the board. If the move lands on a door,
     *  make a good suggestion for the suspect and the weapon. A good
     *  suggestion here is one which does not include any suspects or
     *  weapons that are already in the Detective Notes of this player.
     *
     *  @param    curr_row        The row of the player before move
     *  @param    curr_column     The column of the player before move
     *  @param    row             Selected row 
     *  @param    column          Selected column 
     *  @param    color           Player color
     *  @param    name            Player name
     *  @param    notes           Player Detective Notes 
     *
     *  @return                   A suggestion -> [name,room,suspect,weapon]
     */
    public String[] moveSmart(int curr_row, int curr_column, 
                         int row, int column, String color, String name, 
                         DetectiveNotes notes) {
    	String[] retVal = new String[4];
    	String room = Clue.board.getRoom(row, column);
    	String suspect = "";
		String weapon = "";
    	if (Clue.board.isDoor(curr_row,curr_column))
            Clue.board.setColor(curr_row,curr_column,"Gray");
        else 
            Clue.board.setColor(curr_row, curr_column, "None");
    	
    	 if (Clue.board.isDoor(row,column)) { 
    		 // find a suspect not in the notes
    		 for(String sus : allSuspects) {
    	    	if(!notes.getMySuspects().contains(sus)) {
    	    		suspect = sus;
    	    		break;
    	    	}
    		 }
    		 // find a weapon not in the notes
    		 for(String weap : allWeapons) {
    	    	if(!notes.getMyWeapons().contains(weap)) {
    	    		weapon = weap;
    	    	}
    		 }
             retVal[0] = name;
             retVal[1] = room;
             retVal[2] = suspect;
             retVal[3] = weapon;

             if (Clue.gui) {
                 System.out.print(name+" suggests that the crime was committed");
                 System.out.println(" in the " + room + " by " + suspect +
                                " with the " + weapon);
             }
         }
    	 else retVal = null;

         Clue.board.setColor(row,column,color);
    	
    	
    	return retVal;

    }
    
    /**
     *  Try to prove a suggestion is false by asking the players, in a
     *  round-robin fashion, to show the suggester one of the suggestions if
     *  the player has that suggested card in their hand. The other players 
     *  know that ONE of the suggestions cannot be in the case file, but they 
     *  do not know which one.
     *
     *  @param  suggestion      A suggestion -> [name,room,suspect,weapon]
     *  @param  notes           The Detective Notes of the current player
     *  @param  player          The current player
     *  @param  next            The next player clockwise around the board
     *  
     *  @return                 An accusation, to check if it is a winner
     *
     */
    public ArrayList<String> prove(String[] suggestion, DetectiveNotes notes,
                         int player, int next) {
    	if(suggestion[0] == null) {
    		return null;
    	}
        
        String card = "";
        boolean found = false;
        ArrayList<String> accusation = new ArrayList<String>();

        // Ask the other 5 players to show one of the suggested cards
        while(player != next) {
        	card = findCard(next, suggestion);
        	if(card != "") {
        		found = true;
        		break;
        	}
        	// go to the next player
        	next++;
        	// wrap around
        	if(next == 6) {
        		next = 0;
        	}
        }
        if(found) {
        	if(allSuspects.contains(card)) {
        		setNotes(notes, card, "suspect");
        	}
        	else if(allWeapons.contains(card)) {
        		setNotes(notes, card, "weapon");
        	}
        	else if(allRooms.contains(card)) {
        		setNotes(notes, card, "room");
        	}
        }
        // if only of one type is left
        if(notes.getMySuspects().size() == 5 && notes.getMyWeapons().size() == 5 && notes.getMyRooms().size() == 8) {
        	// find which room is left
        	for(String room : allRooms) {
        		if(!notes.getMyRooms().contains(room)) {
        			accusation.add(room);
        			continue;
        		}
        	}
        	// find which suspect is left
        	for(String sus : allSuspects) {
        		if(!notes.getMySuspects().contains(sus)) {
        			accusation.add(sus);
        			continue;
        		}
        	}
        	// find which weapon is left
        	for(String weap : allWeapons) {
        		if(notes.getMyWeapons().contains(weap)) {
        			accusation.add(weap);
        			continue;
        		}
        	}
        	return accusation;
        }

        // Make an accusation
        if (!found) {
            // Check this player's cards to see if this player has them
            for (int i=0; i<3; i++) { 
                card = (String)Arrays.asList(
                      (Clue.allCards.get(Clue.turn)).keySet().toArray()).get(i);

                for (int k=1; k<=3; k++) 
                    if (!found && card.equals(suggestion[k])) {
                        found = true;
                    }
            }
            // If still not found, I do believe I have won the game!
            for (int i=1; i<4; i++)
                if (!found)
                    accusation.add(suggestion[i]);
                else 
                    accusation.add("None");
        }        
        return accusation;
    }

    /**
     *  Update this player's detective notes upon learning some information.
     *
     *  @param    notes    The detective notes of this player
     *  @param    card     The card that caused the change
     *  @param    type     The type of the card - suspect, weapon, or room
     *
     */
    public void setNotes(DetectiveNotes notes, String card, String type) {

        if (type.equals("suspect"))
            notes.addSuspect(card);
        else if (type.equals("weapon"))
            notes.addWeapon(card);
        else if (type.equals("room"))
            notes.addRoom(card);
    }
    
    /**
     * Find a matching card from the suggestion
     * 
     * @param player		The current player
     * @param suggestion	The player's suggestions
     * @return
     */
    private String findCard(int player, String[] suggestion) {
    	String card = "";
    	
    	List<String> arrSug = Arrays.asList(suggestion);
    	arrSug = arrSug.subList(1, 4);
    	for (int i=1; i<4; i++) { 
    		String playerCard = (String)Arrays.asList(
                    (Clue.allCards.get(player)).keySet().toArray()).get(i-1);
    		if(arrSug.contains(playerCard)) {
    			card = playerCard;
    			break;
    		}
    	}
    	
    	return card;
    }
    
    /**
     * return a random umber from 1 to 6
     * 
     * @return
     */
    public int getRoll() {
        return (int) ((Math.random() * (6 - 1)) + 1);
    }
    /**
     * return a random numer from 1 to 4 that will correspond to a direction
     * @return
     */
    public int randomDirection() {
    	return (int) ((Math.random() * (5 - 1)) + 1);
    }
    
    /**
     * Finds a legal square to move to
     * @param row 		The current row
     * @param col		The current column
     * @param currRoom	The current room
     * @param roll		Number of roll
     * @return			The left over roll
     */
    private int findSquare(int row, int col, String currRoom, int roll) {
    	// if out of dimensions, skip
    	if(row >= Clue.board.HEIGHT || row <= 2 || col >= Clue.board.WIDTH ||col < 0) {
			return roll;
		}
    	if(Clue.board.isDoor(row, col)){
    		return roll-1;
    	}
    	// if not a door, check to make sure did not enter a room
    	if(!Clue.board.isDoor(row, col) && Clue.board.getRoom(row, col) == " ") {
    		return roll-1;
    	}
    	// not a valid move
		return roll;
    }
    
    /**
     * Finds the closest door that is not in the notes
     * @param row		The current row
     * @param col		The current column
     * @param notes		The player's notes
     * @return			The square of the closest door
     */
    private Square findClosestDoor(int row, int col, DetectiveNotes notes) {
    	Square closestSquare = null;
    	int dist = 0;
    	ArrayList<String> seenRooms = notes.getMyRooms();
    	for(Square s : allDoors) {
    		String room = Clue.board.getRoom(s.getRow(), s.getColumn());
    		if(!seenRooms.contains(room)) {
    			if(closestSquare == null) {
    				closestSquare = s;
    				int xdis = Math.abs(row-s.getRow());
    				int ydis = Math.abs(col-s.getColumn());
    				dist = xdis+ydis;
    			}
    			else {
    				int xdis = Math.abs(row-s.getRow());
    				int ydis = Math.abs(col-s.getColumn());
    				if(xdis+ydis < dist) {
    					closestSquare = s;
    					dist = xdis+ydis;
    				}
    			}
    		}
    	}
    	return closestSquare;
    }
    
    /**
     * Player moves towards the target door
     * @param target	The door
     * @param row		The current row
     * @param col		The current column
     * @param roll		The roll
     * @return			A square that the player will move to 
     */
    private Square moveTowardsTarget(Square target, int row, int col, int roll) {   	
    	int targRow = target.getRow();
    	int targCol = target.getColumn();
    	
    	int rowDis = targRow - row;
    	int colDis = targCol - col;
    	boolean rowFailed = false;
    	boolean colFailed = false;
    	if(rowDis+colDis <= roll) {
    		return target;
    	}
    	while(roll > 0) {
    		if(colFailed && rowDis == 0) {
    			rowDis++;
    			row -= 1;
    			roll -= 1;
    			colFailed = false;
    			rowFailed = true;
    		}
    		else if((rowDis < colDis && !rowFailed) || colFailed) {
    			if(rowDis < 0) {
    				if(Clue.board.getRoom(row-1, col).equals(" ")) {
    					row -= 1;
    					roll -= 1;
    					rowDis++;
    					colFailed = false;
    				}
    				// cannot move in that direction
    				else {
    					rowFailed = true;
    					colFailed = false;
    				}
    			}
    			else if(rowDis > 0) {
    				if(Clue.board.getRoom(row+1, col) .equals(" ")) {
    					row++;
    					roll =- 1;
    					rowDis -= 1;
    					colFailed = false;
    				}
    				// cannot move in that direction
    				else {
    					rowFailed = true;
    					colFailed = false;
    				}
    			}

    			else {
    				rowFailed = true;
    			}
    			
    		}
    		else {
    			if(colDis < 0) {
    				if(Clue.board.getRoom(row, col-1) .equals(" ")) {
    					col -= 1;
    					roll -= 1;
    					colDis++;
    					rowFailed = false;
    				}
    				else {
    					colFailed = true;
    				}
    			}
    			else if(colDis > 0) {
    				if(Clue.board.getRoom(row, col+1) .equals(" ")) {
    					col++;
    					roll -= 1;
    					colDis -= 1;
    					rowFailed = false;
    				}
    				else {
    					colFailed = true;
    				}
    		
    			}
    			else {
    				colFailed = true;
    			}
    		}
    	}
 
    	
    	return new Square(row, col);
    }
}
