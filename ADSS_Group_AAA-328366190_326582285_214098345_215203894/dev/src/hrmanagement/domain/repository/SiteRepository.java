package hrmanagement.domain.repository;
import hrmanagement.dal.dto.SiteDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Site operations.
 */
public interface SiteRepository {
    /**
     * Insert a new site record.
     */
    void insert(SiteDTO site) throws SQLException;

    /**
     * Find a site by its address.
     */
    Optional<SiteDTO> findByAddress(String address) throws SQLException;

    /**
     * Retrieve all sites.
     */
    List<SiteDTO> findAll() throws SQLException;

    /**
     * Update an existing site's details.
     */
    void update(SiteDTO site) throws SQLException;

    /**
     * Delete a site by its address.
     */
    void delete(String address) throws SQLException;
}
