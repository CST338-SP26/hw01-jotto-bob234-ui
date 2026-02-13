/**
 * Make a branch and start! :)
 */
public class Driver {
    public static void main(String[] args) {
        String filepath = "wordList.txt";  // change if your file has a different name
        Jotto game = new Jotto(filepath);
        game.play();
    }
}
