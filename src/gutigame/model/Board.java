package gutigame.model;

import java.util.List;

public class Board {

    private final BoardNode[][] grid = new Node[5][5];

    public Board() {
        //Create all 25 nodes
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                grid[r][c] = new Node();

        buildAdjacency();
        resetPieces();
    }

    private void buildAdjacency() {
        int[][][] adj = {
            {{0,1},{1,0},{1,1}},                               // (0,0)
            {{0,0},{0,2},{1,1}},                               // (0,1)
            {{0,1},{0,3},{1,1},{1,2},{1,3}},                   // (0,2)
            {{0,2},{0,4},{1,3}},                               // (0,3)
            {{0,3},{1,3},{1,4}},                               // (0,4)
            {{0,0},{1,1},{2,0}},                               // (1,0)
            {{0,0},{0,1},{0,2},{1,0},{1,2},{2,0},{2,1},{2,2}}, // (1,1)
            {{0,2},{1,1},{1,3},{2,2}},                         // (1,2)
            {{0,2},{0,3},{0,4},{1,2},{1,4},{2,2},{2,3},{2,4}}, // (1,3)
            {{0,4},{1,3},{2,4}},                               // (1,4)
            {{1,0},{1,1},{2,1},{3,0},{3,1}},                   // (2,0)
            {{1,1},{2,0},{2,2},{3,1}},                         // (2,1)
            {{1,1},{1,2},{1,3},{2,1},{2,3},{3,1},{3,2},{3,3}}, // (2,2)
            {{1,3},{2,2},{2,4},{3,3}},                         // (2,3)
            {{1,3},{1,4},{2,3},{3,3},{3,4}},                   // (2,4)
            {{2,0},{3,1},{4,0}},                               // (3,0)
            {{2,0},{2,1},{2,2},{3,0},{3,2},{4,0},{4,1},{4,2}}, // (3,1)
            {{2,2},{3,1},{3,3},{4,2}},                         // (3,2)
            {{2,2},{2,3},{2,4},{3,2},{3,4},{4,2},{4,3},{4,4}}, // (3,3)
            {{2,4},{3,3},{4,4}},                               // (3,4)
            {{3,0},{3,1},{4,1}},                               // (4,0)
            {{3,1},{4,0},{4,2}},                               // (4,1)
            {{3,1},{3,2},{3,3},{4,1},{4,3}},                   // (4,2)
            {{3,3},{4,2},{4,4}},                               // (4,3)
            {{3,3},{3,4},{4,3}}                                // (4,4)
        };

        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                for (int[] nb : adj[r * 5 + c])
                    grid[r][c].addNeighbour(nb[0], nb[1]);
    }

    public void resetPieces() {
        int count = 0;
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (count == 12)    grid[r][c].setOccupiedBy(0);
                else if(count < 12) grid[r][c].setOccupiedBy(1);
                else                grid[r][c].setOccupiedBy(2);
                count++;
            }
        }
    }

    //Node data collection methods
    public int getOccupant(int r, int c) { return grid[r][c].getOccupiedBy(); }
    public void setOccupant(int r, int c, int player) { grid[r][c].setOccupiedBy(player); }
    public List<int[]> getNeighbours(int r, int c) { return grid[r][c].getNeighbours(); }

    public static int opponent(int player) {
        if(player == 0) return 0;
        return (player == 1) ? 2 : 1;
    }

    //Move check

    public boolean isSimpleMove(int fr, int fc, int tr, int tc) {
        if (getOccupant(tr, tc) != 0) return false;
        for (int[] nb : grid[fr][fc].getNeighbours())
            if (nb[0] == tr && nb[1] == tc) return true;
        return false;
    }

    public boolean isCaptureMove(int fr, int fc, int tr, int tc, int player) {
        if (getOccupant(tr, tc) != 0) return false;
        int opp = opponent(player);
        for (int[] mid : grid[fr][fc].getNeighbours()) {
            if (getOccupant(mid[0], mid[1]) != opp) continue;
            int landR = mid[0] + (mid[0] - fr);
            int landC = mid[1] + (mid[1] - fc);
            if (landR == tr && landC == tc && landR >= 0 && landR < 5 && landC >= 0 && landC < 5) return true;
        }
        return false;
    }

    public boolean canJumpAgain(int pr, int pc) {
        int player = getOccupant(pr, pc);
        if (player == 0) return false;
        int opp = opponent(player);
        for (int[] nb : grid[pr][pc].getNeighbours()) {
            if (getOccupant(nb[0], nb[1]) != opp) continue;
            int lr = nb[0] + (nb[0] - pr);
            int lc = nb[1] + (nb[1] - pc);
            if (lr >= 0 && lr < 5 && lc >= 0 && lc < 5 && getOccupant(lr, lc) == 0) return true;
        }
        return false;
    }

    //Update board due to moving

    public void applySimple(int fr, int fc, int tr, int tc) {
        grid[tr][tc].setOccupiedBy(getOccupant(fr, fc));
        grid[fr][fc].setOccupiedBy(0);
    }

    public void applyCapture(int fr, int fc, int tr, int tc) {
        int opp = opponent(getOccupant(fr, fc));
        for (int[] mid : grid[fr][fc].getNeighbours()) {
            if (getOccupant(mid[0], mid[1]) != opp) continue;
            int landR = mid[0] + (mid[0] - fr);
            int landC = mid[1] + (mid[1] - fc);
            if (landR == tr && landC == tc) {
                grid[tr][tc].setOccupiedBy(getOccupant(fr, fc));
                grid[mid[0]][mid[1]].setOccupiedBy(0);
                grid[fr][fc].setOccupiedBy(0);
                return;
            }
        }
    }

    //Game state check

    public int countPieces(int player) {
        int count = 0;
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                if (grid[r][c].getOccupiedBy() == player) count++;
        return count;
    }

    public boolean hasLegalMove(int player) {
        int opp = opponent(player);
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (grid[r][c].getOccupiedBy() != player) continue;
                for (int[] nb : grid[r][c].getNeighbours()) {
                    if (getOccupant(nb[0], nb[1]) == 0) return true;
                    if (getOccupant(nb[0], nb[1]) == opp) {
                        int lr = nb[0] + (nb[0] - r);
                        int lc = nb[1] + (nb[1] - c);
                        if (lr >= 0 && lr < 5 && lc >= 0 && lc < 5 && getOccupant(lr, lc) == 0) return true;
                    }
                }
            }
        }
        return false;
    }
}
