package gutigame.model;

import java.util.ArrayList;
import java.util.List;

public class Node implements BoardNode {

    private int occupiedBy = 0;
    private final List<int[]> neighbours = new ArrayList<>(); //list of {row, col}

    public int getOccupiedBy() { return occupiedBy; }
    public void setOccupiedBy(int player) { occupiedBy = player; }
    public void addNeighbour(int r, int c) { neighbours.add(new int[]{r, c}); }
    public List<int[]> getNeighbours() { return neighbours; }
}
