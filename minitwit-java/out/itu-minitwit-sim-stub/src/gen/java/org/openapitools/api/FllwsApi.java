package org.openapitools.api;

import org.openapitools.model.ErrorResponse;
import org.openapitools.model.FollowAction;
import org.openapitools.model.FollowsResponse;

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
@Path("/fllws/{username}")
@Api(description = "the fllws API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-02-13T14:37:04.162573194Z[Etc/UTC]", comments = "Generator version: 7.19.0")
public class FllwsApi {

    @GET
    @Produces({ "application/json" })
    @ApiOperation(value = "", notes = "Get list of users followed by the given user.  - Query param `?no=` limits result count. - Optionally updates a 'latest' global value via `?latest=` query param.", response = FollowsResponse.class, tags={ "Minitwit" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = FollowsResponse.class),
        @ApiResponse(code = 403, message = "Unauthorized - Must include correct Authorization header", response = ErrorResponse.class),
        @ApiResponse(code = 404, message = "User not found (no response body)", response = Void.class)
    })
    public Response getFollow(@PathParam("username") String username,@HeaderParam("Authorization") @NotNull   @ApiParam("Authorization string of the form &#x60;Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh&#x60;. Used to authenticate as simulator") String authorization,@QueryParam("latest")  @ApiParam("Optional: &#x60;latest&#x60; value to update")  Integer latest,@QueryParam("no") @DefaultValue("100")  @ApiParam("Optional: &#x60;no&#x60; limits result count")  Integer no) {
        return Response.ok().entity("magic!").build();
    }

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "", notes = "Follow or unfollow a user on behalf of `username`.  - Body must contain either `follow: <user>` or `unfollow: <user>`", response = Void.class, tags={ "Minitwit" })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "No Content", response = Void.class),
        @ApiResponse(code = 403, message = "Unauthorized - Must include correct Authorization header", response = ErrorResponse.class),
        @ApiResponse(code = 404, message = "User not found (no response body)", response = Void.class)
    })
    public Response postFollow(@PathParam("username") String username,@HeaderParam("Authorization") @NotNull   @ApiParam("Authorization string of the form &#x60;Basic c2ltdWxhdG9yOnN1cGVyX3NhZmUh&#x60;. Used to authenticate as simulator") String authorization,@Valid @NotNull FollowAction payload,@QueryParam("latest")  @ApiParam("Optional: &#x60;latest&#x60; value to update")  Integer latest) {
        return Response.ok().entity("magic!").build();
    }
}
