package org.blackequity.infrastructure.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.blackequity.application.service.ShoppingListService;
import org.blackequity.infrastructure.repository.UpdateQuantityRequest;
import org.blackequity.shared.dto.CreateShoppingItemRequest;

@Path("/api/shopping-list")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShoppingListController {

    @Inject
    ShoppingListService service;

    @GET
    public Response getCurrentShoppingList() {
        return Response.ok(service.getCurrentList()).build();
    }

    @POST
    public Response createItem(CreateShoppingItemRequest request) {
        var item = service.createItem(request);
        return Response.status(Response.Status.CREATED).entity(item).build();
    }

    @PUT
    @Path("/{itemId}/quantity")
    public Response updateQuantity(@PathParam("itemId") String itemId,
                                   UpdateQuantityRequest request) {
        System.out.println("sadsasdas");
        service.updateQuantity(itemId, request.getQuantity());
        return Response.ok().build();
    }

    @PUT
    @Path("/{itemId}/purchase")
    public Response markAsPurchased(@PathParam("itemId") String itemId) {
        service.purchaseItem(itemId);
        return Response.ok().build();
    }

    @POST
    @Path("/generate")
    public Response generateAutomaticList() {
        var items = service.generateAutomaticShoppingList();
        return Response.ok(items).build();
    }

    @DELETE
    @Path("/{itemId}")
    public Response deleteItem(@PathParam("itemId") String itemId) {
        service.deleteItem(itemId);
        return Response.noContent().build();
    }
}
