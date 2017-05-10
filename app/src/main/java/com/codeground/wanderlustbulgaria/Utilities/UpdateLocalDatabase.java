package com.codeground.wanderlustbulgaria.Utilities;

import android.os.AsyncTask;

import com.codeground.wanderlustbulgaria.Interfaces.IOnLocalDatabaseUpdated;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.LocalParseLocation;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

public class UpdateLocalDatabase extends AsyncTask<List<ParseLocation>, Void, Boolean> {

    private static ArrayList<IOnLocalDatabaseUpdated> mEventListeners = new ArrayList<>();

    public UpdateLocalDatabase() {
    }

    protected Boolean doInBackground(List<ParseLocation>... locations) {
        List<ParseLocation> locs = locations[0];

        if(locs != null){
            List<LocalParseLocation> localLocations = LocalParseLocation.listAll(LocalParseLocation.class);

            //Check for removed items
            if(localLocations!=null){
                for (LocalParseLocation localObj : localLocations) {
                    if(localObj==null){
                        continue;
                    }

                    if(!containsId(locs, localObj.getObjectId())){
                        // outdated item... remove

                        localObj.delete();
                    }
                }
            }

            //Check for outdated items
            for (ParseLocation object : locs) {
                Select query = Select.from(LocalParseLocation.class)
                        .where(Condition.prop("m_object_id").eq(object.getObjectId()));

                LocalParseLocation localLocation = (LocalParseLocation) query.first();

                if(localLocation!=null){
                    if(localLocation.getUpdatedAt().before(object.getUpdatedAt())){
                        //update
                        localLocation.updateLocation(object);
                        localLocation.save();
                    }
                }else{
                    //item does not exist
                    LocalParseLocation newLocation = new LocalParseLocation(object);
                    newLocation.save();
                }
            }

            return true;
        }

        return false;
    }

    protected void onPostExecute(Boolean result) {
        if(result == true && mEventListeners != null){
            for (IOnLocalDatabaseUpdated mEventListener : mEventListeners) {
                if(mEventListener!=null){
                    mEventListener.onLocalDatabaseUpdateCompleted();
                }
            }
        }
    }

    public static void registerForNotification(IOnLocalDatabaseUpdated listener){
        if(listener!=null)
        {
            mEventListeners.add(listener);
        }
    }

    public static void unregisterForNotification(IOnLocalDatabaseUpdated listener){
        if(listener!=null)
        {
            mEventListeners.remove(listener);
        }
    }

    private static boolean containsId(List<ParseLocation> list, String id) {
        for (ParseLocation object : list) {
            if (object.getObjectId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}