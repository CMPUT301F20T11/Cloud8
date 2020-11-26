package com.example.booktracker.boundary;

import androidx.annotation.NonNull;

import com.example.booktracker.control.QueryOutputCallback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.QueryOutput;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UpdateQuery {
    private FirebaseFirestore db;
    private long count;
    public UpdateQuery() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Updates a book in firestore if it exist
     *
     * @param oldBook  oldBook containing necessary data
     * @param callback callback function to be called to show the result of
     *                 query
     * @param data     data containing the updates to be made to the book
     */
    public void updateBook(Book oldBook, QueryOutputCallback callback,
                           HashMap<String, Object> data,
                           QueryOutput queryOutput) {
        DocumentReference bookRef =
                db.collection("books").document(oldBook.getIsbn());
        bookRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot res = task.getResult();
                        if (res.exists()){
                            bookRef.update(data);
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                queryOutput.setOutput("Book " +
                        "successfully edited");
                callback.displayQueryResult(
                        "successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                queryOutput.setOutput("Book " +
                        "was  not edited");
                callback.displayQueryResult(
                        "not successful");
            }
        });
    }

    /**
     * This will change the status of the book by moving it to a different collection in the
     * user document
     * @param oldId id of old book this can be isbn or id for incomingRequest
     * @param newId id of old book this can be isbn or id for incomingRequest
     * @param oldStatus
     * @param newStatus
     * @param user email of the user which contains the book
     */
    public void changeBookStatus(String oldId,String newId,String newStatus,String user,String oldStatus){
            DocumentReference userDoc =  db.collection("users").document(user);
            String isbn = oldId.length() == 13 ? oldId : newId;
            DocumentReference bookRef = db.collection("books").document(isbn);
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("bookReference",bookRef);
            userDoc.collection(newStatus).document(newId).set(data);
            userDoc.collection(oldStatus).document(oldId).delete();
    }

    /**
     * This will increment the counter for the notifications for Accepted Books
     * @param user
     */
    public void incrementNotif(String user,String notifId){
        count = 0;
        DocumentReference userDoc =  db.collection("users").document(user);
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = (DocumentSnapshot) task.getResult();
                count = (long) res.get(notifId);
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                HashMap<String,Object> data = new HashMap<String,Object>();
                count = count + 1 ;
                data.put(notifId,count);
                userDoc.update(data);
            }
        });
    }
    /**
     * This will empty the counter for the notifications for Accepted Books
     * @param user
     */
    public void emptyNotif(String user,String notifId){
        DocumentReference userDoc =  db.collection("users").document(user);
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put(notifId,0);
        userDoc.update(data);
    }
    private void deleteOldBook(CollectionReference colRef,String isbn){
        colRef.document(isbn).delete();
    }
    public void borrowBook(String isbn,String borrower,QueryOutputCallback outputCallback,QueryOutput queryOutput){
        db.collection("users").document(borrower).collection("accepted")
                .document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = task.getResult();
                if (res.exists()){
                    db.collection("books").document(isbn).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot res = task.getResult();
                                    String ownerStat = res.getString("ownerStatus");
                                    if ( ownerStat != null && ownerStat.equals("unavailable")){
                                        //this means that the owner has already scaneed the book and lent it
                                        HashMap<String,Object> data = new HashMap<String,Object>();
                                        data.put("borrower",borrower);
                                        data.put("borrowerStatus","unavailable");
                                        data.put("status","unavailable");
                                        db.collection("books").document(isbn).update(data);
                                        changeBookStatus(isbn,isbn,"borrowed",borrower,"accepted");
                                        changeBookStatus(isbn,isbn,"lent",res.getString("owner"),"accepted");
                                        queryOutput.setOutput("Book successfully borrowed");
                                        outputCallback.displayQueryResult("successful");

                                    }else{
                                        HashMap<String,Object> data = new HashMap<String,Object>();
                                        data.put("borrower",borrower);
                                        data.put("borrowerStatus","unavailable");
                                        queryOutput.setOutput("pending to be accepted by the owner");
                                        outputCallback.displayQueryResult("successful");
                                    }
                                }
                            });

                }else{
                    queryOutput.setOutput("Book could not be borrowed");
                    outputCallback.displayQueryResult("not successful");
                }
            }
        });
    }
    public void lendBook(String isbn,String owner,QueryOutputCallback outputCallback,QueryOutput queryOutput){
        db.collection("users").document(owner).collection("accepted")
                .document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = (DocumentSnapshot) task.getResult();
                if (res.exists()){
                    db.collection("books").document(isbn).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot res = task.getResult();
                                    String borrowerStatus = res.getString("borrowerStatus");
                                    if ( borrowerStatus != null && borrowerStatus.equals("unavailable")){
                                        HashMap<String,Object> data = new HashMap<String,Object>();
                                        data.put("owner","unavailable");
                                        data.put("status","unavailable");
                                        db.collection("books").document(isbn).update(data);
                                        changeBookStatus(isbn,isbn,"lent",owner,"accepted");
                                        changeBookStatus(isbn,isbn,"borrowed",res.getString("borrower"),"accepted");
                                        queryOutput.setOutput("Book successfully lent");
                                        outputCallback.displayQueryResult("successful");
                                    }else{
                                        HashMap<String,Object> data = new HashMap<String,Object>();
                                        data.put("owner","unavailable");
                                        db.collection("books").document(isbn).update(data);
                                        queryOutput.setOutput("Pending to be accepted by the borrower");
                                        outputCallback.displayQueryResult("successful");
                                    }
                                }
                            });
                }else {
                    queryOutput.setOutput("Book could not be lent");
                    outputCallback.displayQueryResult("not successful");
                }
            }
        });
    }
    public void returnBook(String isbn,String borrower,QueryOutputCallback outputCallback,QueryOutput queryOutput){
        DocumentReference userRef = db.collection("users").document(borrower);
        userRef.collection("borrowed")
                .document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = task.getResult();
                if (res.exists()){
                    db.collection("books").document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (res.getString("ownerStatus").equals("available") && res.getString("status").equals("unavailable")){
                                HashMap<String,Object> data = new HashMap<String,Object>();
                                data.put("borrower","none");
                                data.put("status","available");
                                db.collection("books").document(isbn).update(data);
                                userRef.collection("borrowed").document(isbn).delete();
                                db.collection("users").document(res.getString("owner"))
                                        .collection("lent").document(isbn).delete();
                                queryOutput.setOutput("Book successfully returned");
                                outputCallback.displayQueryResult("successful");
                            }
                            HashMap<String,Object> newData = new HashMap<String,Object>();
                            newData.put("borrowerStatus","available");
                            db.collection("books").document(isbn).update(newData);
                            queryOutput.setOutput("Pending to be accepted by the owner");
                            outputCallback.displayQueryResult("successful");
                        }
                    });
                }else{
                    queryOutput.setOutput("Book could not be returned");
                    outputCallback.displayQueryResult("not successful");
                }
            }
        });
    }
    public void acceptReturn(String isbn,String owner,QueryOutputCallback outputCallback,QueryOutput queryOutput){
        DocumentReference userRef = db.collection("users").document(owner);
       userRef.collection("lent")
                .document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = (DocumentSnapshot) task.getResult();
                if (res.exists()){
                    db.collection("books").document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (res.getString("borrowerStatus").equals("available") && res.get("status").equals("unavailable")){
                                HashMap<String,Object> data = new HashMap<String,Object>();
                                userRef.collection("borrowed").document(isbn).delete();
                                db.collection("users").document(res.getString("borrower"))
                                        .collection("borrowed").document(isbn).delete();
                                data.put("status","available");
                                data.put("borrower","none");
                                db.collection("books").document(isbn).update(data);
                                queryOutput.setOutput("Book successfully accepted");
                                outputCallback.displayQueryResult("successful");
                            }
                            HashMap<String,Object> newData = new HashMap<String,Object>();
                            newData.put("ownerStatus","available");
                            db.collection("books").document(isbn).update(newData);
                            queryOutput.setOutput("pending to be returned by the borrower");
                            outputCallback.displayQueryResult("successful");
                        }
                    });
                }else{
                    queryOutput.setOutput("Book could not be accepted");
                    outputCallback.displayQueryResult("not successful");
                }
            }
        });
    }
}
