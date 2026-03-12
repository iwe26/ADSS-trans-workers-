package hrmanagement.domain.facade;

import hrmanagement.domain.repository.SiteRepository;
import hrmanagement.dal.dto.SiteDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Facade for site-related business operations.
 */
public class SiteFacade {
    private final SiteRepository repository;

    public SiteFacade(SiteRepository repository) {
        this.repository = repository;
    }

    public void addSite(SiteDTO dto) throws SQLException {
        repository.insert(dto);
    }

    public Optional<SiteDTO> getSiteByAddress(String address) throws SQLException {
        return repository.findByAddress(address);
    }

    public List<SiteDTO> getAllSites() throws SQLException {
        return repository.findAll();
    }

    public void updateSite(SiteDTO dto) throws SQLException {
        repository.update(dto);
    }

    public void removeSite(String address) throws SQLException {
        repository.delete(address);
    }
}
