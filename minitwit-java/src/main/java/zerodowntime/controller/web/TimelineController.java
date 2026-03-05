package zerodowntime.controller.web;

import java.util.List;
import java.util.Map;

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

        int pageOffset = getOffset(getPage(ctx));
        List<MessageView> messages = timelineService.getTimelineForUser(userId, AppConstants.PER_PAGE, pageOffset);
        int totalCount = timelineService.getUserTimelineCount(userId);

        ctx.status(200).json(Map.of(
                "messages", messages,
                "total", totalCount));
    }

    // Displays the latest messages of all users.
    public void getPublicTimeline(Context ctx) {
        int pageOffset = getOffset(getPage(ctx));
        List<MessageView> messages = timelineService.getPublicTimeline(AppConstants.PER_PAGE, pageOffset);
        int totalCount = timelineService.getPublicTimelineCount();

        ctx.status(200).json(Map.of(
                "messages", messages,
                "total", totalCount));
    }

    private int getPage(Context ctx) {
        return Integer.parseInt(ctx.queryParam("page") != null ? ctx.queryParam("page") : "0");
    }

    private int getOffset(int page) {
        return page * AppConstants.PER_PAGE;
    }
}
