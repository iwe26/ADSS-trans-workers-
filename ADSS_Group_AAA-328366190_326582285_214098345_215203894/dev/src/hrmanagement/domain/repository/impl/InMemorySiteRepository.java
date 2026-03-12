package hrmanagement.domain.repository.impl;
import hrmanagement.domain.repository.SiteRepository;
import hrmanagement.dal.dao.SiteDAO;
import hrmanagement.dal.dto.SiteDTO;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of SiteRepository.
 * Caches SiteDTO instances keyed by address and delegates persistence to SiteDAO.
 */
public class InMemorySiteRepository implements SiteRepository{
    private final SiteDAO dao;
    private final Map<String, SiteDTO> cache = new ConcurrentHashMap<>();

    public InMemorySiteRepository(SiteDAO dao) throws SQLException {
        this.dao = dao;
        List<SiteDTO> all = dao.findAll();
        for (SiteDTO site : all) {
            cache.put(site.getAddress(), site);
        }
    }

    @Override
    public void insert(SiteDTO site) throws SQLException {
        dao.insert(site);
        cache.put(site.getAddress(), site);
    }

    @Override
    public Optional<SiteDTO> findByAddress(String address) throws SQLException {
        SiteDTO site = cache.get(address);
        if (site != null) {
            return Optional.of(site);
        }
        site = dao.findByAddress(address);
        if (site != null) {
            cache.put(address, site);
            return Optional.of(site);
        }
        return Optional.empty();
    }

    @Override
    public List<SiteDTO> findAll() throws SQLException {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void update(SiteDTO site) throws SQLException {
        dao.update(site);
        cache.put(site.getAddress(), site);
    }

    @Override
    public void delete(String address) throws SQLException {
        dao.delete(address);
        cache.remove(address);
    }
}
