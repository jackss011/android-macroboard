package com.jackss.ag.macroboard.network;

import com.jackss.ag.macroboard.utils.StaticLibrary;

/**
 *
 */
public class Packager
{
    private static final String DIV = ";";
    private static final String SPEC = ":";

    private static final String HS_HEADER = "MB_HANDSHAKE";
    private static final String HS_NAME = "N";

    private static final String BE_REQUEST = "MB_REQUEST";
    private static final String BE_RESPONSE = "MB_RESPONSE";

    private static final String SL_ACTION   = "A";
    private static final String MD_COPY     = "c";
    private static final String MD_CUT      = "x";
    private static final String MD_PASTE    = "v";

    private static final String SL_MOUSE    = "M";
    private static final String MD_CLICK_1  = "1";
    private static final String MD_CLICK_2  = "2";


// |==============================
// |==>  HANDSHAKE
// |===============================

    public static String packHandShake()
    {
        return HS_HEADER + DIV + HS_NAME + SPEC + StaticLibrary.getDeviceName();
    }

    public static boolean unpackHandShake(String handshake)
    {
        return handshake.equals(HS_HEADER);
    }


// |==============================
// |==>  BEACON
// |===============================

    public static String packBroadcastMessage()
    {
        return BE_REQUEST;
    }

    public static boolean validateBeaconResponse(String response)
    {
        return response.equals(BE_RESPONSE);
    }


// |==============================
// |==>  ACTIONS
// |===============================

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

    private static String div(String... k)
    {
        String f = "";

        for(String item : k)
            f += item + DIV;

        return f;
    }
}
