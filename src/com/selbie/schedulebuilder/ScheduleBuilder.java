package com.selbie.schedulebuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;




public class ScheduleBuilder
{
    

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        ArrayList<Show> shows = null;
        ArrayList<ShowDescription> descriptions = null;
        
        Importer importer = new Importer();
        try
        {
            System.out.println("Importing schedule page from web");
            shows = importer.buildSchedulePage();
            System.out.println("Importing show descriptions from web");
            descriptions = importer.buildShowDescriptions();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(2);
            return;
        }
        
        System.out.println("Resolving...");
        Resolver resolver = new Resolver();
        resolver.resolve(shows, descriptions);
        shows = resolver.filterForToday(shows);
        
        System.out.println("Exporting...");
        Exporter exporter = new Exporter();
        String finalschedule = exporter.DumpToJson(shows);
        try
        {
            exporter.SaveToFile(finalschedule, (args.length > 0 ? args[0] : null));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(2);
            return;
        }
        
        System.out.println("Success!");
        
        return;
        
    }

}
