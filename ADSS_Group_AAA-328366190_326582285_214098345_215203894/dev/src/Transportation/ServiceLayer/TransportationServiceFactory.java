package Transportation.ServiceLayer;

import Transportation.BusinessLayer.BLs.DriverBL;
import Transportation.BusinessLayer.BLs.TruckBL;
import Transportation.DAL.DAO.TransportationDAO;
import Transportation.DAL.DAO.TruckDAO;
import Transportation.DAL.DAO.TruckTimeIntervalsDAO;
import hrmanagement.dal.dao.SiteDAO;
import hrmanagement.service.TransportationHRService;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class TransportationServiceFactory {
    public final TransportationService _transportationService;
    public final DriverService _driverService;
    public  SiteService _siteService;
    public final TruckService _truckService;


    public TransportationServiceFactory(SiteService siteService, TransportationHRService transportationHRService) {
        // Shared in-memory maps
        Map<Integer, DriverBL> driverMap = new HashMap<>();
        Map<String, TruckBL> truckMap = new HashMap<>();

        // Services
        _driverService = new DriverService(driverMap , transportationHRService);
        _truckService = new TruckService(truckMap);
        _siteService = siteService;
        _transportationService = new TransportationService(driverMap, truckMap, _siteService, transportationHRService);
    }
}
