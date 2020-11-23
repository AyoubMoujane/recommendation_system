package recommender_system;

public class Entry implements Comparable{

    private int key;
    private float rating;
    public int compareTo(Object o) {
        return getRating() > ((Entry) o).getRating() ? 1 : -1;
    }

    public Entry(int key, float rating){
        this.key = key;
        this.rating = rating;
    }

    public String toString(){
        return "Movie:"+getKey()+", Expected rating:"+getRating();
    }

    public float getRating(){
        return this.rating;
    }
    public float getKey(){
        return this.key;
    }
}
