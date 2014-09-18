package ninit.org.lucenesorted;

public class RandomDocument {

    private int id;
    private float score;

    public RandomDocument(int id, float score) {
        this.id = id;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public float getScore() {
        return score;
    }

    public String toString() {
        return "ID=" + this.id + ",SCORE=" + this.score;
    }

}
