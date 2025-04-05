package com.dynamodbclone.client;
import com.dynamodbclone.DynamoDBCloneGrpc;
import com.dynamodbclone.DynamoDBCloneOuterClass.GetItemRequest;
import com.dynamodbclone.DynamoDBCloneOuterClass.GetItemResponse;
import com.dynamodbclone.DynamoDBCloneOuterClass.PutItemRequest;
import com.dynamodbclone.DynamoDBCloneOuterClass.PutItemResponse;
import com.dynamodbclone.DynamoDBCloneOuterClass.DeleteItemRequest;
import com.dynamodbclone.DynamoDBCloneOuterClass.DeleteItemResponse;
import io.grpc.ManagedChannel;
import com.dynamodbclone.DynamoDBCloneGrpc.DynamoDBCloneBlockingStub;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class gRPCClient {
    private final DynamoDBCloneBlockingStub blockingStub;
    gRPCClient (ManagedChannel channel) {
        this.blockingStub = DynamoDBCloneGrpc.newBlockingStub(channel);
    }

    private void getItem(String key) {
        System.out.println("Client running Get Item using key " + key);
        GetItemRequest req = GetItemRequest.newBuilder().setKey(key).build();
        GetItemResponse response;
        try {
            response = blockingStub.getItem(req);
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC failed to Get Item: " + e.getMessage());
            return;
        }
        System.out.println("Item: " + response.getItem());
    }

    private void putItem(String key, String value) {
        System.out.println("Client running Put Item using key: " + key + " Value: " + value);
        PutItemRequest req = PutItemRequest.newBuilder().setKey(key).setValue(value).build();
        PutItemResponse response;
        try {
            response = blockingStub.putItem(req);
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC failed to Put Item: " + e.getMessage());
            return;
        }
        System.out.println("Success: " + response.getSuccess());
    }

    public void deleteItem(String key) {
        System.out.println("Client running Delete Item using key: " + key);
        DeleteItemRequest req = DeleteItemRequest.newBuilder().setKey(key).build();
        DeleteItemResponse response;
        try {
            response = blockingStub.deleteItem(req);
        } catch (RuntimeException e) {
            System.err.println("gRPC failed to Delete Item: " + e.getMessage());
            return;
        }
        System.out.println("Success: " + response.getSuccess());
    }

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        gRPCClient client = new gRPCClient(channel);
        String key = args.length > 0 ? args[0] : "1";
        String value = "Dog";
        client.putItem(key, value);
        client.getItem(key);
        client.deleteItem(key);
    }

}
