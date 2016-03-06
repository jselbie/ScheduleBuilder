package com.selbie.schedulebuilder;

import java.util.ArrayList;

class Show
{
    public enum ShowType
    {
        Live,
        SoundBlock,
        SpecialtyShow,
        PublicAffairs,
        Sports,
        Unknown,
    }
    
    public String DayText="";  // as parsed right off the web page (e.g. "Sunday 6/14")
    
    int MonthInt;
    int DayInt;

    public String Title="";
    public String Time="";
    public String M3U_Low="";
    public String M3U_High="";
    public String LogoURL="";
    public String Genres="";
    public ShowType TypeOfShow = ShowType.Unknown;
    public boolean LiveStream = false;
    public boolean HasIcyMetaInt = false;  // typically LiveStream==HasIceMetaInt  Indicates that the stream supports Icecast/Shoutcast metadata and that the loopback proxy should be used
    
    public ArrayList<String> urls_Low = new ArrayList<String>();
    public ArrayList<String> urls_High = new ArrayList<String>();
    
    
    
}

