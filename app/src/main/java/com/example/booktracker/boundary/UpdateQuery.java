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
import java.util.Map;

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
                        if (res.exists()) {
                            bookRef.update(data);
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                queryOutput.setOutput("Book " +
                        "successfully edited");
                callback.displayQueryResult(
                        "Successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                queryOutput.setOutput("Book " +
                        "was not edited");
                callback.displayQueryResult(
                        "Unsuccessful");
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
    public void changeBookStatus(String oldId, String newId, String newStatus, String user, String oldStatus) {
            DocumentReference userDoc =  db.collection("users").document(user);
            String isbn = oldId.length() == 13 ? oldId : newId;
            DocumentReference bookRef = db.collection("books").document(isbn);
            HashMap<String, Object> data = new HashMap<>();
            data.put("bookReference", bookRef);
            userDoc.collection(newStatus).document(newId).set(data);
            userDoc.collection(oldStatus).document(oldId).delete();
    }

    /**
     * This will increment the counter for the notifications for Accepted Books
     * @param user
     */
    public void incrementNotif(String user, String notifId) {
        count = 0;
        DocumentReference userDoc =  db.collection("users").document(user);
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = task.getResult();
                count = (long) res.get(notifId);
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                HashMap<String, Object> data = new HashMap<>();
                count = count + 1 ;
                data.put(notifId, count);
                userDoc.update(data);
            }
        });
    }

    /**
     * This will empty the counter for the notifications for Accepted Books
     * @param user
     */
    public void emptyNotif(String user, String notifId) {
        DocumentReference userDoc = db.collection("users").document(user);
        HashMap<String, Object> data = new HashMap<>();
        data.put(notifId, 0);
        userDoc.update(data);
    }

    /**
     * This will get the email of the user that is stored in firestore
     * @param owner
     * @return
     */
    private String getEmail(HashMap<String, String> owner) {
        String email = "";
        for (Map.Entry<String, String> entry: owner.entrySet()){
            email = entry.getKey();
        }
        return email;
    }

    private void deleteOldBook(CollectionReference colRef, String isbn){
        colRef.document(isbn).delete();
    }

    public void borrowBook(String isbn, String borrower, QueryOutputCallback outputCallback, QueryOutput queryOutput){
        db.collection("users").document(borrower).collection("accepted")
                .document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = task.getResult();
                if (res.exists()) {
                    db.collection("books").document(isbn).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot res = task.getResult();
                                    String ownerStat = res.getString("ownerStatus");
                                    if (ownerStat != null && ownerStat.equals("unavailable")) {
                                        //this means that the owner has already scanned the book and lent it
                                        HashMap<String, Object> data = new HashMap<>();
                                        data.put("borrower", borrower);
                                        data.put("borrowerStatus", "unavailable");
                                        data.put("status", "borrowed");
                                        db.collection("books").document(isbn).update(data);

                                        changeBookStatus(isbn, isbn,"borrowed", borrower, "accepted");
                                        HashMap<String, String> owner = (HashMap<String, String>)res.get("owner");
                                        db.collection("users")
                                                .document(getEmail(owner))
                                                .collection("available")
                                                .document(isbn).delete();
                                        changeBookStatus(isbn, isbn, "lent", getEmail(owner), "accepted");
                                        queryOutput.setOutput("Book successfully borrowed");
                                        outputCallback.displayQueryResult("Successful");

                                    } else {
                                        HashMap<String, Object> data = new HashMap<>();
                                        data.put("borrowerStatus", "unavailable");
                                        data.put("potentialBorrower", borrower);
                                        db.collection("books")
                                                .document(isbn).update(data);
                                        queryOutput.setOutput("Pending to be accepted by the owner");
                                        outputCallback.displayQueryResult("Successful");
                                    }
                                }
                            });

                } else {
                    queryOutput.setOutput("Book could not be borrowed");
                    outputCallback.displayQueryResult("Unsuccessful");
                }
            }
        });
    }

    public void lendBook(String isbn, String owner, QueryOutputCallback outputCallback, QueryOutput queryOutput) {
        db.collection("users").document(owner).collection("accepted")
                .document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = task.getResult();
                if (res.exists()) {
                    db.collection("books").document(isbn).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot res = task.getResult();
                                    String borrowerStatus = res.getString("borrowerStatus");
                                    if (borrowerStatus != null && borrowerStatus.equals("unavailable")) {
                                        HashMap<String, Object> data = new HashMap<>();
                                        data.put("ownerStatus", "unavailable");
                                        data.put("status", "borrowed");
                                        data.put("borrower", res.getString("potentialBorrower"));
                                        db.collection("books").document(isbn).update(data);
                                        db.collection("users")
                                                .document(owner)
                                                .collection("available")
                                                .document(isbn).delete();
                                        changeBookStatus(isbn, isbn, "lent", owner, "accepted");
                                        changeBookStatus(isbn, isbn, "borrowed", res.getString("potentialBorrower"), "accepted");
                                        queryOutput.setOutput("Book successfully lent");
                                        outputCallback.displayQueryResult("Successful");
                                    } else {
                                        HashMap<String, Object> data = new HashMap<>();
                                        data.put("ownerStatus", "unavailable");
                                        db.collection("books").document(isbn).update(data);
                                        queryOutput.setOutput("Pending to be accepted by the borrower");
                                        outputCallback.displayQueryResult("Successful");
                                    }
                                }
                            });
                } else {
                    queryOutput.setOutput("Book could not be lent");
                    outputCallback.displayQueryResult("Unsuccessful");
                }
            }
        });
    }

    public void returnBook(String isbn, String borrower, QueryOutputCallback outputCallback, QueryOutput queryOutput) {
        DocumentReference userRef = db.collection("users").document(borrower);
        userRef.collection("borrowed")
                .document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = task.getResult();
                if (res.exists()) {
                    db.collection("books").document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot res = task.getResult();
                            if (res.getString("ownerStatus").equals("available") && res.getString("status").equals("borrowed")) {
                                HashMap<String, Object> data = new HashMap<>();
                                data.put("borrower", "none");
                                data.put("status", "available");
                                db.collection("books").document(isbn).update(data);
                                userRef.collection("borrowed").document(isbn).delete();
                                HashMap<String, String> owner = (HashMap<String, String>) res.get("owner");
                                db.collection("users").document(getEmail(owner)).collection("lent").document(isbn).delete();
                                queryOutput.setOutput("Book successfully returned");
                                outputCallback.displayQueryResult("Successful");
                            } else {
                                queryOutput.setOutput("Pending to be accepted by the owner");
                                outputCallback.displayQueryResult("Successful");
                            }
                            HashMap<String,Object> newData = new HashMap<>();
                            newData.put("borrowerStatus", "available");
                            db.collection("books").document(isbn).update(newData);
                        }
                    });
                } else {
                    queryOutput.setOutput("Book could not be returned");
                    outputCallback.displayQueryResult("Unsuccessful");
                }
            }
        });
    }

    public void acceptReturn(String isbn, String owner, QueryOutputCallback outputCallback, QueryOutput queryOutput) {
        DocumentReference userRef = db.collection("users").document(owner);
       userRef.collection("lent")
                .document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = task.getResult();
                if (res.exists()) {
                    db.collection("books").document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot res = task.getResult();
                            if (res.getString("borrowerStatus").equals("available") && res.get("status").equals("borrowed")) {
                                HashMap<String, Object> data = new HashMap<>();
                                userRef.collection("borrowed").document(isbn).delete();
                                db.collection("users").document(res.getString("borrower"))
                                        .collection("borrowed").document(isbn).delete();
                                data.put("status", "available");
                                data.put("borrower", "none");
                                db.collection("books").document(isbn).update(data);
                                queryOutput.setOutput("Book successfully accepted");
                                outputCallback.displayQueryResult("Successful");
                            } else {
                                queryOutput.setOutput("Pending to be returned by the borrower");
                                outputCallback.displayQueryResult("Successful");
                            }
                            HashMap<String, Object> newData = new HashMap<>();
                            newData.put("ownerStatus", "available");
                            db.collection("books").document(isbn).update(newData);

                        }
                    });
                } else {
                    queryOutput.setOutput("Book could not be accepted");
                    outputCallback.displayQueryResult("Unsuccessful");
                }
            }
        });
    }
}
