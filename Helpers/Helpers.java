package Helpers;

public class Helpers {

    public static enum RelDir { // Relative directions
        Left(-1), Forward(0), Right(1);

        public final int value;

        private RelDir(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static enum H_Richtung { // Absolute directions -> For moving in the maze
        Nord, Ost, Sued, West
    };
}
