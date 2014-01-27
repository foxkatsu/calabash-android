/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sh.calaba.instrumentationbackend.actions.sms;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import sh.calaba.instrumentationbackend.Result;
import sh.calaba.instrumentationbackend.actions.Action;
import sh.calaba.org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author jorge.pelaez
 */
public class SMSAction implements Action {

    public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";

    private ContentResolver contentResolver;

    public SMSAction(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public Result execute(String... args) {

        ObjectMapper mapper = new ObjectMapper();
        List<SMSData> data = getSMSList();
        if (data != null) {
            try {
                String resultData = mapper.writeValueAsString(data);
                Result result = Result.successResult();
                result.addBonusInformation(resultData);
                return result;
            } catch (IOException ex) {
                return Result.failedResult(ex.getMessage());
            }
        }
        return Result.failedResult();
    }

    private List<SMSData> getSMSList() {
        ContentResolver contentResolver = getContentResolver();
        List<SMSData> result = new ArrayList<SMSData>();
        
        if (contentResolver != null) {
            Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

            if (!cursor.moveToFirst()) {
                return null;
            }

            do {
                SMSData aux = new SMSData();
                aux.setSender(cursor.getString(cursor.getColumnIndex(BODY)));
                aux.setText(cursor.getString(cursor.getColumnIndex(ADDRESS)));
                aux.setTime(cursor.getString(cursor.getColumnIndex(DATE)));
                result.add(aux);
            } while (cursor.moveToNext());
        }
        return result;
    }

    public String key() {
        return "read_sms";
    }

    private ContentResolver getContentResolver() {
        return contentResolver;
    }

}
