import java.util.*;
import controllers.osc.OSCPacMan;

public class B00697153PacMan extends OSCPacMan {
	/***********************************************************************************************************************/

	private String currentState;
	private String previousState;
	private int lastMove;
	private final int up = 0;
	private final int down = 2;
	private final int right = 1;
	private final int left = 3;

	public B00697153PacMan(String[] args) {
		super(args);
	}

	@Override
	public void startGame() { //runs once at the start of each game
		lastMove = 3;
		currentState = "eatPills";
		previousState = "eatPills";
	}

	@Override
	public void endGame() { //runs once at the end of each game
		
	}

	/**
	* Select a direction for Ms. Pac-Man.
	* Runs once for each timestep of the game.
	* Must return one int: 0 (UP),  1 (RIGHT),  2 (DOWN), 3 (LEFT)
	*
	* Note: to check a binary state variable, use less-than or greater-than:
	* if (directedState.get(11) > 0); //test if closest ghost is approaching
	*
	* @return The direction to move in.
	*/
	@Override
	public int selectMove() {
		int move = lastMove;
		//arrayLists of the board state
		ArrayList<Float> upState = getDirectedState(up);
		ArrayList<Float> rightState = getDirectedState(right);
		ArrayList<Float> downState = getDirectedState(down);
		ArrayList<Float> leftState = getDirectedState(left);

		//use previousState to search for the correct currentState:
		switch (previousState) {
			case "eatPills":
				if (upState.get(8) < 0.01 || downState.get(8) < 0.01 || leftState.get(8) < 0.01 || rightState.get(8) < 0.01) { //dist to closest power pill
					currentState = "wait";
				} else if (upState.get(10) < 0.03 || downState.get(10) < 0.03 || leftState.get(10) < 0.03 || rightState.get(10) < 0.03) { //distance to closest ghost
					currentState = "escapeFromGhost";
				} else {
					currentState = previousState;
				}
				break;
			case "wait":
				if ((upState.get(10) < 0.05 && upState.get(8) < 0.2) || (downState.get(10) < 0.05 && downState.get(8) < 0.2)) {
					currentState = "eatPowerPill";
				} else if (upState.get(10) < 0.02 || downState.get(10) < 0.02 || leftState.get(10) < 0.02 || rightState.get(10) < 0.02) { //distance to closest ghost
					currentState = "eatPowerPill";
				} else {
					currentState = previousState;
				}
				break;
			case "eatPowerPill":
				if (upState.get(8) < 0.1 || downState.get(8) < 0.1) {
					currentState = previousState;
				} else {
					currentState = "eatGhosts";
				}
				break;
			case "escapeFromGhost":
				if (upState.get(10) < 0.03 || downState.get(10) < 0.03 || leftState.get(10) < 0.03 || rightState.get(10) < 0.03) { //distance to closest ghost
					currentState = previousState;
				} else {
					if (upState.get(4) > 0 || downState.get(4) > 0 || leftState.get(4) > 0 || rightState.get(4) > 0) { //edible
						currentState = "eatGhosts";
					} else {
						currentState = "eatPills";
					}
				}
				break;
			case "eatGhosts":
				if (upState.get(4) > 0 || downState.get(4) > 0 || leftState.get(4) > 0 || rightState.get(4) > 0) { //edible
					currentState = previousState;
				}
				if ((upState.get(10) < 0.03 && upState.get(13) < 1) || (downState.get(10) < 0.03 && downState.get(13) < 1) || (rightState.get(10) < 0.03 && rightState.get(13) < 1) || (leftState.get(10) < 0.03 && leftState.get(13) < 1)) {
					currentState = "escapeFromGhost";
				}
				if (upState.get(4) < 1 || downState.get(4) < 1 || leftState.get(4) < 1 || rightState.get(4) < 1) { //edible
					currentState = "eatPills";
				}
				break;
		}

		//select move based on the currentState:
		switch (currentState) {
			case "eatPills":
				if (facingOneWall(lastMove, upState, downState, leftState, rightState) != -1) {
					move = facingOneWall(lastMove, upState, downState, leftState, rightState);
				} else if (isInCorner(lastMove, upState, downState, leftState, rightState) != -1) {
					move = isInCorner(lastMove, upState, downState, leftState, rightState);
				}
				break;
			case "wait":
				move = waitForGhosts(lastMove);
				break;
			case "eatPowerPill":
				move = eatPowerPill(lastMove, upState, downState, leftState, rightState);
				break;
			case "escapeFromGhost":
				move = escape(lastMove, upState, downState, leftState, rightState);
				break;
			case "eatGhosts":
				move = eatGhosts(lastMove, upState, downState, leftState, rightState);
				/*
				switch(move) {
					case up:
						if (upState.get(13) == 0.0) { //closest ghost is not edible
							if (escape(lastMove, upState, downState, leftState, rightState) != -1) {
								move = escape(lastMove, upState, downState, leftState, rightState);
							}
						}
						break;
					case down:
						if (downState.get(13) == 0.0) { //closest ghost is not edible
							if (escape(lastMove, upState, downState, leftState, rightState) != -1) {
								move = escape(lastMove, upState, downState, leftState, rightState);
							}						
						}
						break;
					case left:
						if (leftState.get(13) == 0.0) { //closest ghost is not edible
							if (escape(lastMove, upState, downState, leftState, rightState) != -1) {
								move = escape(lastMove, upState, downState, leftState, rightState);
							}
							if (isTowardWall(move)) {
								if (!isTowardWall(up)) {
									move = up;
								} else {
									move = down;
								}
							}
						}
						break;
					case right:
						if (rightState.get(13) == 0.0) { //closest ghost is not edible
							if (escape(lastMove, upState, downState, leftState, rightState) != -1) {
								move = escape(lastMove, upState, downState, leftState, rightState);
							}
							if (isTowardWall(move)) {
								if (!isTowardWall(up)) {
									move = up;
								} else {
									move = down;
								}
							}
						}
						break;
				}
				
				if (isTowardWall(move)) {
					if (!isTowardWall(up)) {
						move = up;
					} else {
						move = down;
					}
				}
				*/
				break;
		}
		//set previous variables:
		previousState = currentState;
		lastMove = move;

		if (move == -1) {
			if (!isTowardWall(up)) {
				move = up;
			} else if (!isTowardWall(down)) {
				move = down;
			} else if (!isTowardWall(left)) {
				move = left;
			} else if (!isTowardWall(right)) {
				move = right;
			}
		}
		return move;
	}

	public int facingOneWall(int lastMove, ArrayList<Float> upState, ArrayList<Float> downState, ArrayList<Float> leftState, ArrayList<Float> rightState) {
		int move = -1;
		if (isTowardWall(up) && !isTowardWall(right) && !isTowardWall(left) && !isTowardWall(down)) { //only up wall
			if (lastMove == up) {
				if (leftState.get(7) < rightState.get(7)) {
					if (leftState.get(11) > 0 && leftState.get(12) > 0 && leftState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						move = down; //unsafe move, so reverse
					} else { 
						move = left;
					}
				} else if (leftState.get(7) > rightState.get(7)) {
					if (rightState.get(11) > 0 && rightState.get(12) > 0 && rightState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						move = down; //unsafe move, so reverse
					} else { 
						move = right;
					}
				} else { //equal distance
					if (rightState.get(11) > 0 && rightState.get(12) > 0 && rightState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						if (leftState.get(11) > 0 && leftState.get(12) > 0 && leftState.get(10) < 0.15) { //approaching and no junctions and close to ghost
							move = down; //both moves are unsafe, so reverse
						} else {
							move = left;
						}
					} else { 
						move = right;
					}
				}
			}
		} else if (!isTowardWall(up) && isTowardWall(right) && !isTowardWall(left) && !isTowardWall(down)) { //only right wall
			if (lastMove == right) {
				if (upState.get(7) < downState.get(7)) {
					if (upState.get(11) > 0 && upState.get(12) > 0  && upState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						move = left; //unsafe move, so reverse
					} else { 
						move = up;
					}
				} else if (upState.get(7) > downState.get(7)) {
					if (downState.get(11) > 0 && downState.get(12) > 0  && downState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						move = left; //unsafe move, so reverse
					} else { 
						move = down;
					}
				} else { //equal distance
					if (upState.get(11) > 0 && upState.get(12) > 0  && upState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						if (downState.get(11) > 0 && downState.get(12) > 0  && downState.get(10) < 0.15) { //approaching and no junctions and close to ghost
							move = left; //both moves are unsafe, so reverse
						} else {
							move = down;
						}
					} else { 
						move = up;
					}
				}
			}
		} else if (!isTowardWall(up) && !isTowardWall(right) && isTowardWall(left) && !isTowardWall(down)) { //only left wall
			if (lastMove == left) {
				if (upState.get(7) < downState.get(7)) {
					if (upState.get(11) > 0 && upState.get(12) > 0 && upState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						move = right; //unsafe move, so reverse
					} else { 
						move = up;
					}
				} else if (upState.get(7) > downState.get(7)) {
					if (downState.get(11) > 0 && downState.get(12) > 0 && downState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						move = right; //unsafe move, so reverse
					} else { 
						move = down;
					}
				} else { //equal distance
					if (upState.get(11) > 0 && upState.get(12) > 0 && upState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						if (downState.get(11) > 0 && downState.get(12) > 0 && downState.get(10) < 0.15) { //approaching and no junctions and close to ghost
							move = right; //both moves are unsafe, so reverse
						} else {
							move = down;
						}
					} else { 
						move = up;
					}
				}
			}
		} else if (!isTowardWall(up) && !isTowardWall(right) && !isTowardWall(left) && isTowardWall(down)) { //only down wall
			if (lastMove == down) {
				if (leftState.get(7) < rightState.get(7)) {
					if (leftState.get(11) > 0 && leftState.get(12) > 0 && leftState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						move = up; //unsafe move, so reverse
					} else { 
						move = left;
					}
				} else if (leftState.get(7) > rightState.get(7)) {
					if (rightState.get(11) > 0 && rightState.get(12) > 0 && rightState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						move = up; //unsafe move, so reverse
					} else { 
						move = right;
					}
				} else { //equal distance
					if (rightState.get(11) > 0 && rightState.get(12) > 0 && rightState.get(10) < 0.15) { //approaching and no junctions and close to ghost
						if (leftState.get(11) > 0 && leftState.get(12) > 0 && leftState.get(10) < 0.15) { //approaching and no junctions and close to ghost
							move = up; //both moves are unsafe, so reverse
						} else {
							move = left;
						}
					} else { 
						move = right;
					}
				}
			}
		} else {
			move = -1;
		}
		return move;
	}

	//check if pacman is in the corner and return the correct move based on nearby ghosts
	public int isInCorner(int lastMove, ArrayList<Float> upState, ArrayList<Float> downState, ArrayList<Float> leftState, ArrayList<Float> rightState) {
		int move = -1;
		if (isTowardWall(up) && isTowardWall(right) && !isTowardWall(left) && !isTowardWall(down)) { //up-right corner
			if (lastMove == right) {
				if (downState.get(11) > 0 && downState.get(12) > 0  && downState.get(10) < 0.15) { //approaching and no junctions and close to ghost
					move = left; //unsafe move, so reverse
				} else { 
					//TODO: should check if ghosts are approaching before reversing
					//TODO: should check if there are maze junctions between pacman and the ghost
					//TODO: should check if ghosts are edible
					move = down;
				}
			} else if (lastMove == up) {
				if (leftState.get(11) > 0  && leftState.get(12) > 0  && leftState.get(10) < 0.15) { //approaching and no junctions and close to ghost
					move = down; //unsafe move, so reverse
				} else { 
					//TODO: should check if ghosts are approaching before reversing
					move = left;
				}
			} else {
				//TODO: Improve here
				move = down;
			}
		} else if (isTowardWall(down) && isTowardWall(right) && !isTowardWall(left) && !isTowardWall(up)) { //down-right corner
			if (lastMove == right) {
				if (upState.get(11) > 0  && upState.get(12) > 0  && upState.get(10) < 0.15) { //approaching and no junctions and close to ghost
					move = left; //unsafe move, so reverse
				} else { 
					//TODO: should check if ghosts are approaching before reversing
					move = up;
				}
			} else if (lastMove == down) {
				if (leftState.get(11) > 0  && leftState.get(12) > 0  && leftState.get(10) < 0.15) { //approaching and no junctions and close to ghost
					move = up; //unsafe move, so reverse
				} else { 
					//TODO: should check if ghosts are approaching before reversing
					move = left;
				}
			} else {
				//TODO: Improve here
				move = up;
			}
		} else if (isTowardWall(up) && isTowardWall(left) && !isTowardWall(down) && !isTowardWall(right)) { //up-left corner
			if (lastMove == left) {
				if (downState.get(11) > 0 && downState.get(12) > 0  && downState.get(10) < 0.15) { //approaching and no junctions and close to ghost
					move = right; //unsafe move, so reverse
				} else { 
					//TODO: should check if ghosts are approaching before reversing
					move = down;
				}
			} else if (lastMove == up) {
				if (rightState.get(11) > 0  && rightState.get(12) > 0 && rightState.get(10) < 0.15) { //approaching and no junctions and close to ghost
					move = down; //unsafe move, so reverse
				} else { 
					//TODO: should check if ghosts are approaching before reversing
					move = right;
				}
			} else {
				//TODO: Improve here
				move = down;
			}
		} else if (isTowardWall(down) && isTowardWall(left) && !isTowardWall(right) && !isTowardWall(up)) { //down-left corner
			if (lastMove == left) {
				if (upState.get(11) > 0  && upState.get(12) > 0 && upState.get(10) < 0.15) { //approaching and no junctions and close to ghost
					move = right; //unsafe move, so reverse
				} else { 
					//TODO: should check if ghosts are approaching before reversing
					move = up;
				}
			} else if (lastMove == down) {
				if (rightState.get(11) > 0  && rightState.get(12) > 0  && rightState.get(10) < 0.15) { //approaching and no junctions and close to ghost
					move = up; //unsafe move, so reverse
				} else { 
					//TODO: should check if ghosts are approaching before reversing
					move = right;
				}
			} else {
				//TODO: Improve here
				move = right;
			}
		} else { //not in a corner
			move = -1;
		}
		return move;
	}

	//function to determine the next move(s) after the wait state
	public int eatPowerPill(int lastMove, ArrayList<Float> upState, ArrayList<Float> downState, ArrayList<Float> leftState, ArrayList<Float> rightState) {
		int move = lastMove;
		int closestGhostDirection = getDirectionToClosestGhost(false, upState, downState, leftState, rightState);
		//check for the direction of the power pill:
		if (upState.get(8) < 0.2) {
			if (closestGhostDirection == down) {
				move = escape(lastMove, upState, downState, leftState, rightState);
			} else {
				//move towards ghost:
				move = up;
			}
		} else if (downState.get(8) < 0.2) { 
			if (closestGhostDirection == up) {
				move = escape(lastMove, upState, downState, leftState, rightState);
			} else {
				//move towards ghost:
				move = down;
			}
		}
		return move;
	}

	//function that escapes from the closest ghost. This function only gets called if a ghost is very close to pacman
	public int escape(int lastMove, ArrayList<Float> upState, ArrayList<Float> downState, ArrayList<Float> leftState, ArrayList<Float> rightState) {
		int move = -1;
		if (upState.get(10) < 0.1) {
			move = down;
			//go to nearby regular pills if there are any:
			if (leftState.get(7) < rightState.get(7) && leftState.get(7) < downState.get(7)) {
				move = left;
			} else if (rightState.get(7) < leftState.get(7) && rightState.get(7) < downState.get(7)) {
				move = right;
			} else {
				move = down;
			}
		} else if (downState.get(10) < 0.1) {
			move = up;
			//go to nearby regular pills if there are any:
			if (leftState.get(7) < rightState.get(7) && leftState.get(7) < upState.get(7)) {
				move = left;
			} else if (rightState.get(7) < leftState.get(7) && rightState.get(7) < upState.get(7)) {
				move = right;
			} else {
				move = up;
			}
		} else if (leftState.get(10) < 0.1) {
			move = right;
			//go to nearby regular pills if there are any:
			if (upState.get(7) < downState.get(7) && upState.get(7) < rightState.get(7)) {
				move = up;
			} else if (downState.get(7) < upState.get(7) && downState.get(7) < rightState.get(7)) {
				move = down;
			} else {
				move = right;
			}
		} else if (rightState.get(10) < 0.1) { //right
			move = left;
			//go to nearby regular pills if there are any:
			if (upState.get(7) < downState.get(7) && upState.get(7) < leftState.get(7)) {
				move = up;
			} else if (downState.get(7) < upState.get(7) && downState.get(7) < leftState.get(7)) {
				move = down;
			} else {
				move = left;
			}
		}
		return move;
	}

	//this function only gets called if pacman is close to a power pill and no ghosts are near him
	public int waitForGhosts(int lastMove) {
		//stall pacman next to a power pill:
		switch (lastMove) {
			case up: return down;
			case down: return up;
			case left: return right;
			case right: return left;
		}
		//will never hit this return statement:
		return lastMove;
	}

	//function that chases and eats the closest ghost. This function only gets called if ghosts are edible
	public int eatGhosts(int lastMove, ArrayList<Float> upState, ArrayList<Float> downState, ArrayList<Float> leftState, ArrayList<Float> rightState) {
		int move = getDirectionToClosestGhost(true, upState, downState, leftState, rightState);
		return move;
	}

	//function that returns the direction to the closest ghost (edible or non-edible depending on the boolean value findEdibleGhost) 
	public int getDirectionToClosestGhost(boolean findEdibleGhost, ArrayList<Float> upState, ArrayList<Float> downState, ArrayList<Float> leftState, ArrayList<Float> rightState) {
		//declare variables based on walls:
		float distanceToGhostAbove;
		float distanceToGhostBelow;
		float distanceToGhostRight;
		float distanceToGhostLeft;
		if (findEdibleGhost) {
			boolean ghostIsEdible = false;
			int i = 0;
			float smallest = 99999;
			String direction;
			while (ghostIsEdible == false && i <= 12) {
				if (!isTowardWall(up)) {
					distanceToGhostAbove = upState.get(10+i);
				} else {
					distanceToGhostAbove = 99999;
				}
				if (!isTowardWall(down)) {
					distanceToGhostBelow = downState.get(10+i);
				} else {
					distanceToGhostBelow = 99999;
				}
				if (!isTowardWall(right)) {
					distanceToGhostRight = rightState.get(10+i);
				} else {
					distanceToGhostRight = 99999;
				}
				if (!isTowardWall(left)) {
					distanceToGhostLeft = leftState.get(10+i);
				} else {
					distanceToGhostLeft = 99999;
				}
				//find the smallest value:
				smallest = distanceToGhostAbove;
				direction = "up";
				if (distanceToGhostBelow < smallest && downState.get(13+i) > 0) {
					smallest = distanceToGhostBelow;
					direction = "down";
				}
				if (distanceToGhostLeft < smallest && leftState.get(13+i) > 0) {
					smallest = distanceToGhostLeft;
					direction = "left";
				}
				if (distanceToGhostRight < smallest && rightState.get(13+i) > 0) {
					smallest = distanceToGhostRight;
					direction = "right";
				}
				switch (direction) {
					case "up": 
						ghostIsEdible = true;
						return up;
					case "down": 
						ghostIsEdible = true;
						return down;
					case "right":
						ghostIsEdible = true;
						return right;
					case "left":
						ghostIsEdible = true;
						return left;
				}
				i += 4; //increment to the next closest ghost
			} //end of while loop
		} else { //return closest ghost no matter if it is edible or not
			if (!isTowardWall(up)) {
				distanceToGhostAbove = upState.get(10);
			} else {
				distanceToGhostAbove = 99999;
			}
			if (!isTowardWall(down)) {
				distanceToGhostBelow = downState.get(10);
			} else {
				distanceToGhostBelow = 99999;
			}
			if (!isTowardWall(right)) {
				distanceToGhostRight = rightState.get(10);
			} else {
				distanceToGhostRight = 99999;
			}
			if (!isTowardWall(left)) {
				distanceToGhostLeft = leftState.get(10);
			} else {
				distanceToGhostLeft = 99999;
			}
			//find the smallest value:
			float smallest = distanceToGhostAbove;
			String direction = "up";
			if (distanceToGhostBelow < smallest) {
				smallest = distanceToGhostBelow;
				direction = "down";
			}
			if (distanceToGhostLeft < smallest) {
				smallest = distanceToGhostLeft;
				direction = "left";
			}
			if (distanceToGhostRight < smallest) {
				smallest = distanceToGhostRight;
				direction = "right";
			}
			//return correct direction:
			switch (direction) {
				case "up": return up;
				case "down": return down;
				case "right": return right;
				case "left": return left;
			}
		}
		//will never hit this return statement:
		return -1; 
	}

	//function that returns the correct move when pacman is at a junction
	public int atJunction(int lastMove, ArrayList<Float> upState, ArrayList<Float> downState, ArrayList<Float> leftState, ArrayList<Float> rightState) {
		int move = -1;
		if (lastMove == left && leftState.get(9) == 1) { //9: nearest maze junction
			if (!isTowardWall(left)) {
				if (!isTowardWall(up) && !isTowardWall(down)) {
	 				if (leftState.get(7) < upState.get(7) && leftState.get(7) < downState.get(7)) { //left is the closest pill
	 					move = left;
	 				} else if (downState.get(7) < upState.get(7) && downState.get(7) < leftState.get(7)) { //down is the closest pill
	 					move = down;
	 				} else if (upState.get(7) < downState.get(7) && upState.get(7) < leftState.get(7)) { //up is the closest pill
	 					move = up;
	 				} else { //some pill distances are equal
	 					//determine based on approaching ghosts
	 					if (leftState.get(11) > 0) { //TODO: might want to include third and fourth closest ghost
	 						move = left;
	 					} else if (upState.get(11) > 0) { //no ghost up
	 						move = up;
	 					} else if (downState.get(11) > 0) { //no ghost down
	 						move = down;
	 					} else {
	 						move = right; //reverse if ghosts are approaching from all directions
	 					}
	 				}
				} else if (!isTowardWall(up) && isTowardWall(down)) {
					//check distances to nearest regular pill:
					if (leftState.get(7) < upState.get(7)) { 
						move = left;
					} else if (leftState.get(7) > upState.get(7)) {
						move = up;
					} else { //equal distance
						//determine based on approaching ghosts
						if (leftState.get(11) > 0) { //no ghost left
							move = left;
						} else if (upState.get(11) > 0) { //no ghost up
							move = up;
						} else { //ghost up and left
							move = right; //reverse if ghosts are approaching from all directions
						}
					}
				} else if (!isTowardWall(down) && isTowardWall(up)) {
					//check distances to nearest regular pill:
					if (leftState.get(7) < downState.get(7)) { 
						move = left;
					} else if (leftState.get(7) > downState.get(7)) {
						move = down;
					} else { //equal distance
						//determine based on approaching ghosts
						if (leftState.get(11) > 0) { //no ghost left
							move = left;
						} else if (downState.get(11) > 0) { //no ghost up
							move = down;
						} else { //ghost up and left
							move = right; //reverse if ghosts are approaching from all directions
						}
					}
				} else {
					move = left;
				}
			} else { //is towards left wall
				if (!isTowardWall(up) && !isTowardWall(down)) {
					if (upState.get(7) < downState.get(7)) {
						move = up;
					} else if (upState.get(7) > downState.get(7)) {
						move = down;
					} else { //equal distance, so check for ghosts
						if (downState.get(11) > 0) { //no ghost from down
							move = down;
						} else if (upState.get(11) > 0) { //no ghost from up
							move = up;
						} else { //reverse
							move = right;
						}
					}
				} else if (isTowardWall(up) && !isTowardWall(down)) {
					if (downState.get(11) < 1) {
						move = down;
					} else {
						move = right;
					}
				} else if (!isTowardWall(up) && isTowardWall(down)) {
					if (upState.get(11) < 1) {
						move = up;
					} else {
						move = right;
					}
				}
			}
		} else if (lastMove == right && rightState.get(9) == 1) { //9: nearest maze junction
			if (!isTowardWall(right)) {
				if (!isTowardWall(up) && !isTowardWall(down)) { //4 way junction
					if (rightState.get(7) < upState.get(7) && rightState.get(7) < downState.get(7)) { //left is the closest pill
	 					move = right;
	 				} else if (downState.get(7) < upState.get(7) && downState.get(7) < rightState.get(7)) { //down is the closest pill
	 					move = down;
	 				} else if (upState.get(7) < downState.get(7) && upState.get(7) < rightState.get(7)) { //up is the closest pill
	 					move = up;
	 				} else { //some pill distances are equal
	 					//determine based on approaching ghosts
	 					if (rightState.get(11) > 0) { //TODO: might want to include third and fourth closest ghost
	 						move = right;
	 					} else if (upState.get(11) > 0) { //no ghost up
	 						move = up;
	 					} else if (downState.get(11) > 0) { //no ghost down
	 						move = down;
	 					} else {
	 						move = left; //reverse if ghosts are approaching from all directions
	 					}
	 				}
				} else if (!isTowardWall(up) && isTowardWall(down)) { //3-way junction
					//check distances to nearest regular pill:
					if (rightState.get(7) < upState.get(7)) { 
						move = right;
					} else if (rightState.get(7) > upState.get(7)) {
						move = up;
					} else { //equal distance
						//determine based on approaching ghosts
						if (rightState.get(11) > 0) { //no ghost left
							move = left;
						} else if (upState.get(11) > 0) { //no ghost up
							move = up;
						} else { //ghost up and left
							move = left; //reverse if ghosts are approaching from all directions
						}
					}
				} else if (isTowardWall(up) && !isTowardWall(down)) { //3-way junction
					//check distances to nearest regular pill:
					if (rightState.get(7) < downState.get(7)) { 
						move = right;
					} else if (rightState.get(7) > downState.get(7)) {
						move = down;
					} else { //equal distance
						//determine based on approaching ghosts
						if (rightState.get(11) > 0) { //no ghost left
							move = right;
						} else if (downState.get(11) > 0) { //no ghost up
							move = down;
						} else { //ghost up and left
							move = left; //reverse if ghosts are approaching from all directions
						}
					}
				} else {
					move = right;
				}
			} else {
				if (!isTowardWall(up) && !isTowardWall(down)) { //3-way
					if (upState.get(7) < downState.get(7)) {
						move = up;
					} else if (upState.get(7) > downState.get(7)) {
						move = down;
					} else { //equal distance, so check for ghosts
						if (downState.get(11) > 0) { //no ghost from down
							move = down;
						} else if (upState.get(11) > 0) { //no ghost from up
							move = up;
						} else { //reverse
							move = left;
						}
					}
				}
			}
		} else if (lastMove == down && downState.get(9) == 1) { //9: nearest maze junction
			if (!isTowardWall(down)) {
				if (!isTowardWall(left) && !isTowardWall(right)) { //4 way junction
					if (downState.get(7) < leftState.get(7) && downState.get(7) < rightState.get(7)) { 
	 					move = down;
	 				} else if (rightState.get(7) < downState.get(7) && rightState.get(7) < leftState.get(7)) { 
	 					move = right;
	 				} else if (leftState.get(7) < downState.get(7) && leftState.get(7) < rightState.get(7)) { 
	 					move = left;
	 				} else { //some pill distances are equal
	 					//determine based on approaching ghosts
	 					if (downState.get(11) > 0) { //TODO: might want to include third and fourth closest ghost
	 						move = down;
	 					} else if (leftState.get(11) > 0) { //no ghost up
	 						move = left;
	 					} else if (rightState.get(11) > 0) { //no ghost down
	 						move = right;
	 					} else {
	 						move = up; //reverse if ghosts are approaching from all directions
	 					}
	 				}
				} else if (!isTowardWall(left) && isTowardWall(right)) { //3-way junction
					//check distances to nearest regular pill:
					if (leftState.get(7) < downState.get(7)) { 
						move = left;
					} else if (leftState.get(7) > downState.get(7)) {
						move = down;
					} else { //equal distance
						//determine based on approaching ghosts
						if (downState.get(11) > 0) { 
							move = down;
						} else if (leftState.get(11) > 0) { 
							move = left;
						} else { 
							move = up; //reverse if ghosts are approaching from all directions
						}
					}
				} else if (isTowardWall(left) && !isTowardWall(right)) { //3-way junction
					//check distances to nearest regular pill:
					if (rightState.get(7) < downState.get(7)) { 
						move = right;
					} else if (rightState.get(7) > downState.get(7)) {
						move = down;
					} else { //equal distance
						//determine based on approaching ghosts
						if (rightState.get(11) > 0) { //no ghost left
							move = right;
						} else if (downState.get(11) > 0) { //no ghost up
							move = down;
						} else { //ghost up and left
							move = up; //reverse if ghosts are approaching from all directions
						}
					}
				} else {
					move = down;
				}
			} else { //is facing down wall
				if (!isTowardWall(right) && !isTowardWall(left)) { //3-way
					if (rightState.get(7) < leftState.get(7)) {
						move = right;
					} else if (rightState.get(7) > leftState.get(7)) {
						move = left;
					} else { //equal distance, so check for ghosts
						if (rightState.get(11) > 0) { 
							move = right;
						} else if (leftState.get(11) > 0) { 
							move = left;
						} else { //reverse
							move = up;
						}
					}
				}
			}
		} else if (lastMove == up && upState.get(9) == 1) { //9: nearest maze junction
			if (!isTowardWall(up)) {
				if (!isTowardWall(left) && !isTowardWall(right)) { //4 way junction
					if (upState.get(7) < leftState.get(7) && upState.get(7) < rightState.get(7)) { 
	 					move = up;
	 				} else if (rightState.get(7) < upState.get(7) && rightState.get(7) < leftState.get(7)) { 
	 					move = right;
	 				} else if (leftState.get(7) < upState.get(7) && leftState.get(7) < rightState.get(7)) { 
	 					move = left;
	 				} else { //some pill distances are equal
	 					//determine based on approaching ghosts
	 					if (upState.get(11) > 0) { //TODO: might want to include third and fourth closest ghost
	 						move = up;
	 					} else if (leftState.get(11) > 0) { 
	 						move = left;
	 					} else if (rightState.get(11) > 0) { 
	 						move = right;
	 					} else {
	 						move = down; //reverse if ghosts are approaching from all directions
	 					}
	 				}
				} else if (!isTowardWall(left) && isTowardWall(right)) { //3-way junction
					//check distances to nearest regular pill:
					if (leftState.get(7) < upState.get(7)) { 
						move = left;
					} else if (leftState.get(7) > upState.get(7)) {
						move = up;
					} else { //equal distance
						//determine based on approaching ghosts
						if (upState.get(11) > 0) { 
							move = up;
						} else if (leftState.get(11) > 0) { 
							move = left;
						} else { 
							move = down; //reverse if ghosts are approaching from all directions
						}
					}
				} else if (isTowardWall(left) && !isTowardWall(right)) { //3-way junction
					//check distances to nearest regular pill:
					if (rightState.get(7) < upState.get(7)) { 
						move = right;
					} else if (rightState.get(7) > upState.get(7)) {
						move = up;
					} else { //equal distance
						//determine based on approaching ghosts
						if (rightState.get(11) > 0) { //no ghost left
							move = right;
						} else if (upState.get(11) > 0) { //no ghost up
							move = up;
						} else { //ghost up and left
							move = down; //reverse if ghosts are approaching from all directions
						}
					}
				} else {
					move = up;
				}
			} else { //is facing up wall
				if (!isTowardWall(right) && !isTowardWall(left)) { //3-way
					if (rightState.get(7) < leftState.get(7)) {
						move = right;
					} else if (rightState.get(7) > leftState.get(7)) {
						move = left;
					} else { //equal distance, so check for ghosts
						if (rightState.get(11) > 0) { 
							move = right;
						} else if (leftState.get(11) > 0) { 
							move = left;
						} else { //reverse
							move = down;
						}
					}
				}
			}
		} else {
			move = -1;
		}
		return move;
	}

	//function that checks if pacman is on a direct path and returns the correct move based on approaching ghosts
	public int isInTunnel(int lastMove, ArrayList<Float> upState, ArrayList<Float> downState, ArrayList<Float> leftState, ArrayList<Float> rightState) {
		int move = -1;
		if (lastMove == up && isTowardWall(left) && isTowardWall(right)) { //tunnel going up
			if (upState.get(11) > 0) { //closest ghost approaching
				move = down;
			} else {
				move = up;
			}
		} else if (lastMove == down && isTowardWall(left) && isTowardWall(right)) { //tunnel going down
			if (downState.get(11) > 0) { //closest ghost approaching
				move = up;
			} else {
				move = down;
			}
		} else if (lastMove == left && isTowardWall(down) && isTowardWall(up)) { //tunnel going left
			if (leftState.get(11) > 0) { //closest ghost approaching
				move = right;
			} else {
				move = left;
			}
		} else if (lastMove == right && isTowardWall(down) && isTowardWall(up)) { //tunnel going right
			if (rightState.get(11) > 0) { //closest ghost approaching
				move = left;
			} else {
				move = right;
			}
		} else {
			move = -1;
		}
		return move;
	}

	/***********************************************************************************************************************/
	public static void main(String[] args) {
		B00697153PacMan pacman = new B00697153PacMan(args);
		double sum = 0, avg = 0;
		for (int g = 0; g < pacman.numGamesToPlay(); g++) { //loop over number of games
			pacman.initGame();
			while (!pacman.gameOver()) {
				try {
					Thread.sleep(1);
				}catch(Exception e) {
					System.out.println(e);
				}
			}
			System.out.println("Game: " + (g+1) + " Score: " + pacman.gameScore());
			sum += pacman.gameScore();
		}
		System.out.println("Average Score: " + (sum / pacman.numGamesToPlay()));
		pacman.exit();
		System.exit(0);
	}
}
