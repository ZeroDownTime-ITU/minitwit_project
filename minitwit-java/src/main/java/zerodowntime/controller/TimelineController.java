package zerodowntime.controller;

import java.util.List;
import java.util.Map;

import io.javalin.http.Context;
import zerodowntime.App;
import zerodowntime.constants.AppConstants;
import zerodowntime.constants.AppConstants.Web;
import zerodowntime.dto.MessageView;
import zerodowntime.model.User;
import zerodowntime.service.TimelineService;

public class TimelineController extends BaseController {
    TimelineService timelineService;

    public TimelineController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    // Shows a users timeline or if no user is logged in it will
    // redirect to the public timeline. This shows the user's
    // messages as well as all the messages of followed users.
    public void showUserTimeline(Context ctx) {
        System.out.println("We got a visitor from: " + ctx.ip());

        User user = ctx.attribute("user");
        if (user == null) {
            ctx.redirect(Web.PUBLIC);
            return;
        }

        List<MessageView> messages = timelineService.getTimelineForUser(user.getUserId(), AppConstants.PER_PAGE);

        Map<String, Object> model = createModel(ctx);
        model.put("messages", messages);
        ctx.render("timeline.html", model);
    }

    // Displays the latest messages of all users.
    public void showPublicTimeline(Context ctx) {
        List<MessageView> messages = timelineService.getPublicTimeline(AppConstants.PER_PAGE);

        Map<String, Object> model = createModel(ctx);
        model.put("messages", messages);
        model.put("endpoint", "public_timeline");

        ctx.render("timeline.html", model);
    }
}
