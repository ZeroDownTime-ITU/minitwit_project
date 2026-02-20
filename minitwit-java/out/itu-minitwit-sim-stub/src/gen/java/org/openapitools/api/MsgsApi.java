package org.openapitools.api;

import org.openapitools.model.ErrorResponse;
import org.openapitools.model.Message;
import org.openapitools.model.PostMessage;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.swagger.annotations.*;

import java.io.InputStream;
import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
* Represents a collection of functions to interact with the API endpoints.
*/
@Path("/msgs")
@Api(description = "the msgs API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-02-13T14:37:04.162573194Z[Etc/UTC]", comments = "Generator version: 7.19.0")
public class MsgsApi {

    @GET
    @Produces({ "application/json" })
    @ApiOperation(value = "", notes = "Get recent messages.  - Filters out flagged messages - Returns a list of recent messages (max defined by `?no=` param) - Optionally updates a 'latest' global value via `?latest=` query param.", response = Message.class, responseContainer = "List", tags={ "Minitwit" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = Message.class, responseContainer = "List"),
        @ApiResponse(code = 403, message = "Unauthorized - Must include correct Authorization header", response = ErrorResponse.class)
    })
    public Response getMessages(@HeaderParam("Authorization") @NotNull   @ApiParam("Authorization string of the form &#x60;Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh&#x60;. Used to authenticate as simulator") String authorization,@QueryParam("latest")  @ApiParam("Optional: &#x60;latest&#x60; value to update")  Integer latest,@QueryParam("no") @DefaultValue("100")  @ApiParam("Optional: &#x60;no&#x60; limits result count")  Integer no) {
        return Response.ok().entity("magic!").build();
    }

    @GET
    @Path("/{username}")
    @Produces({ "application/json" })
    @ApiOperation(value = "", notes = "Get messages for a specific user.  - Returns messages authored by the specified user - Filtered by unflagged - Returns a list of recent messages for the user (max defined by `?no=` param) - Optionally updates a 'latest' global value via `?latest=` query param.", response = Message.class, responseContainer = "List", tags={ "Minitwit" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = Message.class, responseContainer = "List"),
        @ApiResponse(code = 403, message = "Unauthorized - Must include correct Authorization header", response = ErrorResponse.class),
        @ApiResponse(code = 404, message = "User not found (no response body)", response = Void.class)
    })
    public Response getMessagesPerUser(@PathParam("username") String username,@HeaderParam("Authorization") @NotNull   @ApiParam("Authorization string of the form &#x60;Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh&#x60;. Used to authenticate as simulator") String authorization,@QueryParam("latest")  @ApiParam("Optional: &#x60;latest&#x60; value to update")  Integer latest,@QueryParam("no") @DefaultValue("100")  @ApiParam("Optional: &#x60;no&#x60; limits result count")  Integer no) {
        return Response.ok().entity("magic!").build();
    }

    @POST
    @Path("/{username}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "", notes = "Post a new message as a specific user.  - Message must include `content` in the body - Stored with timestamp and `flagged=0` - Returns empty body on success - Optionally updates a 'latest' global value via `?latest=` query param.", response = Void.class, tags={ "Minitwit" })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "No Content", response = Void.class),
        @ApiResponse(code = 403, message = "Unauthorized - Must include correct Authorization header", response = ErrorResponse.class)
    })
    public Response postMessagesPerUser(@PathParam("username") String username,@HeaderParam("Authorization") @NotNull   @ApiParam("Authorization string of the form &#x60;Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh&#x60;. Used to authenticate as simulator") String authorization,@Valid @NotNull PostMessage payload,@QueryParam("latest")  @ApiParam("Optional: &#x60;latest&#x60; value to update")  Integer latest) {
        return Response.ok().entity("magic!").build();
    }
}
