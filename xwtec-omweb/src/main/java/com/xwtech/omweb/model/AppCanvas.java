package com.xwtech.omweb.model;

import java.util.List;

/**
 * Created by zhangq on 2017/2/22.
 */
public class AppCanvas {

    private List<AppCanvasLinks> connections;


    private List<AppCanvasPosition> locations;

    public List<AppCanvasLinks> getConnections() {
        return connections;
    }

    public void setConnections(List<AppCanvasLinks> connections) {
        this.connections = connections;
    }

    public List<AppCanvasPosition> getLocations() {
        return locations;
    }

    public void setLocations(List<AppCanvasPosition> locations) {
        this.locations = locations;
    }
}
