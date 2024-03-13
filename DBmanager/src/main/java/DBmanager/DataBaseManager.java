package DBmanager;

import java.util.Date;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.json.simple.JSONObject;

public class DataBaseManager {
    private String host = "localhost";
    private String nameDB = "RailwayDB";
    private String collectionName;
    private Date date;
    private JSONObject jsonMessage;
    public void startDB() {

        try (MongoClient mongoClient = new MongoClient(host, 27017)) {

            MongoDatabase database = mongoClient.getDatabase(nameDB);

            MongoCollection<Document> collection = database.getCollection(collectionName);

            Document document = new Document()
                    .append("train", jsonMessage.get("Train"))
                    .append("speed", jsonMessage.get("Speed"))
                    .append("temp", jsonMessage.get("Temperature"))
                    .append("door", jsonMessage.get("Door status"))
                    .append("light", jsonMessage.get("Light status"))
                    .append("date", date);
            collection.insertOne(document);
            System.out.println("Insert completed");
        } catch (Exception exe) {
            exe.printStackTrace();
        }
    }

    public void setCollectionName(String collectionName, JSONObject jsonMessage, Date date){
        this.collectionName = collectionName;
        this.jsonMessage = jsonMessage;
        this.date = date;
        startDB();
    }
}
