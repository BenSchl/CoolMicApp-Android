/*
 *      Copyright (C) Jordan Erickson                     - 2014-2020,
 *      Copyright (C) Löwenfelsen UG (haftungsbeschränkt) - 2015-2020
 *       on behalf of Jordan Erickson.
 *
 * This file is part of Cool Mic.
 *
 * Cool Mic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Mic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Mic.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cc.echonet.coolmicapp.Configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class Server extends ProfileBase {
    Server(ProfileBase profile) {
        super(profile);
    }

    @SuppressWarnings("SameReturnValue")
    public String getProtocol() {
        // TODO: This is static for now but may change in future.
        return "http";
    }

    public String getHostname() {
        String serverName = getString("connection_address");

        if (serverName.indexOf(':') > 0) {
            serverName = serverName.split(":", 2)[0];
        }

        return serverName;
    }

    public int getPort() {
        String serverName = getString("connection_address");

        if (serverName.indexOf(':') > 0) {
            return  Integer.parseInt(serverName.split(":", 2)[1]);
        }

        return 8000;
    }

    public void setAddress(String hostname, int port) {
        if (hostname.indexOf(':') > 0)
            throw new IllegalArgumentException("Bad Hostname");

        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("Bad Port number");

        if (port == 0) {
            editor.putString("connection_address", hostname);
        } else {
            editor.putString("connection_address", String.format(Locale.ENGLISH, "%s:%d", hostname, port));
        }
    }

    public void setAddress(String address) {
        String hostname;
        int port;

        if (address.indexOf(':') > 0) {
            String[] splitted = address.split(":", 2);
            hostname = splitted[0];
            port = Integer.parseInt(splitted[1]);
        } else {
            hostname = address;
            port = 0;
        }

        setAddress(hostname, port);
    }

    public String getUsername() {
        return getString("connection_username");
    }

    public void setUsername(String username) {
        editor.putString("connection_username", username);
    }

    public String getPassword() {
        return getString("connection_password");
    }

    public void setPassword(String password) {
        editor.putString("connection_password", password);
    }

    public boolean getReconnect() {
        return getBoolean("connection_reconnect", false);
    }

    public String getMountpoint() {
        return getString("connection_mountpoint");
    }

    public void setMountpoint(String mountpoint) {
        editor.putString("connection_mountpoint", mountpoint);
    }

    public boolean isSet() {
        return !getHostname().isEmpty() && !getMountpoint().isEmpty() && !getUsername().isEmpty() && !getPassword().isEmpty();
    }

    public URL getStreamURL() throws MalformedURLException {
        return new URL(getProtocol(), getHostname(), getPort(), getMountpoint());
    }
}
