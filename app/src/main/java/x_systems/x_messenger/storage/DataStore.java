package x_systems.x_messenger.storage;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremiah8100 on 1-12-2016.
 */

public class DataStore {

    private List<Object[]> Items = new ArrayList<Object[]>();
    private static List<Object[]> DataStores = new ArrayList<Object[]>();
    public static void Init(Context Main){
        boolean found = false;
        for(Object[] ob : DataStore.DataStores) {
            if((Context)ob[0] == Main)
                found = true;
        }
        if(!found) {
            DataStores.add(new Object[]{Main, new DataStore()});
            System.out.println("Database Added");
        }

    }

    //Delete an Item from the DataStore
    public static void Delete(Context Main){
        for(Object[] ob : DataStore.DataStores) {
            if((Context)ob[0] == Main)
            DataStores.remove(ob);
        }
    }

    //Add an Item to the DataStore
    private static void AddItem(String Name, Object Value, Context main){
        Object[] toadd = new Object[]{Name, Value};
        DataStore dt = DataStore.getDataStore(main);
        if(!dt.Items.contains(toadd)) {
            dt.Items.add(toadd);
            System.out.println("Item Added");
        }

    }

    //Get an Item from the DataStore
    private static DataStore getDataStore(Context main){
        for(Object[] ob : DataStores) {
            if((Context)ob[0] == main){
                System.out.println("DataStore Found");
                return (DataStore)ob[1];
            }
        }
        return null;
    }

    
    private static void ChangeItem(String Name, Object Value, Context main){
        DataStore dt = DataStore.getDataStore(main);
        for(int a = 0; a < dt.Items.size();a++){
            if(dt.Items.get(a)[0] == Name){
                dt.Items.get(a)[1] = Value;
                System.out.println("Item Changed");
            }
        }
    }

    public static void DeleteItem(String Name, Context main){
        DataStore dt = DataStore.getDataStore(main);
        for(Object[] l : dt.Items)
        {
            if(l[0] == Name)
                dt.Items.remove(l);
        }
    }

    public static void SetItem(String Name, Object Value, Context main){
        if(GetItem(Name, main) == null){
            DataStore.AddItem(Name, Value, main);
        } else {
            DataStore.ChangeItem(Name, Value, main);
        }
    }

    public static Object GetItem(String Name, Context main){
        DataStore dt = DataStore.getDataStore(main);
        Object output = null;
        for(Object[] l : dt.Items){
            if(l[0] == Name) {
                output = l[1];
                System.out.println("found item");
            }
        }
        return output;
    }

    public static class GlobalStore{
        private static List<Object[]> Items = new ArrayList<Object[]>();

        private static void AddItem(String Name, Object Value){
            Object[] toadd = new Object[]{Name, Value};
            if(!GlobalStore.Items.contains(toadd)) {
                GlobalStore.Items.add(toadd);
                System.out.println("Item Added");
            }

        }


        private static void ChangeItem(String Name, Object Value){
            for(int a = 0; a < GlobalStore.Items.size();a++){
                if(GlobalStore.Items.get(a)[0] == Name){
                    GlobalStore.Items.get(a)[1] = Value;
                    System.out.println("Item Changed");
                }
            }
        }

        public static void DeleteItem(String Name){
            for(Object[] l : GlobalStore.Items)
            {
                if(l[0] == Name)
                    GlobalStore.Items.remove(l);
            }
        }

        public static void SetItem(String Name, Object Value){
            if(GetItem(Name) == null){
                GlobalStore.AddItem(Name, Value);
            } else {
                GlobalStore.ChangeItem(Name, Value);
            }
        }

        public static Object GetItem(String Name){
            Object output = null;
            for(Object[] l : GlobalStore.Items){
                if(l[0] == Name) {
                    output = l[1];
                    System.out.println("found item");
                }
            }
            return output;
        }
    }
}
