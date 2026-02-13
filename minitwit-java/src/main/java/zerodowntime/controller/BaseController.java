package zerodowntime.controller;

import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {

    /**
     * Helper method to create the standard model for Pebble templates.
     * Includes user info and flash messages from the context.
     */
    protected Map<String, Object> createModel(Context context) {
        Map<String, Object> model = new HashMap<>();

        // Add user to a "g" (global) map to match Flask/Minitwit style
        Map<String, Object> g = new HashMap<>();
        g.put("user", context.attribute("user"));
        model.put("g", g);

        // Add flashes if they exist
        Object flashes = context.attribute("flashes");
        if (flashes != null) {
            model.put("flashes", flashes);
        }

        return model;
    }
}