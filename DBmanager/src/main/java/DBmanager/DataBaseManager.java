package DBmanager;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.json.simple.JSONObject;

public class DataBaseManager {
    private String collectionName;
    private Date date;
    private JSONObject jsonMessage;
    private static final Logger logger = LogManager.getLogger(DataBaseManager.class);

    //starting database
    public void insertTrainLog() {
        String databaseName = "RailwayDB";
        try (MongoClient mongoClient = new MongoClient("localhost", 27017)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);
            Document document = new Document()
                    .append("logLevel", jsonMessage.get("logLevel"))
                    .append("train", jsonMessage.get("Train"))
                    .append("speed", jsonMessage.get("Speed"))
                    .append("temp", jsonMessage.get("Temperature"))
                    .append("door", jsonMessage.get("Door status"))
                    .append("light", jsonMessage.get("Light status"))
                    .append("date", date);
            collection.insertOne(document);
            logger.info("{} data insert completed", databaseName);
        } catch (Exception exe) {
            logger.error("Database insert failed");
        }
    }

    //method for creating the collection name on the database
    public void setCollectionName(String collectionName, JSONObject jsonMessage, Date date){
        this.collectionName = collectionName;
        this.jsonMessage = jsonMessage;
        this.date = date;
        insertTrainLog();
    }
}
