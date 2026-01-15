package org.blackequity.infrastructure.controller.inventory;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.blackequity.application.service.inventory.InventoryConsumptionService;
import org.blackequity.domain.dto.inventory.InventoryConsumption;

import java.util.List;

@Path("/inventory-consumptions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryConsumptionController {

    @Inject
    InventoryConsumptionService inventoryConsumptionService;

    @POST
    public Response create(InventoryConsumption inventoryConsumption) {
        InventoryConsumption created = inventoryConsumptionService.create(inventoryConsumption);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    public Response getAll() {
        List<InventoryConsumption> consumptions = inventoryConsumptionService.getAll();
        return Response.ok(consumptions).build();
    }
}
