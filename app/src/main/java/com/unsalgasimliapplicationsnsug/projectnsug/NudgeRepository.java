package com.unsalgasimliapplicationsnsug.projectnsug;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NudgeRepository {
    private final FirebaseFirestore db   = FirebaseFirestore.getInstance();
    private final String            myUid = FirebaseAuth.getInstance()
            .getCurrentUser()
            .getUid();

    public interface Callback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface ListCallback {
        void onResult(List<Nudge> items);
        void onError(Exception e);
    }

    private CollectionReference templatesRef() {
        return db.collection("nudgeTemplates");
    }

    private CollectionReference nudgesRef() {
        return db.collection("nudges");
    }

    /** 1) Create your own template */
    public void createTemplate(String title, String body, Callback cb) {
        long now = System.currentTimeMillis();
        Nudge tpl = new Nudge();
        tpl.setTitle(title);
        tpl.setBody(body);
        tpl.setFromUserId(myUid);
        tpl.setToUserId(null);
        tpl.setTimestamp(now);

        templatesRef()
                .add(tpl)
                .addOnSuccessListener(d -> cb.onSuccess())
                .addOnFailureListener(cb::onFailure);
    }

    /** 2) List *all* your templates */
    public void getAllTemplates(ListCallback cb) {
        templatesRef()
                .whereEqualTo("fromUserId", myUid)
                .get()
                .addOnSuccessListener(q -> {
                    List<Nudge> list = new ArrayList<>();
                    for (DocumentSnapshot doc: q.getDocuments()) {
                        Nudge n = doc.toObject(Nudge.class);
                        n.setId(doc.getId());
                        list.add(n);
                    }
                    cb.onResult(list);
                })
                .addOnFailureListener(cb::onError);
    }

    /** 3) Send a template to someone */
    public void send(Nudge template, String toUserId, Callback cb) {
        long now = System.currentTimeMillis();
        Nudge msg = new Nudge();
        msg.setTitle(template.getTitle());
        msg.setBody(template.getBody());
        msg.setFromUserId(myUid);
        msg.setToUserId(toUserId);
        msg.setTimestamp(now);

        nudgesRef()
                .add(msg)
                .addOnSuccessListener(d -> cb.onSuccess())
                .addOnFailureListener(cb::onFailure);
    }

    /** 4) Fetch *all* sent & received nudges, merged & sorted newest-first */
    public void getHistory(ListCallback cb) {
        List<Nudge> all = new ArrayList<>();
        Query sentQ = nudgesRef().whereEqualTo("fromUserId", myUid);
        Query recvQ = nudgesRef().whereEqualTo("toUserId", myUid);

        sentQ.get().addOnSuccessListener(sentSnap -> {
            for (DocumentSnapshot doc: sentSnap.getDocuments()) {
                Nudge n = doc.toObject(Nudge.class);
                n.setId(doc.getId());
                all.add(n);
            }
            recvQ.get().addOnSuccessListener(recvSnap -> {
                for (DocumentSnapshot doc: recvSnap.getDocuments()) {
                    Nudge n = doc.toObject(Nudge.class);
                    n.setId(doc.getId());
                    all.add(n);
                }
                // sort descending by timestamp
                Collections.sort(all, (a,b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                cb.onResult(all);
            }).addOnFailureListener(cb::onError);
        }).addOnFailureListener(cb::onError);
    }
}
