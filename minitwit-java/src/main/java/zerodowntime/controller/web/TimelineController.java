package zerodowntime.controller.web;

import java.util.List;

import io.javalin.http.Context;
import zerodowntime.constants.AppConstants;
import zerodowntime.dto.web.MessageView;
import zerodowntime.service.TimelineService;

public class TimelineController extends BaseController {
    TimelineService timelineService;

    public TimelineController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    // Shows a users timeline.This shows the user's messages as well as all the
    // messages of followed users.
    public void getUserTimeline(Context ctx) {
        System.out.println("We got a visitor from: " + ctx.ip());

        Integer userId = getAuthenticatedUserId(ctx);

        List<MessageView> messages = timelineService.getTimelineForUser(userId, AppConstants.PER_PAGE);

        ctx.status(200).json(messages);
    }

    // Displays the latest messages of all users.
    public void getPublicTimeline(Context ctx) {
        List<MessageView> messages = timelineService.getPublicTimeline(AppConstants.PER_PAGE);

        ctx.status(200).json(messages);
    }
}
