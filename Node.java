import static Helpers.Helpers.RelDir;

// 0 = hydrophil, "white"
// 1 = hydrophob, "black"

class Node {
    private RelDir direction;
    private boolean isHydrophobic;
    private int x, y;

    public Node(RelDir direction, boolean isHydrophobic) {
        this.direction = direction;
        this.isHydrophobic = isHydrophobic;
    }

    public RelDir getDirection() {
        return this.direction;
    }

    public boolean getIsHydrophobic() {
        return this.isHydrophobic;
    }

    public void setIsHydrophobic(boolean isHydrophobic) {
        this.isHydrophobic = isHydrophobic;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDirection(RelDir direction) {
        this.direction = direction;
    }

}