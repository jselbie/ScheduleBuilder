package com.selbie.schedulebuilder;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Exporter
{
    
    
    public Exporter()
    {
        
    }
    
    public void SaveToFile(String schedule, String filename) throws FileNotFoundException
    {
        if ((filename != null) && !filename.isEmpty())
        {
            System.out.println("Saving to file: " + filename);
            PrintWriter writer = new PrintWriter(filename);
            writer.print(schedule);
            writer.close();
        }         
    }
    
    
    public String DumpToJson(ArrayList<Show> shows)
    {
        JSONObject root = new JSONObject();
        
        JSONArray items = new JSONArray();
        
        
        for (Show s : shows)
        {
            JSONObject showObject = new JSONObject();
            showObject.put("title", s.Title);
            showObject.put("genre", s.Genres);
            
            // final fix up of time string - this is what gets displayed to the user
            String time = ""; 
            if ((s.DayText.length() >= 3) && (s.MonthInt != 0) && (s.DayInt != 0))
            {
                time = s.DayText.substring(0,3) + " " + s.MonthInt + "/" + s.DayInt + " " + s.Time;
            }
            
            showObject.put("time", time);
            showObject.put("logo", s.LogoURL);
            
            JSONArray arr;
            JSONObject stream1 = null;
            JSONObject stream2 = null;
            
            if (!s.M3U_Low.equals(""))
            {
                stream1 = new JSONObject();
                arr = new JSONArray();
                
                stream1.put("bitrate", 24);        // hack - hardcoding 24 and 128 for bitrates
                stream1.put("url_m3u", s.M3U_Low);
                stream1.put("live", s.LiveStream);
                
                if (s.HasIcyMetaInt)
                {
                    stream1.put("metaint", true);
                }
                
                for (String url : s.urls_Low)
                {
                    arr.put(url);
                }
                stream1.put("playlist", arr);
            }
            
            if (!s.M3U_High.equals(""))
            {
                stream2 = new JSONObject();
                arr = new JSONArray();
                stream2.put("bitrate", 128);
                stream2.put("live", s.LiveStream);
                stream2.put("url_m3u", s.M3U_High);
                
                if (s.HasIcyMetaInt)
                {
                    stream2.put("metaint", true);
                }
                
                for (String url : s.urls_High)
                {
                    arr.put(url);
                }
                stream2.put("playlist", arr);
            }
            
            arr = new JSONArray();
            if (stream1 != null)
            {
                arr.put(stream1);
            }
            if (stream2 != null)
            {
                arr.put(stream2);
            }
            
            showObject.put("streams", arr);
            
            items.put(showObject);
        }
        
        root.put("items", items);
        
        
        String strResult = root.toString(4);
        
        System.out.println(strResult);
        
        
        return strResult;
        
    }

    
    

}
