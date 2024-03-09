package DBmanager;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

public class DataBaseManager {
    String collectionName;
    public void startDB(){
        String uri = "mongodb://localhost";
        // Construct a ServerApi instance using the ServerApi.builder() method
        ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(uri))
                .serverApi(serverApi).build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase("RailwayDB");
            try {
                // Send a ping to confirm a successful connection
                Bson command = new BsonDocument("ping", new BsonInt64(1));
                Document commandResult = database.runCommand(command);
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");

                MongoCollection<Document> collection = database.getCollection(collectionName);
                Document doc = new Document("train","RE5");
                collection.insertOne(doc);
                System.out.println("Insert compelted");
            } catch (MongoException me) {
                System.err.println(me);
            }
        }
    }

    public void setCollectionName(String collectionName){
        this.collectionName = collectionName;
        startDB();
    }
}
