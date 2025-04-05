package com.dynamodbclone.server;
import com.dynamodbclone.DynamoDBCloneGrpc;
import com.dynamodbclone.DynamoDBCloneOuterClass.GetItemRequest;
import com.dynamodbclone.DynamoDBCloneOuterClass.GetItemResponse;
import com.dynamodbclone.DynamoDBCloneOuterClass.PutItemRequest;
import com.dynamodbclone.DynamoDBCloneOuterClass.PutItemResponse;
import com.dynamodbclone.DynamoDBCloneOuterClass.DeleteItemRequest;
import com.dynamodbclone.DynamoDBCloneOuterClass.DeleteItemResponse;
import com.dynamodbclone.DynamoDBCloneOuterClass.Attribute;
import io.grpc.stub.StreamObserver;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.util.HashMap;

public class gRPCServer {
    static private class DynamoDBCloneImp extends DynamoDBCloneGrpc.DynamoDBCloneImplBase {
        HashMap<String, String> myMap = new HashMap<>();

        @Override
        public void getItem(GetItemRequest req, StreamObserver<GetItemResponse> responseObserver) {
            String key = req.getKey();
            String value = myMap.get(key);

            Attribute attribute = Attribute.newBuilder().setValue(value).setKey(key).build();
            GetItemResponse reply = GetItemResponse.newBuilder().setItem(attribute).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void putItem(PutItemRequest req, StreamObserver<PutItemResponse> responseObserver) {
            String added = myMap.put(req.getKey(), req.getValue());

            PutItemResponse reply = PutItemResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void deleteItem(DeleteItemRequest req, StreamObserver<DeleteItemResponse> responseObserver) {
            String removed = myMap.remove(req.getKey());

            DeleteItemResponse reply = DeleteItemResponse.newBuilder().setSuccess(removed != null).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
    public static void main(String[] args) throws Exception {
        // Create and start the server
        Server server = ServerBuilder.forPort(50051)
                .addService(new DynamoDBCloneImp())
                .build();

        System.out.println("Server is running...");
        server.start();

        // Keep the server running
        server.awaitTermination();
    }
}
