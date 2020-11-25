package recommender_system;
import java.util.*;

public class RecommenderSystem {
    private ArrayList<ArrayList<Float>>users = new ArrayList<ArrayList<Float>>();
    private int nbUsers = 0;
    private int nbItems = 0;

    public ArrayList<ArrayList<Float>> getUsers(){
        return users;
    }

    public ArrayList<Float> getUser(int i){
        return users.get(i);
    }

    // generates 'nbUsers' with 'nbItems' movies having watched and rated
    // a movie with a probability of 'sparsity'
    public void generateData(int nbUsers, int nbItems, float sparsity) {
        this.nbItems = nbItems;
        this.nbUsers = nbUsers;
        ArrayList<ArrayList<Float>> userProfiles = new ArrayList<ArrayList<Float>>();
        ArrayList<ArrayList<Float>> itemsProfiles = new ArrayList<ArrayList<Float>>();
        int profileDimension = 3;
        float maxNote = 5.0f;
        Random r = new Random();
        // creating users profiles
        for (int i = 0 ; i < nbUsers ; i++){
            ArrayList<Float> currentUserProfile = new ArrayList<Float>();

            for(int j = 0 ; j < profileDimension ; j++) {
                currentUserProfile.add((float) (r.nextInt(6)));
            }
            userProfiles.add(currentUserProfile);
        }
        // creating items profiles
        for (int i = 0 ; i < nbItems ; i++){
            ArrayList<Float> currenItemProfile = new ArrayList<Float>();

            for(int j = 0 ; j < profileDimension ; j++) {
                currenItemProfile.add((float) (r.nextFloat()*maxNote));
            }
            itemsProfiles.add(currenItemProfile);
        }

        //generating rates from users and items profiles
        for (int i = 0 ; i < nbUsers ; i++) {
            ArrayList<Float> currentUserNote = new ArrayList<Float>();
            for (int j = 0 ; j < nbItems ; j++) {
                float newNote = r.nextFloat();
                if (newNote < sparsity) {

                    float note = 0.0f;
                    for (int k = 0 ; k < profileDimension ; k++) {
                        note += userProfiles.get(i).get(k)*itemsProfiles.get(j).get(k);

                    }
                    currentUserNote.add((float) r.nextInt(6));
                } else {
                    currentUserNote.add(-1.0f);
                }

            }
            users.add(currentUserNote);
        }

        System.out.println("USERS GENERATED \n");
        for (ArrayList<Float> user: users
             ) {
            System.out.println(user);
        }

    }

    // method used is cosine similarity
    public double computeSimilarity(ArrayList<Float> vector1, ArrayList<Float> vector2) {
        double norm1 = 0;
        double norm2 = 0;
        double prod = 0;
        for(int i = 0; i < vector1.size(); i++) {
            float vectorOne = vector1.get(i) != -1.0f ? vector1.get(i) : 0;
            float vectorTwo = vector2.get(i) != -1.0f ? vector2.get(i) : 0;
            prod += vectorOne * vectorTwo;
            norm1 += Math.pow(vectorOne, 2);
            norm2 += Math.pow(vectorTwo, 2);
        }
        double denom = (Math.sqrt(norm1) * Math.sqrt(norm2));
        if(denom != 0){
            return prod / denom;
        }
        else return 0;
    }

    // method printing the similarity matrix of users
    private void printSimilarityMatrix(){
        System.out.println("*** Similarity matrix ***");
        for (int i = 0; i < nbUsers ; i++) {
            System.out.println("\n");
            for (int j = 0; j < nbUsers; j++) {
                System.out.print(computeSimilarity(getUser(i), getUser(j))+" ");
            }
        }
    }

    // Utility function to get the missing indexes of a user vector
    // missingIndexes.contains(i) users.get(i) <=> == -1
    private ArrayList<Integer> getMissingIndexes(ArrayList<Float> vector1){
        ArrayList<Integer> missingIndexes = new ArrayList<Integer>();
        for (int i = 0; i < vector1.size(); i++) {
            if(vector1.get(i)==-1){
                missingIndexes.add(i);
            }
        }
        return missingIndexes;
    }

    // For a vector returns missing values using weighted average
    // More weight is given to similar users (cosine similarity)
    public ArrayList<Float> returnPredictedUser(int index){
        ArrayList<Float> currentVector = new ArrayList<>(users.get(index));
        ArrayList<Float> returnedVector = new ArrayList<>(users.get(index));
        ArrayList<Integer> missingIndexes = getMissingIndexes(users.get(index));
        double totalWeight=0;
        double totalValue=0;
        double currentWeight;
        double predictedValue;
        // scroll through every users
        for(int i=0;i<missingIndexes.size();i++){
            totalWeight=0;
            totalValue=0;
            // weighted average with other vectors using j index
            for(int j=0;j<users.size();j++){
                // ignore if current vector is the given vector
                // ignore if the value of the current vector index is -1
                if (j!=index && users.get(j).get(missingIndexes.get(i))!=-1){
                    currentWeight=computeSimilarity(currentVector,users.get(j));
                    totalWeight+=currentWeight;
                    totalValue+=users.get(j).get(missingIndexes.get(i))*currentWeight;
                }
            }
            // if every other vector had a -1
            if(totalWeight==0){
                // unable to make a prediction
                predictedValue = -1;
            } else {
                predictedValue = totalValue/totalWeight;
                returnedVector.set(missingIndexes.get(i),(float) predictedValue);
            }
        }
        return returnedVector;
    }

    // returns a list of the top 'limit' movies
    // of the user number 'i' he should watch
    public List<Entry> getTopRecommendation(int index, int limit){
        ArrayList<Float> currentVector = new ArrayList<>(users.get(index));
        ArrayList<Integer> missingIndexes = getMissingIndexes(users.get(index));
        ArrayList<Entry> recommendations = new ArrayList<>();
        double totalWeight=0;
        double totalValue=0;
        double currentWeight;
        double predictedValue;
        // scroll through every missing ratings
        for(int i=0;i<missingIndexes.size();i++){
            totalWeight=0;
            totalValue=0;
            // weighted average with other vectors using j index
            for(int j=0;j<users.size();j++){
                // ignore if current vector is the given vector
                // ignore if the value of the current vector index is -1
                if (j!=index && users.get(j).get(missingIndexes.get(i))!=-1){
                    currentWeight=computeSimilarity(currentVector,users.get(j));
                    totalWeight+=currentWeight;
                    totalValue+=users.get(j).get(missingIndexes.get(i))*currentWeight;
                }
            }
            // if every other vector had a -1
            if(totalWeight==0){
                // unable to make a prediction
                predictedValue = -1;
            } else {

                predictedValue = totalValue/totalWeight;
                recommendations.add(new Entry(missingIndexes.get(i),(float) predictedValue));
            }
        }

        Collections.sort(recommendations,Collections.reverseOrder());
        return recommendations.subList(0,limit);

    }


    public static void main (String[] args){
        RecommenderSystem recSys = new RecommenderSystem();

        recSys.generateData(10, 10, 0.6f);

        // System.out.println(recSys.computeSimilarity(recSys.getUser(0), recSys.getUser(1)));
        // Prints the similarity matrix
        // the diagonal is populated with 1
        // Because the vector is compared with himself
        //recSys.printSimilarityMatrix();

        System.out.println("MISSING RATINGS FOR USER 0 (indexes) :");
        System.out.println(recSys.getMissingIndexes(recSys.getUser(0)));

        System.out.println("PREDICTIONS FOR USER 0 :");
        System.out.println(recSys.returnPredictedUser(0));

        System.out.println("TOP 3 RECOMMENDED MOVIES FOR USER 0 AND THEIR PREDICTED RATINGS:");
        System.out.println(recSys.getTopRecommendation(0,3));

    }
}