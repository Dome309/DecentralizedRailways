package DBmanager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    //insert data to DB
    public void insertTrainLog() {
        String databaseName = "RailwayDB";
        try (MongoClient mongoClient = new MongoClient("localhost", 27017)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = localDateTime.format(formatter);

            Document document = new Document()
                    .append("logLevel", jsonMessage.get("logLevel"))
                    .append("train", jsonMessage.get("Train"))
                    .append("speed", jsonMessage.get("Speed"))
                    .append("temp", jsonMessage.get("Temperature"))
                    .append("door", jsonMessage.get("Door status"))
                    .append("light", jsonMessage.get("Light status"))
                    .append("date", formattedDate);
            collection.insertOne(document);
            logger.info("{} data insert completed", databaseName);
        } catch (Exception exe) {
            logger.error("Database insert failed");
        }
    }

    public void setCollectionName(String collectionName, JSONObject jsonMessage, Date date){
        this.collectionName = collectionName;
        this.jsonMessage = jsonMessage;
        this.date = date;
        insertTrainLog();
    }
}
