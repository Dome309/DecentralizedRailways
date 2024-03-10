package DBmanager;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Date;

public class DataBaseManager {
    private String uri = "mongodb://localhost";
    private String nameDB = "RailwayDB";
    private String collectionName;
    private String trainId;
    private String trainType;
    private String attribute;
    private String status;
    private String data;
    private MqttMessage messageMqtt;
    private String message;
    private Date date;
    public void startDB(){
        //Construct a ServerApi instance using the ServerApi.builder() method
        ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(uri))
                .serverApi(serverApi).build();
        //Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase(nameDB);
            try {
                MongoCollection<Document> collection = database.getCollection(collectionName);
                splitMessage();
                Document doc = new Document("date", date)
                        .append("trainId", "TO BE DEFINED")
                        .append("trainType", "TO BE DEFINED")
                        .append("attribute", attribute)
                        .append("status", "TO BE DEFINED")
                        .append("data", data);
                collection.insertOne(doc);
                System.out.println("Insert completed");
            } catch (MongoException me) {
                System.err.println(me);
            }
        }
    }

    public void setCollectionName(String collectionName, MqttMessage messageMqtt, Date date){
        this.collectionName = collectionName;
        this.messageMqtt = messageMqtt;
        this.date = date;
        startDB();
    }

    private void splitMessage(){
        message = new String(messageMqtt.getPayload());
        String[] splitMsg = message.split(": ");
        attribute = splitMsg[0];
        data = splitMsg[1];
    }
}
