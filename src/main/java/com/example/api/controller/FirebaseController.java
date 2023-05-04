package com.example.api.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class FirebaseController {

    @GetMapping(produces = "application/json")
    public String get() throws ExecutionException, InterruptedException {
        List<String> result = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("bbs").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        Gson gson = new Gson();
        for (QueryDocumentSnapshot document : documents){
            System.out.println("id: "+document.getId());
            System.out.println("data: "+document.getData());
            System.out.println("=========================");
            result.add(gson.toJson(document.getData()));
        }
        return result.toString();
    }

    @GetMapping(value = "/{documentId}", produces = "application/json")
    public Callable<String> getById(@PathVariable UUID documentId) throws ExecutionException, InterruptedException {
        System.out.println(documentId);
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference reference = db.collection("bbs").document(documentId.toString());
        ApiFuture<DocumentSnapshot> future = reference.get();
        DocumentSnapshot snapshot = future.get();
        if (snapshot.exists()){
            System.out.println(snapshot.getData());
        }
        Gson gson = new Gson();
        return () -> gson.toJson(snapshot.getData());
    }

    @GetMapping(value = "/{key}/{value}", produces = "application/json")
    public String searchDocuments(@PathVariable String key, @PathVariable String value) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference bbs = db.collection("bbs");
        Query query = bbs.whereEqualTo(key, value);
        ApiFuture<QuerySnapshot> snapshot = query.get();

        List<String> result = new ArrayList<>();
        Gson gson = new Gson();
        for (DocumentSnapshot document : snapshot.get().getDocuments()) {
            result.add(gson.toJson(document.getData()));
        }
        return result.toString();
    }

    @PostMapping
    public String add(@RequestBody HashMap<String, Object> request) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection("bbs").document(UUID.randomUUID().toString()).set(request);
        return future.get().getUpdateTime().toString();
    }


}
