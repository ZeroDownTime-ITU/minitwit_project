package org.openapitools.api;

import org.openapitools.model.ErrorResponse;
import org.openapitools.model.RegisterRequest;

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
@Path("/register")
@Api(description = "the register API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-02-13T14:37:04.162573194Z[Etc/UTC]", comments = "Generator version: 7.19.0")
public class RegisterApi {

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "", notes = "Register a new user. - Optionally updates a 'latest' global value via `?latest=` query param.", response = Void.class, tags={ "Minitwit" })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "No Content", response = Void.class),
        @ApiResponse(code = 400, message = "Bad Request | Possible reasons:  - missing username  - invalid email  - password missing  - username already taken", response = ErrorResponse.class)
    })
    public Response postRegister(@Valid @NotNull RegisterRequest payload,@QueryParam("latest")  @ApiParam("Optional: &#x60;latest&#x60; value to update")  Integer latest) {
        return Response.ok().entity("magic!").build();
    }
}
