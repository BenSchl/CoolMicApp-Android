package cc.echonet.coolmicapp.Icecast.Request;

import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import cc.echonet.coolmicapp.Icecast.State;

public class Stats extends Request {

    public Stats(URL url) throws IOException {
        super(url);
        Log.d("CM-StreamStatsService", "url=" + url);
    }

    private String exceptionToString(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    @Override
    public void finish() {
        try {
            connect();

            if (connection.getResponseCode() != 200) {
                Log.e("CM-StreamStatsService", "HTTP error, invalid server status code: " + connection.getResponseMessage());
                setError();
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(connection.getInputStream());

            Log.d("CM-StreamStatsService", "Parsed Document " + doc.toString());

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            Log.d("CM-StreamStatsService", "post xpath");

            XPathExpression expr_listeners = xpath.compile("/icestats/source/listeners/text()");
            XPathExpression expr_listeners_peak = xpath.compile("/icestats/source/listener_peak/text()");

            Log.d("CM-StreamStatsService", "post xpath compile");

            String listeners = (String) expr_listeners.evaluate(doc, XPathConstants.STRING);
            String listeners_peak = (String) expr_listeners_peak.evaluate(doc, XPathConstants.STRING);

            Log.d("CM-StreamStatsService", "post xpath eval " + listeners + " " + listeners_peak);

            int listenersCurrent = -1;
            int listenersPeak = -1;

            if (!listeners.isEmpty()) {
                listenersCurrent = Integer.valueOf(listeners);
            } else {
                Log.d("CM-StreamStatsService", "found no listeners");
            }

            if (!listeners_peak.isEmpty()) {
                listenersPeak = Integer.valueOf(listeners_peak);
            } else {
                Log.d("CM-StreamStatsService", "found no listeners peak");
            }

            response = new cc.echonet.coolmicapp.Icecast.Response.Stats(listenersCurrent, listenersPeak);
            state = State.FINISHED;
        } catch (XPathExpressionException e) {
            Log.e("CM-StreamStatsService", "XPException while fetching Stats: " + exceptionToString(e));
            setError();
        } catch (SAXException e) {
            Log.e("CM-StreamStatsService", "SAXException while fetching Stats: " + exceptionToString(e));
            setError();
        } catch (ParserConfigurationException e) {
            Log.e("CM-StreamStatsService", "PCException while fetching Stats: " + exceptionToString(e));
            setError();
        } catch (IOException e) {
            Log.e("CM-StreamStatsService", "IOException while fetching Stats: " + exceptionToString(e));
            setError();
        } catch (Exception e) {
            Log.e("CM-StreamStatsService", "Exception while fetching Stats: " + exceptionToString(e));
            setError();
        }
    }

    @Override
    public cc.echonet.coolmicapp.Icecast.Response.Stats getResponse() {
        return (cc.echonet.coolmicapp.Icecast.Response.Stats) super.getResponse();
    }
}