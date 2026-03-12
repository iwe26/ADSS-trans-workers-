package hrmanagement.presentation.controller;
import Transportation.BusinessLayer.Resources.Response;
import Transportation.ServiceLayer.SiteService;
import hrmanagement.dal.dto.SiteDTO;
import Transportation.BusinessLayer.Resources.TransportationZone;

import java.sql.SQLException;
import java.util.HashSet;


/**
 * Controller for admin-related operations.
 * Handles orchestration between view and SiteService.
 */
public class AdminController {
    private final SiteService siteService;

    public AdminController(SiteService siteService) {
        this.siteService = siteService;
    }

    public HashSet<String> getAllSiteAddresses() {
        return siteService.getAllSiteAddresses();
    }

    public Response addSite(String name, String address, String contactName, String phone, TransportationZone zone) {
        // Basic validation could go here
        return siteService.addSite(name, address, contactName, phone, zone);
    }

    public Response removeSite(String address) {
        return siteService.removeSite(address);
    }
}
