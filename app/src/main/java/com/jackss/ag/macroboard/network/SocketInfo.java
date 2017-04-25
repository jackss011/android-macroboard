package com.jackss.ag.macroboard.network;

import java.net.InetAddress;

/**
 *
 */
public class SocketInfo
{
    public InetAddress address;
    public String hostName;

    public SocketInfo(InetAddress address, String hostName)
    {
        this.address = address;
        this.hostName = hostName;
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
