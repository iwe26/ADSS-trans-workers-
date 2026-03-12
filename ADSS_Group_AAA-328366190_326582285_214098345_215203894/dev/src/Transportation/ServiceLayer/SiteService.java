package Transportation.ServiceLayer;

import Transportation.BusinessLayer.Resources.Response;
import Transportation.BusinessLayer.Resources.TransportationZone;
import hrmanagement.dal.dao.SiteDAO;
import hrmanagement.dal.dto.SiteDTO;

import java.util.*;

public class SiteService {
    private final Map<String, SiteDTO> sites = new HashMap<>();  // key: site address
    private final SiteDAO siteDAO = new SiteDAO();

    public SiteService() {
        loadSitesFromDatabase();
    }

    public Response addSite(String name,
                            String address,
                            String contactName,
                            String phone,
                            TransportationZone zone) {
        try {
            if (siteDAO.findByAddress(address) != null) {
                throw new IllegalArgumentException("Site already exists.");
            }
            SiteDTO site = new SiteDTO(name, address, contactName, phone, zone);
            siteDAO.insert(site);
            sites.put(address, site);
            return new Response("Site added successfully.", null);
        } catch (Exception e) {
            return new Response(null, e.getMessage());
        }
    }

    public Response removeSite(String address) {
        try {
            if (siteDAO.findByAddress(address) == null) {
                throw new NoSuchElementException("Site not found.");
            }
            siteDAO.delete(address);
            sites.remove(address);
            return new Response(true, null);
        } catch (Exception e) {
            return new Response(null, e.getMessage());
        }
    }

    public Response getSite(String address) {
        try {
            SiteDTO site = siteDAO.findByAddress(address);
            if (site == null) {
                throw new NoSuchElementException("Site not found.");
            }
            return new Response(site, null);
        } catch (Exception e) {
            return new Response(null, e.getMessage());
        }
    }

    public Response siteExists(String address) {
        try {
            boolean exists = siteDAO.findByAddress(address) != null;
            return new Response(exists, null);
        } catch (Exception e) {
            return new Response(null, e.getMessage());
        }
    }

    public Response setTransportationZone(String address, TransportationZone zone) {
        try {
            SiteDTO site = siteDAO.findByAddress(address);
            if (site == null) {
                throw new NoSuchElementException("Site not found.");
            }
            site.setZone(zone);
            siteDAO.update(site);
            sites.put(address, site);
            return new Response(true, null);
        } catch (Exception e) {
            return new Response(null, e.getMessage());
        }
    }

    public Response printAllSites() {
        try {
            for (SiteDTO site : siteDAO.findAll()) {
                System.out.println(site);
            }
            return new Response(null, null);
        } catch (Exception e) {
            return new Response(null, e.getMessage());
        }
    }

    public Response getAllSitesSummary() {
        try {
            List<String> summaries = new ArrayList<>();
            for (SiteDTO site : siteDAO.findAll()) {
                summaries.add(String.format(
                        "Site: %s,  Address: %s, Contact: %s (%s), Zone: %s",
                        site.getName(),
                        site.getAddress(),
                        site.getContactName(),
                        site.getPhone(),
                        site.getZone().name()
                ));
            }
            return new Response(summaries, null);
        } catch (Exception e) {
            return new Response(null, e.getMessage());
        }
    }

    public Collection<SiteDTO> getAllSitesRaw() {
        return Collections.unmodifiableCollection(sites.values());
    }

    public HashSet<String> getAllSiteAddresses() {
        return new HashSet<>(sites.keySet());
    }

    public final void loadSitesFromDatabase() {
        try {
            List<SiteDTO> list = siteDAO.findAll();
            sites.clear();
            for (SiteDTO s : list) {
                sites.put(s.getAddress(), s);
            }
        } catch (Exception e) {
            System.err.println("Failed to load sites: " + e.getMessage());
        }
    }
}
