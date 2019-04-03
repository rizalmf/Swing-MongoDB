/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Anggota;

/**
 *
 * @author PKane_NS
 */
public class AnggotaRepo {
    private MongoClient client;
    private DB db;
    private DBCollection collection;
    private Map createMap(Anggota a){
        Map<String, Object> map = new HashMap<>();
        map.put("nama", a.getNama());
        map.put("nim", a.getNim());
        map.put("no_telp", String.valueOf(a.getNo_telp()));
        map.put("alamat", a.getAlamat());
        map.put("moto", a.getMoto());
        return map;
    }
    private void check(){
        if (client == null) {
            client = new MongoClient("localhost:27017");
            db = (DB) client.getDB("universitas"); // database
        }
    }
    public void save(Anggota a) throws Exception{
        check();
        // ini table gan..
        collection =  db.getCollection("anggota"); 
        
        // ini row/ data yg bakal d insert
        BasicDBObject doc = new BasicDBObject(); 
        Map<String, Object> map = createMap(a);
        doc.putAll(map);
        
        //execute
        collection.insert(doc);
    }
    public void update(Anggota a) throws Exception{
        check();
        collection =  db.getCollection("anggota");
        // asumsi nim tiap orang berbeda "where clause"
        BasicDBObject query = new BasicDBObject();
        query.put("nim", a.getNim());
        
        //tentukan apa yg ingin di update
        BasicDBObject newDoc = new BasicDBObject();
        Map<String, Object> map = createMap(a);
        newDoc.putAll(map);
        
        //query update ke mongo
        BasicDBObject updateDoc = new BasicDBObject();
        updateDoc.put("$set", newDoc);
        
        //execute
        collection.update(query, updateDoc);
    }
    public void delete(String nim) throws Exception{
        check();
        collection =  db.getCollection("anggota");
        // "where clause"
        BasicDBObject query = new BasicDBObject();
        query.put("nim", nim);
        
        //execute
        collection.remove(query);
    }
    public List<Anggota> getAnggotas() throws Exception{
        check();
        collection =  db.getCollection("anggota");
        //ambil smua document yg ada di collection pkai DBCursor
        DBCursor cursor = collection.find();
        
        //save dari cursor ke String
        String json ="[";
        while (cursor.hasNext()) {
            json += ","+cursor.next();
        }
        json += "]";
        json = json.replaceFirst(",", "");
        
        // pakai bantuan Gson :DDD
        Gson gson = new Gson();
        List<Anggota> anggotas = gson.fromJson(json, new TypeToken<List<Anggota>>(){}.getType());
        return anggotas;
    }
    
}
