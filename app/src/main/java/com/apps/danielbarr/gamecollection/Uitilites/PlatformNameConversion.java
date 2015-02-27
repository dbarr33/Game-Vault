package com.apps.danielbarr.gamecollection.Uitilites;

import android.content.Context;

import com.apps.danielbarr.gamecollection.R;

/**
 * @author Daniel Barr (Fuzz)
 */
public class PlatformNameConversion {
    private static final String playstation = "playstation";
    private static final String playstation_2 = "playstation 2";
    private static final String playstation_3 = "playstation 3";
    private static final String playstation_4 = "playstation 4";
    private static final String xbox = "xbox";
    private static final String xbox_360 = "xbox 360";
    private static final String xbox_one = "xbox one";
    private static final String nintendo = "nintendo";
    private static final String nintendo_super = "super nintendo";
    private static final String nintendo_64 = "nintendo 64";
    private static final String nintendo_gamecube = "Gamecube";
    private static final String nintendo_wii = "Wii";
    private static final String nintendo_wii_u = "Wii U";
    private static final String pc = "PC";


    public static  String convertToFormalName(String platform, Context context)
    {
        if(platform == context.getString(R.string.ps1_drawer_title))
        {
            return playstation;
        }
        else  if(platform == context.getString(R.string.ps2_drawer_title))
        {
            return playstation_2;
        }
        else  if(platform == context.getString(R.string.ps3_drawer_title))
        {
            return playstation_3;
        }else  if(platform == context.getString(R.string.ps4_drawer_title))
        {
            return playstation_4;
        }
        else  if(platform == context.getString(R.string.xbox_drawer_title))
        {
            return xbox;
        }
        else  if(platform == context.getString(R.string.xbox360_drawer_title))
        {
            return xbox_360;
        }
        else  if(platform == context.getString(R.string.xboxone_drawer_title))
        {
            return xbox_one;
        }
        else  if(platform == context.getString(R.string.nintendo_drawer_title))
        {
            return nintendo;
        }
        else  if(platform == context.getString(R.string.supernintendo_drawer_title))
        {
            return nintendo_super;
        }
        else  if(platform == context.getString(R.string.nintendo64_drawer_title))
        {
            return nintendo_64;
        } else  if(platform == context.getString(R.string.gamecube_drawer_title))
        {
            return nintendo_gamecube;
        } else  if(platform == context.getString(R.string.wii_drawer_title))
        {
            return nintendo_wii;
        } else  if(platform == context.getString(R.string.wiiu_drawer_title))
        {
            return nintendo_wii_u;
        }
        else  if(platform == context.getString(R.string.pc_drawer_title))
        {
            return pc;
        }

        return "";
    }
}
