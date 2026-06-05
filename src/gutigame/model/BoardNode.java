package gutigame.model;

import java.util.List;

public interface BoardNode {
    int getOccupiedBy();
    void setOccupiedBy(int player);
    void addNeighbour(int r, int c);
    List<int[]> getNeighbours();
}