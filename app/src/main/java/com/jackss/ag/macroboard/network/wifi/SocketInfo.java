package com.jackss.ag.macroboard.network;

import android.support.annotation.NonNull;
import java.net.InetAddress;


/**
 *
 */
public class SocketInfo
{
    public String address;
    public String hostName;

    public SocketInfo(String address, String hostName)
    {
        this.address = address;
        this.hostName = hostName;
    }

    /** Shouldn't be called on main thread. The name fetch is a network operation */
    public SocketInfo(@NonNull InetAddress address)
    {
        this.address = address.getHostAddress();
        this.hostName = address.getHostName(); // Net operation
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SocketInfo that = (SocketInfo) o;

        return address != null ? address.equals(that.address) : that.address == null;
    }

    @Override
    public int hashCode()
    {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (hostName != null ? hostName.hashCode() : 0);
        return result;
    }
}
