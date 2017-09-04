package com.selbie.schedulebuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class ScheduleBuilder
{

    public static String getArgmument(String argname, String[] args) {

        String result = "";
        for (int i = 1; i < args.length; i++) {
            if (args[i-1].equals(argname)) {
                result = args[i];
                break;
            }
        }
        return result;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        ArrayList<Show> shows = null;
        ArrayList<ShowDescription> descriptions = null;
        HashMap<String, Show> showmap = null;

        String argFilename = getArgmument("--output", args);
        String argMapFile = getArgmument("--mapfile", args);

        if (!argFilename.isEmpty()) {
            System.out.println("Using " + argFilename + " for output");
        }

        if (!argMapFile.isEmpty()) {
            System.out.println("Using " + argFilename + " for map file");
            showmap = ShowMap.load(argMapFile);
        }

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
        resolver.resolve(shows, descriptions, showmap);
        shows = resolver.filterForToday(shows);
        
        System.out.println("Exporting...");
        Exporter exporter = new Exporter();
        String finalschedule = exporter.DumpToJson(shows);
        try
        {
            if (!argFilename.isEmpty())
                exporter.SaveToFile(finalschedule, argFilename);
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
