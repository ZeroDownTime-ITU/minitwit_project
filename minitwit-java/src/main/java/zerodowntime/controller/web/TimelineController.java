package zerodowntime.controller.web;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import zerodowntime.constants.AppConstants;
import zerodowntime.controller.simulator.SimulatorController;
import zerodowntime.dto.web.MessageView;
import zerodowntime.service.TimelineService;

public class TimelineController extends BaseController {
    TimelineService timelineService;
    private static final Logger log = LoggerFactory.getLogger(SimulatorController.class);

    public TimelineController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    // Shows a users timeline.This shows the user's messages as well as all the
    // messages of followed users.
    public void getUserTimeline(Context ctx) {
        log.info("We got a visitor from: " + ctx.ip());
        Integer userId = getAuthenticatedUserId(ctx);

        int pageOffset = getOffset(getPage(ctx));
        List<MessageView> messages = timelineService.getTimelineForUser(userId, AppConstants.PER_PAGE, pageOffset);
        int messagesCount = timelineService.countUserTimelineMessages(userId);

        ctx.status(200).json(Map.of(
                "messages", messages,
                "total", messagesCount));
    }

    // Displays the latest messages of all users.
    public void getPublicTimeline(Context ctx) {
        int pageOffset = getOffset(getPage(ctx));
        List<MessageView> messages = timelineService.getPublicTimeline(AppConstants.PER_PAGE, pageOffset);
        int messagesCount = timelineService.countPublicTimelineMessages();

        ctx.status(200).json(Map.of(
                "messages", messages,
                "total", messagesCount));
    }
}
