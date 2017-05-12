package com.jackss.ag.macroboard.network;

/**
 *
 */
public class Packager
{
    private static final String DIV = " ";

    private static final String HD_REQUEST  = "MB_REQUEST";
    private static final String HD_RESPONSE = "MB_RESPONSE";


    private static final String SL_ACTION   = "A";
    private static final String MD_COPY     = "c";
    private static final String MD_CUT      = "x";
    private static final String MD_PASTE    = "v";

    private static final String SL_MOUSE    = "M";
    private static final String MD_CLICK_1  = "1";
    private static final String MD_CLICK_2  = "2";


    public static String packBroadcastMessage()
    {
        return HD_REQUEST;
    }

    public static boolean validateBeaconResponse(String response)
    {
        return response.equals(HD_RESPONSE);
    }

    public static String packActionCopy()
    {
        return SL_ACTION + DIV + MD_COPY;
    }

    public static String packActionCut()
    {
        return SL_ACTION + DIV + MD_CUT;
    }

    public static String packActionPaste()
    {
        return SL_ACTION + DIV + MD_PASTE;
    }
}
